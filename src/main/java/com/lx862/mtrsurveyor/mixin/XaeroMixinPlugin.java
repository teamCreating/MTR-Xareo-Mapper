package com.lx862.mtrsurveyor.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Mixin plugin that conditionally loads Xaero World Map mixins
 * only if the Xaero World Map mod is present on the classpath.
 */
public class XaeroMixinPlugin implements IMixinConfigPlugin {

    private static final String XAERO_WORLD_MAP_CLASS = "xaero.map.gui.GuiMap";
    private boolean xaeroWorldMapPresent;

    @Override
    public void onLoad(String mixinPackage) {
        xaeroWorldMapPresent = getClass().getClassLoader()
                .getResource(XAERO_WORLD_MAP_CLASS.replace('.', '/') + ".class") != null;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Only apply xaero-related mixins if Xaero World Map is present
        if (mixinClassName.contains(".xaero.")) {
            return xaeroWorldMapPresent;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
