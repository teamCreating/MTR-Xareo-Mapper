/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  xaero.lib.client.config.ClientConfigManager
 *  xaero.lib.client.gui.widget.MySmallButton
 *  xaero.lib.common.config.util.ConfigUtils
 */
package xaero.map.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.gui.widget.MySmallButton;
import xaero.lib.common.config.util.ConfigUtils;
import xaero.map.WorldMap;
import xaero.map.WorldMapSession;
import xaero.map.common.config.option.WorldMapProfiledConfigOptions;
import xaero.map.gui.ScreenBase;
import xaero.map.mods.SupportMods;
import xaero.map.world.MapWorld;

public class GuiMapTpCommand
extends ScreenBase {
    private MySmallButton confirmButton;
    private EditBox commandFormatTextField;
    private EditBox dimensionCommandFormatTextField;
    private boolean usingDefault;
    private String commandFormat;
    private String dimensionCommandFormat;
    private Component waypointCommandHint = Component.m_237115_((String)"gui.xaero_wm_teleport_command_waypoints_hint");

    public GuiMapTpCommand(Screen parent, Screen escape) {
        super(parent, escape, (Component)Component.m_237115_((String)"gui.xaero_wm_teleport_command"));
        WorldMapSession session = WorldMapSession.getCurrentSession();
        MapWorld mapWorld = session.getMapProcessor().getMapWorld();
        this.usingDefault = mapWorld.isUsingDefaultMapTeleport();
        this.commandFormat = mapWorld.getTeleportCommandFormat();
        this.dimensionCommandFormat = mapWorld.getDimensionTeleportCommandFormat();
        this.canSkipWorldRender = true;
    }

    public void m_7856_() {
        super.m_7856_();
        WorldMapSession session = WorldMapSession.getCurrentSession();
        MapWorld mapWorld = session.getMapProcessor().getMapWorld();
        ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        String defaultMapTeleportFormat = (String)configManager.getEffective(WorldMapProfiledConfigOptions.DEFAULT_MAP_TELEPORT_FORMAT);
        String defaultMapTeleportDimensionFormat = (String)configManager.getEffective(WorldMapProfiledConfigOptions.DEFAULT_MAP_TELEPORT_DIMENSION_FORMAT);
        this.commandFormatTextField = new EditBox(this.f_96547_, this.f_96543_ / 2 - 100, this.f_96544_ / 7 + 60, 200, 20, (Component)Component.m_237115_((String)"gui.xaero_wm_teleport_command"));
        this.commandFormatTextField.m_94199_(500);
        this.commandFormatTextField.m_94144_(this.usingDefault ? defaultMapTeleportFormat : this.commandFormat);
        this.dimensionCommandFormatTextField = new EditBox(this.f_96547_, this.f_96543_ / 2 - 100, this.f_96544_ / 7 + 90, 200, 20, (Component)Component.m_237115_((String)"gui.xaero_wm_dimension_teleport_command"));
        this.dimensionCommandFormatTextField.m_94199_(500);
        this.dimensionCommandFormatTextField.m_94144_(this.usingDefault ? defaultMapTeleportDimensionFormat : this.dimensionCommandFormat);
        if (this.usingDefault) {
            this.dimensionCommandFormatTextField.f_93623_ = false;
            this.commandFormatTextField.f_93623_ = false;
            this.commandFormatTextField.m_94202_(-11184811);
            this.dimensionCommandFormatTextField.m_94202_(-11184811);
        } else {
            this.commandFormatTextField.m_94151_(text -> {
                this.commandFormat = text;
            });
            this.dimensionCommandFormatTextField.m_94151_(text -> {
                this.dimensionCommandFormat = text;
            });
        }
        this.m_7787_((GuiEventListener)this.commandFormatTextField);
        this.m_7787_((GuiEventListener)this.dimensionCommandFormatTextField);
        if (SupportMods.minimap()) {
            this.m_142416_((GuiEventListener)new MySmallButton(0, this.f_96543_ / 2 - 75, this.f_96544_ / 7 + 138, (Component)Component.m_237115_((String)"gui.xaero_wm_teleport_command_waypoints"), b -> SupportMods.xaeroMinimap.openWaypointWorldTeleportCommandScreen((Screen)this, this.escape)));
        }
        this.confirmButton = new MySmallButton(1, this.f_96543_ / 2 - 155, this.f_96544_ / 6 + 168, (Component)Component.m_237115_((String)"gui.xaero_confirm"), b -> {
            if (this.canConfirm()) {
                if (!this.usingDefault && this.commandFormat.equals(defaultMapTeleportFormat) && this.dimensionCommandFormat.equals(defaultMapTeleportDimensionFormat)) {
                    this.usingDefault = true;
                }
                mapWorld.setTeleportCommandFormat(this.commandFormat);
                mapWorld.setDimensionTeleportCommandFormat(this.dimensionCommandFormat);
                mapWorld.setUseDefaultMapTeleport(this.usingDefault);
                mapWorld.saveConfig();
                this.goBack();
            }
        });
        this.m_142416_((GuiEventListener)this.confirmButton);
        this.m_142416_((GuiEventListener)new MySmallButton(2, this.f_96543_ / 2 + 5, this.f_96544_ / 6 + 168, (Component)Component.m_237115_((String)"gui.xaero_cancel"), b -> this.goBack()));
        this.m_142416_((GuiEventListener)new MySmallButton(202, this.f_96543_ / 2 - 75, this.f_96544_ / 7 + 20, (Component)CommonComponents.m_178393_((Component)Component.m_237115_((String)"gui.xaero_wm_use_default"), (Component)ConfigUtils.getDisplayForBoolean(null, (Boolean)this.usingDefault)), b -> {
            this.usingDefault = !this.usingDefault;
            this.m_6575_(this.f_96541_, this.f_96543_, this.f_96544_);
        }));
    }

    public void m_88315_(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        this.renderEscapeScreen(guiGraphics, mouseX, mouseY, partial);
        this.m_280273_(guiGraphics);
        guiGraphics.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, 20, 0xFFFFFF);
        if (SupportMods.minimap()) {
            guiGraphics.m_280653_(this.f_96547_, this.waypointCommandHint, this.f_96543_ / 2, this.f_96544_ / 7 + 124, -5592406);
        }
        guiGraphics.m_280137_(this.f_96547_, "{x} {y} {z} {d}", this.f_96543_ / 2, this.f_96544_ / 7 + 46, -5592406);
        super.m_88315_(guiGraphics, mouseX, mouseY, partial);
        this.commandFormatTextField.m_88315_(guiGraphics, mouseX, mouseY, partial);
        this.dimensionCommandFormatTextField.m_88315_(guiGraphics, mouseX, mouseY, partial);
    }

    private boolean canConfirm() {
        return this.commandFormat != null && this.commandFormat.length() > 0 && this.dimensionCommandFormat != null && this.dimensionCommandFormat.length() > 0;
    }

    public void m_86600_() {
        super.m_86600_();
        this.commandFormatTextField.m_94120_();
        this.dimensionCommandFormatTextField.m_94120_();
        this.confirmButton.f_93623_ = this.canConfirm();
    }

    public boolean m_7933_(int par1, int par2, int par3) {
        if (par1 == 257 && this.commandFormat != null && this.commandFormat.length() > 0) {
            this.confirmButton.m_5716_(0.0, 0.0);
        }
        return super.m_7933_(par1, par2, par3);
    }
}

