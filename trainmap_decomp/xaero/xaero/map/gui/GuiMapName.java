/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  xaero.lib.client.gui.widget.MySmallButton
 */
package xaero.map.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.lib.client.gui.widget.MySmallButton;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.gui.ScreenBase;
import xaero.map.world.MapDimension;

public class GuiMapName
extends ScreenBase {
    private EditBox nameTextField;
    private MapDimension mapDimension;
    private String editingMWId;
    private String currentNameFieldContent;
    private MapProcessor mapProcessor;
    private Button confirmButton;

    public GuiMapName(MapProcessor mapProcessor, Screen par1GuiScreen, Screen escape, MapDimension mapDimension, String editingMWId) {
        super(par1GuiScreen, escape, (Component)Component.m_237115_((String)"gui.xaero_map_name"));
        this.mapDimension = mapDimension;
        this.editingMWId = editingMWId;
        this.currentNameFieldContent = editingMWId == null ? "" : mapDimension.getMultiworldName(editingMWId);
        this.mapProcessor = mapProcessor;
        this.canSkipWorldRender = true;
    }

    public void m_7856_() {
        super.m_7856_();
        if (this.nameTextField != null) {
            this.currentNameFieldContent = this.nameTextField.m_94155_();
        }
        this.nameTextField = new EditBox(this.f_96547_, this.f_96543_ / 2 - 100, 60, 200, 20, (Component)Component.m_237115_((String)"gui.xaero_map_name"));
        this.nameTextField.m_94144_(this.currentNameFieldContent);
        this.m_7522_((GuiEventListener)this.nameTextField);
        this.m_142416_((GuiEventListener)this.nameTextField);
        this.confirmButton = new MySmallButton(0, this.f_96543_ / 2 - 155, this.f_96544_ / 6 + 168, (Component)Component.m_237110_((String)"gui.xaero_confirm", (Object[])new Object[0]), b -> {
            if (this.canConfirm()) {
                Object object = this.mapProcessor.uiSync;
                synchronized (object) {
                    if (this.mapProcessor.getMapWorld() == this.mapDimension.getMapWorld()) {
                        Object mwIdFixed;
                        String unfilteredName = this.nameTextField.m_94155_();
                        if (this.editingMWId == null) {
                            Object mwId = unfilteredName.toLowerCase().replaceAll("[^a-z0-9]+", "");
                            if (((String)mwId).isEmpty()) {
                                mwId = "map";
                            }
                            mwId = "cm$" + (String)mwId;
                            boolean mwAdded = false;
                            mwIdFixed = mwId;
                            int fix = 1;
                            while (!mwAdded) {
                                mwAdded = this.mapDimension.addMultiworldChecked((String)mwIdFixed);
                                if (mwAdded) continue;
                                mwIdFixed = (String)mwId + ++fix;
                            }
                            Path dimensionFolderPath = this.mapDimension.getMainFolderPath();
                            Path multiworldFolderPath = dimensionFolderPath.resolve((String)mwIdFixed);
                            try {
                                Files.createDirectories(multiworldFolderPath, new FileAttribute[0]);
                            }
                            catch (IOException e) {
                                WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                            }
                            this.mapDimension.setMultiworldUnsynced((String)mwIdFixed);
                        } else {
                            mwIdFixed = this.editingMWId;
                        }
                        this.mapDimension.setMultiworldName((String)mwIdFixed, unfilteredName);
                        this.mapDimension.saveConfigUnsynced();
                        this.goBack();
                    }
                }
            }
        });
        this.m_142416_((GuiEventListener)this.confirmButton);
        this.m_142416_((GuiEventListener)new MySmallButton(1, this.f_96543_ / 2 + 5, this.f_96544_ / 6 + 168, (Component)Component.m_237110_((String)"gui.xaero_cancel", (Object[])new Object[0]), b -> this.goBack()));
        this.updateConfirmButton();
    }

    private boolean canConfirm() {
        return this.nameTextField.m_94155_().length() > 0;
    }

    private void updateConfirmButton() {
        this.confirmButton.f_93623_ = this.canConfirm();
    }

    public boolean m_7933_(int par1, int par2, int par3) {
        boolean result = super.m_7933_(par1, par2, par3);
        if (par1 == 257 && this.canConfirm()) {
            this.confirmButton.m_5716_(0.0, 0.0);
            return true;
        }
        return result;
    }

    public void m_86600_() {
        this.updateConfirmButton();
        this.nameTextField.m_94120_();
    }

    public void m_88315_(GuiGraphics guiGraphics, int par1, int par2, float par3) {
        this.renderEscapeScreen(guiGraphics, par1, par2, par3);
        this.m_280273_(guiGraphics);
        guiGraphics.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, 20, 0xFFFFFF);
        this.nameTextField.m_88315_(guiGraphics, par1, par2, par3);
        super.m_88315_(guiGraphics, par1, par2, par3);
    }
}

