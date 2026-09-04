/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.Button$OnPress
 *  xaero.lib.client.gui.widget.Tooltip
 */
package xaero.map.gui;

import net.minecraft.client.gui.components.Button;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.map.WorldMap;
import xaero.map.gui.GuiTexturedButton;

public class GuiMapSwitchingButton
extends GuiTexturedButton {
    public static final Tooltip TOOLTIP = new Tooltip("gui.xaero_box_map_switching");

    public GuiMapSwitchingButton(boolean menuActive, int x, int y, Button.OnPress onPress) {
        super(x, y, 20, 20, menuActive ? 97 : 81, 0, 16, 16, WorldMap.guiTextures, onPress, () -> TOOLTIP);
    }
}

