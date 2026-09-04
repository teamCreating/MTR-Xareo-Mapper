package com.lx862.mtrsurveyor.network;

import com.lx862.mtrsurveyor.MTRSurveyor;
import com.lx862.mtrsurveyor.config.MTRSurveyorConfig;
import com.lx862.mtrsurveyor.mapdata.MapDataCache;
import com.lx862.mtrsurveyor.mixin.client.ClientCommonListenerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.connection.ConnectionType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side half of the full-network map sync: requests snapshots from the
 * server, reassembles chunked transfers and feeds {@link MapDataCache}.
 *
 * <p>Fully optional - when the server does not run this mod the client falls
 * back to MTR's own radius-limited client data. Requests are only sent over
 * NeoForge connections; payloads are registered as optional so even a
 * NeoForge server without this mod cannot break the client.</p>
 */
@EventBusSubscriber(modid = MTRSurveyor.MOD_ID, value = Dist.CLIENT)
public final class ClientNetworkSync {

    /** Retry a snapshot request this often until the server answers once. */
    private static final long INITIAL_RETRY_MILLIS = 30_000;
    /** After the first successful sync, refresh at least this often while playing. */
    private static final long MIN_REFRESH_MILLIS = 60_000;

    private static final Map<Integer, TransferBuffer> transfers = new ConcurrentHashMap<>();
    private static long lastRequestMillis = 0;
    private static long lastSuccessfulSyncMillis = 0;
    private static int SCREEN_TRACE_TIMER = 0;
    /** Flips to true when a server answered at least once; flips back on world change. */
    private static boolean serverHasSupport = false;
    /** Set when the connected server clearly has no support, to back off requests. */
    private static long serverUnsupportedBackoffUntil = 0;

    private ClientNetworkSync() {
    }

    public static void requestSync(String trigger) {
        if (!MTRSurveyorConfig.INSTANCE.networkSyncEnabled.get()) {
            return;
        }
        final Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null || mc.player == null) {
            return;
        }
        if (System.currentTimeMillis() < serverUnsupportedBackoffUntil) {
            return;
        }
        try {
            if (!(mc.getConnection() instanceof ClientCommonListenerAccessor accessor)
                    || accessor.mtrsurveyor$getConnectionType() != ConnectionType.NEOFORGE) {
                // Vanilla/Forge server - this mod cannot be installed there, so
                // fall back to client-only data without poking the connection.
                markServerUnsupported();
                return;
            }
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(RequestNetworkSync.INSTANCE);
            lastRequestMillis = System.currentTimeMillis();
            if (MTRSurveyorConfig.INSTANCE.debugLog.get()) {
                MTRSurveyor.LOGGER.info("[MTRSurveyor] Requested full-network snapshot ({})", trigger);
            }
        } catch (Throwable e) {
            // Payload rejected - back off for a while.
            MTRSurveyor.LOGGER.debug("[MTRSurveyor] Snapshot request failed (server lacks the mod?): {}", e.getMessage());
            markServerUnsupported();
        }
    }

    static void onChunkReceived(NetworkSyncChunk chunk) {
        // Ignore stale transfers.
        transfers.values().removeIf(t -> t.ageMillis() > 120_000);

        final TransferBuffer buffer = transfers.computeIfAbsent(chunk.transferId(), id -> new TransferBuffer());
        if (buffer.done) {
            return;
        }
        buffer.store(chunk.chunkIndex(), chunk);
        if (buffer.receivedCount < chunk.totalChunks()) {
            return;
        }

        // All chunks present - reassemble.
        buffer.done = true;
        transfers.remove(chunk.transferId());
        try {
            final byte[] payload = buffer.assemble();
            final List<MapDataCache.DimensionData> dimensions =
                    NetworkSyncChunk.readDimensionList(NetworkSyncChunk.wrapForRead(payload));

            boolean firstOnServer = !serverHasSupport;
            serverHasSupport = true;
            lastSuccessfulSyncMillis = System.currentTimeMillis();
            for (MapDataCache.DimensionData dimension : dimensions) {
                MapDataCache.putServerData(dimension.dimensionId, dimension);
                MTRSurveyor.LOGGER.info(
                        "[MTRSurveyor] Full-network snapshot applied for {}: {} routes, {} track polylines",
                        dimension.dimensionId, dimension.routes.size(), dimension.tracks.size());
            }
            if (firstOnServer) {
                showActionbar("Full-network map sync active");
            }
        } catch (IOException e) {
            MTRSurveyor.LOGGER.error("[MTRSurveyor] Failed to decode network snapshot", e);
        }
    }

    private static void markServerUnsupported() {
        serverUnsupportedBackoffUntil = System.currentTimeMillis() + 10 * 60_000;
    }

    private static void showActionbar(String message) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal("[MTR] " + message), true);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Events & ticking
    // -----------------------------------------------------------------------------------------------------------------

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null || mc.player == null) {
            return;
        }

        // Diagnostics: periodic screen-state trace (debugLog only), useful for
        // checking which GUI the map layer should render into.
        if (MTRSurveyorConfig.INSTANCE.debugLog.get()
                && (SCREEN_TRACE_TIMER++ % 60) == 0) {
            MTRSurveyor.LOGGER.info("[MTRSurveyor] screen-trace: {}", mc.screen);
        }

        if (!MTRSurveyorConfig.INSTANCE.networkSyncEnabled.get()) {
            return;
        }

        final long now = System.currentTimeMillis();
        if (serverHasSupport) {
            // Known-good server: periodic refresh.
            final long interval = Math.max(MIN_REFRESH_MILLIS,
                    MTRSurveyorConfig.INSTANCE.networkSyncIntervalSeconds.get() * 1000L);
            if (now - lastSuccessfulSyncMillis >= interval) {
                requestSync("periodic refresh");
            }
        } else if (now - lastRequestMillis >= INITIAL_RETRY_MILLIS
                && now >= serverUnsupportedBackoffUntil) {
            // Not yet answered: retry until the server proves it has the mod.
            requestSync("initial handshake retry");
        }
    }

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        // Fresh world/connection: reset sync state and ask for a snapshot as
        // soon as the server is ready to answer.
        MapDataCache.clearServerData();
        transfers.clear();
        serverHasSupport = false;
        serverUnsupportedBackoffUntil = 0;
        lastSuccessfulSyncMillis = 0;
        lastRequestMillis = System.currentTimeMillis() - INITIAL_RETRY_MILLIS
                + 3_000; // first attempt ~3 seconds after login
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        MapDataCache.clearServerData();
        transfers.clear();
        serverHasSupport = false;
        serverUnsupportedBackoffUntil = 0;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Reassembly buffer
    // -----------------------------------------------------------------------------------------------------------------

    private static final class TransferBuffer {

        private final Map<Short, NetworkSyncChunk> chunks = new ConcurrentHashMap<>();
        private final long createdMillis = System.currentTimeMillis();
        private volatile boolean done;
        private volatile int receivedCount;

        void store(short index, NetworkSyncChunk chunk) {
            if (chunks.put(index, chunk) == null) {
                receivedCount++;
            }
        }

        long ageMillis() {
            return System.currentTimeMillis() - createdMillis;
        }

        byte[] assemble() {
            final short total = chunks.values().iterator().next().totalChunks();
            int size = 0;
            for (short i = 0; i < total; i++) {
                size += chunks.get(i).data().length;
            }
            final byte[] payload = new byte[size];
            int offset = 0;
            for (short i = 0; i < total; i++) {
                final byte[] part = chunks.get(i).data();
                System.arraycopy(part, 0, payload, offset, part.length);
                offset += part.length;
            }
            return payload;
        }
    }
}
