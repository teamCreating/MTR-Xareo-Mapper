package com.lx862.mtrjourneymap.mixin;

import com.lx862.mtrjourneymap.MTRDataSummary;
import com.lx862.mtrjourneymap.config.MTRSurveyorConfig;
import com.lx862.mtrjourneymap.landmark.MTRLandmarkManager;
import com.lx862.mtrjourneymap.MTRSurveyor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.mtr.core.data.Data;
import org.mtr.core.simulation.Simulator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Simulator.class, remap = false)
public class MTRSimulatorMixin extends Data {
    @Shadow
    @Final
    public String dimension;

    @Override
    public void sync() {
        super.sync();
        if (MTRSurveyorConfig.INSTANCE.enabled) {
            MTRLandmarkManager.SyncOrigin syncOrigin = MTRLandmarkManager.SyncOrigin.ofServer("MTR Data Changed");
            // dimension is in format e.g. minecraft/overworld
            String[] dimSplit = dimension.split("/");
            String dimensionNamespace = dimSplit[0];
            String dimensionPath = dimSplit[1];
            ResourceLocation dimensionId = new ResourceLocation(dimensionNamespace, dimensionPath);
            MinecraftServer server = MTRSurveyor.getServerInstance();
            MTRDataSummary dataSummary = MTRDataSummary.of(this);
            server.execute(() -> {
                Level world = server.getLevel(ResourceKey.create(Registries.DIMENSION, dimensionId));
                MTRLandmarkManager.syncLandmarks(syncOrigin, world, dataSummary, MTRSurveyorConfig.INSTANCE);
            });
        }
    }
}
