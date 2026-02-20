package com.lx862.mtrsurveyor.mixin;

import com.lx862.mtrsurveyor.config.MTRSurveyorConfig;
import com.lx862.mtrsurveyor.integration.XaeroIntegration;
import com.lx862.mtrsurveyor.MTRSurveyor;
import org.mtr.core.data.Data;
import org.mtr.core.simulation.Simulator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Simulator.class, remap = false)
public class MTRSimulatorMixin extends Data {

    @Override
    public void sync() {
        super.sync();
        if (MTRSurveyorConfig.INSTANCE.enabled.get()) {
            MTRSurveyor.LOGGER.debug("[MTRSurveyor] MTR server data synced, requesting waypoint sync");
            XaeroIntegration.requestSync();
        }
    }
}
