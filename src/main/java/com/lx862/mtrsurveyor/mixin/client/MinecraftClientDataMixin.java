package com.lx862.mtrsurveyor.mixin.client;

import com.lx862.mtrsurveyor.MTRSurveyor;
import com.lx862.mtrsurveyor.config.MTRSurveyorConfig;
import com.lx862.mtrsurveyor.integration.XaeroIntegration;
import org.mtr.mod.client.MinecraftClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClientData.class, remap = false)
public class MinecraftClientDataMixin {
    @Inject(method = "sync", at = @At("TAIL"))
    public void onSync(CallbackInfo ci) {
        if (MTRSurveyorConfig.INSTANCE.enabled.get()) {
            MTRSurveyor.LOGGER.debug("[MTRSurveyor] MTR client data synced, requesting waypoint sync");
            XaeroIntegration.requestSync();
        }
    }
}
