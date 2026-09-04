/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.ConfirmScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  xaero.lib.client.gui.widget.Tooltip
 *  xaero.lib.client.gui.widget.dropdown.DropDownWidget
 *  xaero.lib.client.gui.widget.dropdown.DropDownWidget$Builder
 *  xaero.lib.client.gui.widget.dropdown.IDropDownContainer
 *  xaero.lib.common.util.KeySortableByOther
 */
package xaero.map.gui;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.client.gui.widget.dropdown.DropDownWidget;
import xaero.lib.client.gui.widget.dropdown.IDropDownContainer;
import xaero.lib.common.util.KeySortableByOther;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.config.util.WorldMapClientConfigUtils;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.gui.GuiDimensionOptions;
import xaero.map.gui.GuiMap;
import xaero.map.gui.GuiMapName;
import xaero.map.gui.GuiMapSwitchingButton;
import xaero.map.gui.TooltipButton;
import xaero.map.world.MapConnectionManager;
import xaero.map.world.MapConnectionNode;
import xaero.map.world.MapDimension;

public class GuiMapSwitching {
    private static final Component CONNECT_MAP = Component.m_237115_((String)"gui.xaero_connect_map");
    private static final Component DISCONNECT_MAP = Component.m_237115_((String)"gui.xaero_disconnect_map");
    private MapProcessor mapProcessor;
    private MapDimension settingsDimension;
    private String[] mwDropdownValues;
    private DropDownWidget createdDimensionDropdown;
    private DropDownWidget createdMapDropdown;
    private Button switchingButton;
    private Button multiworldTypeOptionButton;
    private Button renameButton;
    private Button connectButton;
    private Button deleteButton;
    private Button confirmButton;
    private Tooltip serverSelectionModeBox = new Tooltip("gui.xaero_mw_server_box");
    private Tooltip mapSelectionBox = new Tooltip("gui.xaero_map_selection_box");
    public boolean active;
    private boolean writableOnInit;
    private boolean uiPausedOnUpdate;
    private boolean mapSwitchingAllowed;

