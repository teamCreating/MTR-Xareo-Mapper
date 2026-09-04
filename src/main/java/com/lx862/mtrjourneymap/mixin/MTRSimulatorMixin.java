package com.lx862.mtrjourneymap.mixin;

import org.mtr.core.data.Data;
import org.mtr.core.simulation.Simulator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Simulator.class, remap = false)
public class MTRSimulatorMixin extends Data {

    @Override
    public void sync() {
        super.sync();
        // MTR JourneyMap Integration is a client-side mapper. Markers are rebuilt
        // on the client from MinecraftClientData, so we must not touch the
        // JourneyMap API from the server: the API does not exist on dedicated
        // servers, and players also connect to servers that don't have this mod
        // installed.
    }
}
