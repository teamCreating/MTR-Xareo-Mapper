package com.lx862.mtrsurveyor.network;

import com.lx862.mtrsurveyor.MTRSurveyor;
import com.lx862.mtrsurveyor.mapdata.MapRoute;
import com.lx862.mtrsurveyor.mapdata.MapTrack;
import com.lx862.mtrsurveyor.mapdata.TrackSampler;
import com.lx862.mtrsurveyor.mixin.MainAccessorMixin;
import com.lx862.mtrsurveyor.mixin.MTRAccessorMixin;
import org.mtr.core.Main;
import org.mtr.core.data.Position;
import org.mtr.core.data.Route;
import org.mtr.core.data.RoutePlatformData;
import org.mtr.core.simulation.Simulator;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Server-side collector for the full-network map snapshot.
 *
 * <p>Walks every MTR {@link Simulator} (one per dimension) and copies routes
 * and sampled track geometry into a compact binary payload, which is streamed
 * to the requesting client in chunks. Collection runs on each simulator's own
 * thread via {@link Simulator#run(Runnable)} so it can never race MTR's
 * simulation; the resulting packets are sent on the server thread.</p>
 */
public final class ServerNetworkCollector {

    /** Max bytes of payload per S2C chunk (well under the 1 MiB custom payload limit). */
    private static final int CHUNK_SIZE = 200_000;

    private ServerNetworkCollector() {
    }

    public static void collectAndSend(ServerPlayer player) {
        final Main main = MTRAccessorMixin.getMain();
        if (main == null) {
            return;
        }
        final ObjectImmutableList<Simulator> simulators = ((MainAccessorMixin) main).getSimulators();
        if (simulators == null || simulators.isEmpty()) {
            return;
        }

        final MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        final long requestTime = System.currentTimeMillis();
        for (int i = 0; i < simulators.size(); i++) {
            final Simulator simulator = simulators.get(i);
            final int dimensionIndex = i;
            // Queue the read onto the simulator thread, then hop back to the
            // server thread to send the packets.
            simulator.run(() -> {
                final NetworkSyncChunk.PendingDimension dimension = collect(simulator, requestTime);
                server.execute(() -> {
                    if (player.connection != null) {
                        send(player, dimension, requestTime, dimensionIndex);
                    }
                });
            });
        }
    }

    private static NetworkSyncChunk.PendingDimension collect(Simulator simulator, long requestTime) {
        final NetworkSyncChunk.PendingDimension result = new NetworkSyncChunk.PendingDimension(simulator.dimension);

        // Routes: stop order, colors and names straight from the authoritative dataset.
        simulator.routes.forEach(route -> {
            try {
                final List<RoutePlatformData> routePlatforms = route.getRoutePlatforms();
                if (routePlatforms == null || routePlatforms.size() < 2) {
                    return;
                }
                final List<MapRoute.Stop> stops = new ArrayList<>(routePlatforms.size());
                for (RoutePlatformData routePlatform : routePlatforms) {
                    final org.mtr.core.data.Platform platform = routePlatform.getPlatform();
                    if (platform == null) {
                        return; // unresolved platform - skip this route entirely
                    }
                    final Position pos = platform.getMidPosition();
                    stops.add(new MapRoute.Stop(pos.getX(), pos.getZ(),
                            platform.getStationName(), routePlatform.getCustomDestination()));
                }
                final boolean circular = route.getCircularState() == Route.CircularState.CLOCKWISE
                        || route.getCircularState() == Route.CircularState.ANTICLOCKWISE;
                result.routes.add(new MapRoute(route.getName(), route.getColor(), circular, stops));
            } catch (Throwable e) {
                MTRSurveyor.LOGGER.debug("[MTRSurveyor] Failed to collect route on server: {}", e.getMessage());
            }
        });

        // Tracks: sample the real rail geometry.
        simulator.rails.forEach(rail -> {
            final List<double[]> points = TrackSampler.sample(rail);
            if (points != null) {
                result.tracks.add(new MapTrack(points));
            }
        });

        return result;
    }

    private static void send(ServerPlayer player, NetworkSyncChunk.PendingDimension dimension, long requestTime,
            int dimensionIndex) {
        try {
            final ByteArrayOutputStream byteOut = new ByteArrayOutputStream(1 << 16);
            final DataOutputStream dataOut = new DataOutputStream(byteOut);
            NetworkSyncChunk.writeDimensionList(dataOut, List.of(dimension));
            dataOut.flush();
            final byte[] payload = byteOut.toByteArray();

            final int totalChunks = (int) Math.max(1, Math.ceil((double) payload.length / CHUNK_SIZE));
            final int transferId = (int) (requestTime ^ (31 * payload.length) ^ (dimensionIndex * 1_000_003))
                    ^ player.getUUID().hashCode();
            for (short chunk = 0; chunk < totalChunks; chunk++) {
                final int from = chunk * CHUNK_SIZE;
                final int to = Math.min(payload.length, from + CHUNK_SIZE);
                final byte[] slice = new byte[to - from];
                System.arraycopy(payload, from, slice, 0, slice.length);
                MTRNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                        new NetworkSyncChunk(transferId, chunk, (short) totalChunks, slice));
            }

            MTRSurveyor.LOGGER.info("[MTRSurveyor] Sent full-network snapshot for {} to {} ({} routes, {} rails, {} bytes, {} chunk(s))",
                    dimension.dimensionId, player.getGameProfile().getName(),
                    dimension.routes.size(), dimension.tracks.size(), payload.length, totalChunks);
        } catch (Throwable e) {
            MTRSurveyor.LOGGER.error("[MTRSurveyor] Failed to send network snapshot for {}: {}",
                    dimension.dimensionId, e.getMessage(), e);
        }
    }
}