    public GuiMapSwitching(MapProcessor mapProcessor) {
        this.mapProcessor = mapProcessor;
        this.mapSelectionBox.setStartWidth(200);
        this.serverSelectionModeBox.setStartWidth(200);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init(GuiMap mapScreen, Minecraft minecraft, int width, int height) {
        boolean dimensionDDWasOpen = this.createdDimensionDropdown != null && !this.createdDimensionDropdown.isClosed();
        boolean mapDDWasOpen = this.createdMapDropdown != null && !this.createdMapDropdown.isClosed();
        this.createdDimensionDropdown = null;
        this.createdMapDropdown = null;
        this.switchingButton = null;
        this.multiworldTypeOptionButton = null;
        this.renameButton = null;
        this.deleteButton = null;
        this.confirmButton = null;
        this.settingsDimension = this.mapProcessor.getMapWorld().getFutureDimension();
        this.mapSwitchingAllowed = this.settingsDimension != null;
        Object object = this.mapProcessor.uiPauseSync;
        synchronized (object) {
            this.uiPausedOnUpdate = this.isUIPaused();
            this.switchingButton = new GuiMapSwitchingButton(this.active, 0, height - 20, b -> {
                Object object = this.mapProcessor.uiPauseSync;
                synchronized (object) {
                    if (!this.canToggleThisScreen()) {
                        return;
                    }
                    this.active = !this.active;
                    mapScreen.m_6575_(minecraft, width, height);
                    mapScreen.m_7522_((GuiEventListener)this.switchingButton);
                }
            });
            mapScreen.addButton(this.switchingButton);
            if (this.mapSwitchingAllowed) {
                this.writableOnInit = this.settingsDimension.futureMultiworldWritable;
                if (this.active) {
                    this.createdDimensionDropdown = this.createDimensionDropdown(this.uiPausedOnUpdate, width, mapScreen, minecraft);
                    this.createdMapDropdown = this.createMapDropdown(this.uiPausedOnUpdate, width, mapScreen, minecraft);
                    mapScreen.m_7787_(this.createdDimensionDropdown);
                    mapScreen.m_7787_(this.createdMapDropdown);
                    if (dimensionDDWasOpen) {
                        this.createdDimensionDropdown.setClosed(false);
                    }
                    if (mapDDWasOpen) {
                        this.createdMapDropdown.setClosed(false);
                    }
                    this.multiworldTypeOptionButton = new TooltipButton(width / 2 - 90, 24, 180, 20, (Component)Component.m_237113_((String)this.getMultiworldTypeButtonMessage()), b -> {
                        Object object = this.mapProcessor.uiPauseSync;
                        synchronized (object) {
                            if (this.isMapSelectionOptionEnabled()) {
                                this.mapProcessor.toggleMultiworldType(this.settingsDimension);
                                b.m_93666_((Component)Component.m_237113_((String)this.getMultiworldTypeButtonMessage()));
                            }
                        }
                    }, this.settingsDimension.isFutureMultiworldServerBased() ? () -> this.serverSelectionModeBox : () -> this.mapSelectionBox);
                    mapScreen.addButton(this.multiworldTypeOptionButton);
                    this.renameButton = Button.m_253074_((Component)Component.m_237115_((String)"gui.xaero_rename"), b -> {
                        Object object = this.mapProcessor.uiPauseSync;
                        synchronized (object) {
                            if (!this.canRenameMap()) {
                                return;
                            }
                            String currentMultiworld = this.settingsDimension.getFutureMultiworldUnsynced();
                            if (currentMultiworld == null) {
                                return;
                            }
                            minecraft.m_91152_((Screen)new GuiMapName(this.mapProcessor, (Screen)mapScreen, (Screen)mapScreen, this.settingsDimension, currentMultiworld));
                        }
                    }).m_252987_(width / 2 + 109, 80, 60, 20).m_253136_();
                    mapScreen.addButton(this.renameButton);
                    this.connectButton = Button.m_253074_((Component)this.getConnectButtonLabel(), b -> {
                        if (!this.canConnectMap()) {
                            return;
                        }
                        MapConnectionNode playerMapKey = this.settingsDimension.getMapWorld().getPlayerMapKey();
                        if (playerMapKey == null) {
                            return;
                        }
                        MapConnectionNode destinationMapKey = this.settingsDimension.getSelectedMapKeyUnsynced();
                        if (destinationMapKey == null) {
                            return;
                        }
                        String autoMapName = playerMapKey.getNamedString(this.settingsDimension.getMapWorld());
                        String selectedMapName = destinationMapKey.getNamedString(this.settingsDimension.getMapWorld());
                        String connectionDisplayString = autoMapName + "   \u00a7e<=>\u00a7r   " + selectedMapName;
                        MapConnectionManager mapConnections = this.settingsDimension.getMapWorld().getMapConnections();
                        boolean connected = mapConnections.isConnected(playerMapKey, destinationMapKey);
                        BooleanConsumer confirmationConsumer = result -> {
                            if (result) {
                                Object object = this.mapProcessor.uiSync;
                                synchronized (object) {
                                    if (connected) {
                                        mapConnections.removeConnection(playerMapKey, destinationMapKey);
                                    } else {
                                        mapConnections.addConnection(playerMapKey, destinationMapKey);
                                    }
                                    b.m_93666_(this.getConnectButtonLabel());
                                    this.settingsDimension.getMapWorld().saveConfig();
                                }
                            }
                            minecraft.m_91152_((Screen)mapScreen);
                        };
                        if (connected) {
                            minecraft.m_91152_((Screen)new ConfirmScreen(confirmationConsumer, (Component)Component.m_237115_((String)"gui.xaero_wm_disconnect_from_auto_msg"), (Component)Component.m_237113_((String)connectionDisplayString)));
                        } else {
                            minecraft.m_91152_((Screen)new ConfirmScreen(confirmationConsumer, (Component)Component.m_237115_((String)"gui.xaero_wm_connect_with_auto_msg"), (Component)Component.m_237113_((String)connectionDisplayString)));
                        }
                    }).m_252987_(width / 2 + 109, 102, 60, 20).m_253136_();
                    mapScreen.addButton(this.connectButton);
                    this.deleteButton = Button.m_253074_((Component)Component.m_237115_((String)"gui.xaero_delete"), b -> {
                        Object object = this.mapProcessor.uiPauseSync;
                        synchronized (object) {
                            if (!this.canDeleteMap()) {
                                return;
                            }
                            String selectedMWId = this.settingsDimension.getFutureCustomSelectedMultiworld();
                            minecraft.m_91152_((Screen)new ConfirmScreen(result -> {
                                if (result) {
                                    String mapNameAndIdLine = I18n.m_118938_((String)"gui.xaero_delete_map_msg4", (Object[])new Object[0]) + ": " + this.settingsDimension.getMultiworldName(selectedMWId) + " (" + selectedMWId + ")";
                                    minecraft.m_91152_((Screen)new ConfirmScreen(result2 -> {
                                        if (result2) {
                                            Object object = this.mapProcessor.uiSync;
                                            synchronized (object) {
                                                if (this.mapProcessor.getMapWorld() == this.settingsDimension.getMapWorld()) {
                                                    MapDimension currentDimension;
                                                    MapDimension mapDimension = currentDimension = !this.mapProcessor.isMapWorldUsable() ? null : this.mapProcessor.getMapWorld().getCurrentDimension();
                                                    if (this.settingsDimension == currentDimension && this.settingsDimension.getCurrentMultiworld().equals(selectedMWId)) {
                                                        if (WorldMapClientConfigUtils.getDebug()) {
                                                            WorldMap.LOGGER.info("Delayed map deletion!");
                                                        }
                                                        this.mapProcessor.requestCurrentMapDeletion();
                                                    } else {
                                                        if (WorldMapClientConfigUtils.getDebug()) {
                                                            WorldMap.LOGGER.info("Instant map deletion!");
                                                        }
                                                        this.settingsDimension.deleteMultiworldMapDataUnsynced(selectedMWId);
                                                    }
                                                    this.settingsDimension.deleteMultiworldId(selectedMWId);
                                                    this.settingsDimension.pickDefaultCustomMultiworldUnsynced();
                                                    this.settingsDimension.saveConfigUnsynced();
                                                    this.settingsDimension.futureMultiworldWritable = false;
                                                }
                                            }
                                        }
                                        minecraft.m_91152_((Screen)mapScreen);
                                    }, (Component)Component.m_237115_((String)"gui.xaero_delete_map_msg3"), (Component)Component.m_237113_((String)mapNameAndIdLine)));
                                } else {
                                    minecraft.m_91152_((Screen)mapScreen);
                                }
                            }, (Component)Component.m_237115_((String)"gui.xaero_delete_map_msg1"), (Component)Component.m_237115_((String)"gui.xaero_delete_map_msg2")));
                        }
                    }).m_252987_(width / 2 - 168, 80, 60, 20).m_253136_();
                    mapScreen.addButton(this.deleteButton);
                    this.confirmButton = Button.m_253074_((Component)Component.m_237115_((String)"gui.xaero_confirm"), b -> {
                        Object object = this.mapProcessor.uiPauseSync;
                        synchronized (object) {
                            if (!this.canConfirm()) {
                                return;
                            }
                            this.confirm(mapScreen, minecraft, width, height);
                        }
                    }).m_252987_(width / 2 - 50, 104, 100, 20).m_253136_();
                    mapScreen.addButton(this.confirmButton);
                    this.updateButtons(mapScreen, width, minecraft);
                } else {
                    this.switchingButton.f_93623_ = this.canToggleThisScreen();
                }
            } else {
                this.switchingButton.f_93623_ = false;
            }
        }
    }

    public static GuiDimensionOptions getSortedDimensionOptions(MapDimension dim) {
        int selected = 0;
        ResourceKey<Level> currentDim = dim.getDimId();
        ArrayList sortableList = new ArrayList();
        for (MapDimension dimension : dim.getMapWorld().getDimensionsList()) {
            sortableList.add(new KeySortableByOther(dimension.getDimId(), new Comparable[]{dimension.getDimId().m_135782_().toString()}));
        }
        Collections.sort(sortableList);
        selected = GuiMapSwitching.getDropdownSelectionIdFromValue(sortableList, currentDim);
        ResourceKey[] values = new ResourceKey[]{};
        values = sortableList.stream().map(KeySortableByOther::getKey).collect(ArrayList::new, ArrayList::add, ArrayList::addAll).toArray(values);
        return new GuiDimensionOptions(selected, values);
    }

    private DropDownWidget createDimensionDropdown(boolean paused, int width, GuiMap mapScreen, Minecraft minecraft) {
        GuiDimensionOptions dimOptions = GuiMapSwitching.getSortedDimensionOptions(this.settingsDimension);
        ArrayList<String> dropdownLabels = new ArrayList<String>();
        ResourceKey currentWorldDim = this.mapProcessor.getWorld() == null ? null : this.mapProcessor.getWorld().m_46472_();
        for (ResourceKey<Level> k : dimOptions.values) {
            Object result = k.m_135782_().toString();
            if (((String)result).startsWith("minecraft:")) {
                result = ((String)result).substring(10);
            }
            if (k == currentWorldDim) {
                result = (String)result + " (auto)";
            }
            dropdownLabels.add((String)result);
        }
        ResourceKey<Level>[] finalValues = dimOptions.values;
        DropDownWidget result = DropDownWidget.Builder.begin().setOptions(dropdownLabels.toArray(new String[0])).setX(width / 2 - 100).setY(64).setW(200).setSelected(Integer.valueOf(dimOptions.selected)).setCallback((dd, i) -> {
            ResourceKey selectedValue = finalValues[i];
            this.settingsDimension = this.settingsDimension.getMapWorld().getDimension((ResourceKey<Level>)selectedValue);
            if (selectedValue == currentWorldDim) {
                selectedValue = null;
            }
            this.settingsDimension.getMapWorld().setCustomDimensionId((ResourceKey<Level>)selectedValue);
            this.mapProcessor.checkForWorldUpdate();
            DropDownWidget newDropDown = this.createMapDropdown(this.uiPausedOnUpdate, width, mapScreen, minecraft);
            mapScreen.replaceWidget((AbstractWidget)this.createdMapDropdown, (AbstractWidget)newDropDown);
            this.createdMapDropdown = newDropDown;
            this.updateButtons(mapScreen, width, minecraft);
            return true;
        }).setContainer((IDropDownContainer)mapScreen).setNarrationTitle((Component)Component.m_237115_((String)"gui_xaero_wm_dropdown_dimension_select")).build();
        return result;
    }

    private DropDownWidget createMapDropdown(boolean paused, int width, GuiMap mapScreen, Minecraft minecraft) {
        List<CallSite> mwDropdownNames;
        int selected = 0;
        if (!paused) {
            int currentIndex;
            String currentMultiworld = this.settingsDimension.getFutureMultiworldUnsynced();
            ArrayList sortableList = new ArrayList();
            for (String mwId : this.settingsDimension.getMultiworldIdsCopy()) {
                sortableList.add(new KeySortableByOther((Object)mwId, new Comparable[]{this.settingsDimension.getMultiworldName(mwId).toLowerCase()}));
            }
            if (currentMultiworld != null && (currentIndex = GuiMapSwitching.getDropdownSelectionIdFromValue(sortableList, currentMultiworld)) == -1) {
                sortableList.add(new KeySortableByOther((Object)currentMultiworld, new Comparable[]{this.settingsDimension.getMultiworldName(currentMultiworld).toLowerCase()}));
            }
            Collections.sort(sortableList);
            if (currentMultiworld != null) {
                selected = GuiMapSwitching.getDropdownSelectionIdFromValue(sortableList, currentMultiworld);
            }
            this.mwDropdownValues = sortableList.stream().map(KeySortableByOther::getKey).collect(ArrayList::new, ArrayList::add, ArrayList::addAll).toArray(new String[0]);
            mwDropdownNames = sortableList.stream().map(KeySortableByOther::getKey).map(this.settingsDimension::getMultiworldName).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            mwDropdownNames.add((CallSite)((Object)("\u00a78" + I18n.m_118938_((String)"gui.xaero_create_new_map", (Object[])new Object[0]))));
        } else {
            mwDropdownNames = new ArrayList<CallSite>();
            this.mwDropdownValues = null;
            mwDropdownNames.add((CallSite)((Object)("\u00a77" + I18n.m_118938_((String)"gui.xaero_map_menu_please_wait", (Object[])new Object[0]))));
        }
        DropDownWidget result = DropDownWidget.Builder.begin().setOptions(mwDropdownNames.toArray(new String[0])).setX(width / 2 - 100).setY(84).setW(200).setSelected(Integer.valueOf(selected)).setCallback((dd, i) -> {
            Object object = this.mapProcessor.uiPauseSync;
            synchronized (object) {
                if (this.isUIPaused() || this.uiPausedOnUpdate) {
                    return false;
                }
                if (i < this.mwDropdownValues.length) {
                    this.mapProcessor.setMultiworld(this.settingsDimension, this.mwDropdownValues[i]);
                    this.updateButtons(mapScreen, width, minecraft);
                    return true;
                }
                minecraft.m_91152_((Screen)new GuiMapName(this.mapProcessor, (Screen)mapScreen, (Screen)mapScreen, this.settingsDimension, null));
                return false;
            }
        }).setContainer((IDropDownContainer)mapScreen).setNarrationTitle((Component)Component.m_237115_((String)"gui_xaero_wm_dropdown_map_select")).build();
        result.setActive(!paused);
        return result;
    }

    private boolean isUIPaused() {
        return this.mapProcessor.isUIPaused() || this.mapProcessor.isWaitingForWorldUpdate();
    }

    private boolean isMapSelectionOptionEnabled() {
        return !this.isUIPaused() && !this.settingsDimension.isFutureMultiworldServerBased() && this.settingsDimension.getMapWorld().isMultiplayer();
    }

    private boolean canToggleThisScreen() {
        return !this.isUIPaused() && this.settingsDimension != null && this.settingsDimension.futureMultiworldWritable;
    }

    private boolean canDeleteMap() {
        return !this.isUIPaused() && !this.settingsDimension.isFutureUsingWorldSaveUnsynced() && this.mwDropdownValues != null && this.mwDropdownValues.length > 1 && this.settingsDimension.getFutureCustomSelectedMultiworld() != null;
    }

    private boolean canRenameMap() {
        return !this.isUIPaused() && !this.settingsDimension.isFutureUsingWorldSaveUnsynced();
    }

    private boolean canConnectMap() {
        if (!this.mapProcessor.getMapWorld().isMultiplayer()) {
            return false;
        }
        MapConnectionNode playerMapKey = this.settingsDimension.getMapWorld().getPlayerMapKey();
        if (playerMapKey == null) {
            return false;
        }
        MapConnectionNode destinationMapKey = this.settingsDimension.getSelectedMapKeyUnsynced();
        if (destinationMapKey == null) {
            return false;
        }
        return !destinationMapKey.equals(playerMapKey);
    }

    private boolean canConfirm() {
        return !this.isUIPaused();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Component getConnectButtonLabel() {
        Object object = this.mapProcessor.uiPauseSync;
        synchronized (object) {
            if (this.isUIPaused()) {
                return CONNECT_MAP;
            }
            MapConnectionNode playerMapKey = this.settingsDimension.getMapWorld().getPlayerMapKey();
            if (playerMapKey == null) {
                return CONNECT_MAP;
            }
            MapConnectionNode destinationMapKey = this.settingsDimension.getSelectedMapKeyUnsynced();
            if (destinationMapKey == null) {
                return CONNECT_MAP;
            }
            MapConnectionManager mapConnections = this.settingsDimension.getMapWorld().getMapConnections();
            if (mapConnections.isConnected(playerMapKey, destinationMapKey)) {
                return DISCONNECT_MAP;
            }
            return CONNECT_MAP;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateButtons(GuiMap mapScreen, int width, Minecraft minecraft) {
        Object object = this.mapProcessor.uiPauseSync;
        synchronized (object) {
            boolean isPaused = this.isUIPaused();
            if (this.uiPausedOnUpdate != isPaused) {
                DropDownWidget newDropDown;
                DropDownWidget dropDownWidget = newDropDown = !this.active ? null : this.createMapDropdown(isPaused, width, mapScreen, minecraft);
                if (newDropDown != null) {
                    if (this.createdMapDropdown != null) {
                        mapScreen.replaceWidget((AbstractWidget)this.createdMapDropdown, (AbstractWidget)newDropDown);
                    } else {
                        mapScreen.m_7787_(newDropDown);
                    }
                } else if (this.createdMapDropdown != null) {
                    mapScreen.m_169411_((GuiEventListener)this.createdMapDropdown);
                }
                this.createdMapDropdown = !this.active ? null : newDropDown;
                this.uiPausedOnUpdate = isPaused;
            }
            this.switchingButton.f_93623_ = this.canToggleThisScreen();
            if (this.deleteButton != null) {
                this.deleteButton.f_93623_ = this.canDeleteMap();
            }
            if (this.renameButton != null) {
                this.renameButton.f_93623_ = this.canRenameMap();
            }
            if (this.connectButton != null) {
                this.connectButton.f_93623_ = this.canConnectMap();
                this.connectButton.m_93666_(this.getConnectButtonLabel());
            }
            if (this.multiworldTypeOptionButton != null) {
                this.multiworldTypeOptionButton.f_93623_ = this.isMapSelectionOptionEnabled();
            }
            if (this.confirmButton != null) {
                this.confirmButton.f_93623_ = this.canConfirm();
            }
        }
    }

    private String getMultiworldTypeButtonMessage() {
        int multiworldType = this.settingsDimension.getMapWorld().getFutureMultiworldType(this.settingsDimension);
        return I18n.m_118938_((String)"gui.xaero_map_selection", (Object[])new Object[0]) + ": " + I18n.m_118938_((String)(this.settingsDimension.isFutureMultiworldServerBased() ? "gui.xaero_mw_server" : (multiworldType == 0 ? "gui.xaero_mw_single" : (multiworldType == 1 ? "gui.xaero_mw_manual" : "gui.xaero_mw_spawn"))), (Object[])new Object[0]);
    }

    public void confirm(GuiMap mapScreen, Minecraft minecraft, int width, int height) {
        if (this.mapProcessor.confirmMultiworld(this.settingsDimension)) {
            this.active = false;
            mapScreen.m_6575_(minecraft, width, height);
        }
    }

    private static <S> int getDropdownSelectionIdFromValue(List<KeySortableByOther<S>> values, S value) {
        for (int selected = 0; selected < values.size(); ++selected) {
            if (!values.get(selected).getKey().equals(value)) continue;
            return selected;
        }
        return -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void preMapRender(GuiMap mapScreen, Minecraft minecraft, int width, int height) {
        String currentDropdownSelection;
        String currentMultiworld;
        if (!this.active && this.settingsDimension != null && !this.settingsDimension.futureMultiworldWritable) {
            this.active = true;
            mapScreen.m_6575_(minecraft, width, height);
        }
        if (this.mapSwitchingAllowed && (this.createdMapDropdown == null || this.createdMapDropdown.isClosed())) {
            Object object = this.mapProcessor.uiPauseSync;
            synchronized (object) {
                if (this.uiPausedOnUpdate != this.isUIPaused()) {
                    this.updateButtons(mapScreen, width, minecraft);
                }
            }
        }
        if (this.active && this.settingsDimension != null && this.createdMapDropdown.isClosed() && !this.uiPausedOnUpdate && (currentMultiworld = this.settingsDimension.getFutureMultiworldUnsynced()) != null && (!currentMultiworld.equals(currentDropdownSelection = this.mwDropdownValues[this.createdMapDropdown.getSelected()]) || this.writableOnInit != this.settingsDimension.futureMultiworldWritable)) {
            mapScreen.m_6575_(minecraft, width, height);
        }
    }

    public void renderText(GuiGraphics guiGraphics, Minecraft minecraft, int mouseX, int mouseY, int width, int height) {
        if (!this.active) {
            return;
        }
        String selectMapString = I18n.m_118938_((String)"gui.xaero_select_map", (Object[])new Object[0]) + ":";
        MultiBufferSource.BufferSource renderTypeBuffers = this.mapProcessor.getCvc().getRenderTypeBuffers();
        VertexConsumer backgroundVertexBuffer = renderTypeBuffers.m_6299_(CustomRenderTypes.MAP_COLOR_OVERLAY);
        MapRenderHelper.drawStringWithBackground(guiGraphics, minecraft.f_91062_, selectMapString, width / 2 - minecraft.f_91062_.m_92895_(selectMapString) / 2, 49, -1, 0.0f, 0.0f, 0.0f, 0.4f, backgroundVertexBuffer);
        renderTypeBuffers.m_109911_();
    }

    public void postMapRender(GuiGraphics guiGraphics, Minecraft minecraft, int mouseX, int mouseY, int width, int height) {
    }
}

