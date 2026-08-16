package com.lx862.mtrjourneymap.mixin.client;

import com.lx862.mtrjourneymap.MTRDataSummary;
import com.lx862.mtrjourneymap.config.MTRSurveyorConfig;
import com.lx862.mtrjourneymap.landmark.MTRLandmarkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.mtr.mod.client.MinecraftClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClientData.class, remap = false)
public class MinecraftClientDataMixin {
    @Inject(method = "sync", at = @At("TAIL"))
    public void onSync(CallbackInfo ci) {
        Level world = Minecraft.getInstance().level;
        if (world == null)
            return;
        MTRLandmarkManager.SyncOrigin syncOrigin = MTRLandmarkManager.SyncOrigin.ofClient("MTR Data Changed");
        MTRDataSummary mtrDataSummary = MTRDataSummary.of((MinecraftClientData) (Object) this);
        MTRLandmarkManager.syncLandmarks(syncOrigin, world, mtrDataSummary, MTRSurveyorConfig.INSTANCE);
    }
}
