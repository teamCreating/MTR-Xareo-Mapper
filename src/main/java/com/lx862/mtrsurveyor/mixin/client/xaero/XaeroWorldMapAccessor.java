package com.lx862.mtrsurveyor.mixin.client.xaero;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import xaero.map.gui.GuiMap;

/**
 * Accessor mixin for Xaero's World Map GuiMap to read camera position and
 * scale.
 */
@Mixin(value = GuiMap.class, remap = false)
public interface XaeroWorldMapAccessor {

    @Accessor
    double getCameraX();

    @Accessor
    double getCameraZ();

    @Accessor
    double getScale();
}
