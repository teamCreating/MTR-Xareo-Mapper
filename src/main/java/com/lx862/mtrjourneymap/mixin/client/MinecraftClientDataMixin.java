package com.lx862.mtrjourneymap.mixin.client;

import com.lx862.mtrjourneymap.ClientSyncHandler;
import com.lx862.mtrjourneymap.MTRSurveyor;
import com.lx862.mtrjourneymap.config.MTRSurveyorConfig;
import org.mtr.mod.client.MinecraftClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClientData.class, remap = false)
public class MinecraftClientDataMixin {
    @Inject(method = "sync", at = @At("TAIL"))
    public void onSync(CallbackInfo ci) {
        if (MTRSurveyorConfig.INSTANCE.enabled) {
            MTRSurveyor.LOGGER.debug("[{}] MTR client data synced, requesting landmark sync",
                    MTRSurveyor.MOD_NAME);
            ClientSyncHandler.requestSync();
        }
    }
}
