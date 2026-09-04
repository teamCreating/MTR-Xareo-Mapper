package com.lx862.mtrsurveyor.mixin.client.xaero;

import com.lx862.mtrsurveyor.MTRSurveyor;
import com.lx862.mtrsurveyor.integration.xaero.XaeroRouteRenderer;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.gui.GuiMap;

/**
 * Mixin into Xaero's World Map render method to draw the MTR path layer
 * (route lines & track geometry) on the map.
 * Modelled after Create mod's XaeroFullscreenMapMixin.
 */
@Mixin(value = GuiMap.class)
public abstract class XaeroWorldMapMixin {

    @Unique
    private boolean mtrsurveyor$failedToRender = false;

    @Inject(method = "m_88315_(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;m_280218_(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), remap = false, require = 0)
    public void mtrsurveyor$onRenderTail(GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
            CallbackInfo ci) {
        try {
            if (!this.mtrsurveyor$failedToRender) {
                XaeroRouteRenderer.onRender(graphics, (GuiMap) (Object) this, mouseX, mouseY, partialTick);
            }
        } catch (Throwable e) {
            MTRSurveyor.LOGGER.error("[MTRSurveyor] Failed to render the MTR path layer on Xaero's World Map - "
                    + "the layer is disabled for this session (possibly an incompatible Xaero's World Map version):", e);
            this.mtrsurveyor$failedToRender = true;
        }
    }

    @Inject(method = "m_6375_(DDI)Z", at = @At("HEAD"), remap = false, cancellable = true, require = 0)
    public void mtrsurveyor$onMouseClicked(double mouseX, double mouseY, int button,
            CallbackInfoReturnable<Boolean> cir) {
        try {
            if (XaeroRouteRenderer.onMouseClicked(mouseX, mouseY, button)) {
                cir.setReturnValue(true);
            }
        } catch (Throwable e) {
            MTRSurveyor.LOGGER.error("[MTRSurveyor] Failed to handle mouse click on Xaero World Map:", e);
        }
    }
}
