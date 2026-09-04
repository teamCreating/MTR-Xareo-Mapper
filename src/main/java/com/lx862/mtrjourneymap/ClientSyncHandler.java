package com.lx862.mtrjourneymap;

import com.lx862.mtrjourneymap.config.MTRSurveyorConfig;
import com.lx862.mtrjourneymap.landmark.MTRLandmarkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.mtr.mod.client.MinecraftClientData;

/**
 * Defers landmark syncs to the client tick loop, mirroring how the Xaero
 * edition schedules its waypoint syncs.
 *
 * <p>MTR's {@link MinecraftClientData#sync()} and the JourneyMap API are not
 * guaranteed to be ready at the same moment (and may even run off the client
 * thread), so sync requests are queued here and retried every
 * {@link #SYNC_INTERVAL_TICKS} ticks until they succeed.</p>
 */
public class ClientSyncHandler {

    public static volatile boolean needsSync = false;
    private static int tickCounter = 0;
    private static final int SYNC_INTERVAL_TICKS = 100; // 5 seconds
    private static ResourceLocation lastDimension = null;

    /**
     * Called every client tick. Performs a pending sync once MTR data and the
     * JourneyMap API are ready.
     */
    public static void onClientTick() {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        // Re-sync when the player changes dimension so markers for the new
        // dimension are created
        ResourceLocation dimension = world.dimension().location();
        if (!dimension.equals(lastDimension)) {
            lastDimension = dimension;
            requestSync();
        }

        if (!needsSync) {
            return;
        }
        if (!MTRSurveyorConfig.INSTANCE.enabled) {
            return;
        }

        tickCounter++;
        if (tickCounter < SYNC_INTERVAL_TICKS) {
            return;
        }
        tickCounter = 0;

        try {
            MinecraftClientData clientData = MinecraftClientData.getInstance();
            if (clientData == null) {
                MTRSurveyor.LOGGER.debug("[{}] MTR client data not available yet, will retry...",
                        MTRSurveyor.MOD_NAME);
                return;
            }

            MTRDataSummary dataSummary = MTRDataSummary.of(clientData);
            MTRLandmarkManager.syncLandmarks(MTRLandmarkManager.SyncOrigin.ofClient("scheduled sync"), world,
                    dataSummary, MTRSurveyorConfig.INSTANCE);
            needsSync = false;
        } catch (Throwable e) {
            MTRSurveyor.LOGGER.error("[{}] Error during landmark sync tick", MTRSurveyor.MOD_NAME, e);
            needsSync = false;
        }
    }

    /**
     * Mark that a landmark sync is needed (called from mixins and commands).
     */
    public static void requestSync() {
        needsSync = true;
        tickCounter = SYNC_INTERVAL_TICKS - 1; // Try on next tick cycle
    }
}
