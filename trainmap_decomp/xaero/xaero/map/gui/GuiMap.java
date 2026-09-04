/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.controls.KeyBindsScreen
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.core.Registry
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.dimension.DimensionType
 *  org.joml.Matrix4f
 *  org.joml.Vector3fc
 *  org.lwjgl.opengl.GL11
 *  xaero.lib.client.config.ClientConfigManager
 *  xaero.lib.client.controls.util.KeyMappingUtils
 *  xaero.lib.client.graphics.shader.LibShaders
 *  xaero.lib.client.gui.config.context.BuiltInEditConfigScreenContexts
 *  xaero.lib.client.gui.widget.Tooltip
 *  xaero.lib.client.gui.widget.dropdown.DropDownWidget
 *  xaero.lib.common.config.option.ConfigOption
 *  xaero.lib.common.config.single.SingleConfigManager
 *  xaero.lib.common.util.MathUtils
 */
package xaero.map.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.controls.util.KeyMappingUtils;
import xaero.lib.client.graphics.shader.LibShaders;
import xaero.lib.client.gui.config.context.BuiltInEditConfigScreenContexts;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.client.gui.widget.dropdown.DropDownWidget;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.single.SingleConfigManager;
import xaero.lib.common.util.MathUtils;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.animation.Animation;
import xaero.map.animation.SinAnimation;
import xaero.map.animation.SlowingAnimation;
import xaero.map.common.config.WorldMapConfigConstants;
import xaero.map.common.config.option.WorldMapProfiledConfigOptions;
import xaero.map.config.primary.option.WorldMapPrimaryClientConfigOptions;
import xaero.map.config.util.WorldMapClientConfigUtils;
import xaero.map.controls.ControlsRegister;
import xaero.map.core.IWorldMapMinecraftClient;
import xaero.map.effects.Effects;
import xaero.map.element.HoveredMapElementHolder;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.ImprovedFramebuffer;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.map.gui.ExportScreen;
import xaero.map.gui.GuiCaveModeOptions;
import xaero.map.gui.GuiMapSwitching;
import xaero.map.gui.GuiTexturedButton;
import xaero.map.gui.GuiWorldMapSettings;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.MapMouseButtonPress;
import xaero.map.gui.MapTileSelection;
import xaero.map.gui.ScreenBase;
import xaero.map.gui.dropdown.rightclick.GuiRightClickMenu;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.misc.Misc;
import xaero.map.misc.OptimizedMath;
import xaero.map.mods.SupportMods;
import xaero.map.mods.gui.Waypoint;
import xaero.map.radar.tracker.PlayerTeleporter;
import xaero.map.radar.tracker.PlayerTrackerMapElement;
import xaero.map.region.BranchLeveledRegion;
import xaero.map.region.LayeredRegionManager;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapBlock;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTile;
import xaero.map.region.MapTileChunk;
import xaero.map.region.texture.RegionTexture;
import xaero.map.teleport.MapTeleporter;
import xaero.map.world.MapDimension;
import xaero.map.world.MapWorld;

public class GuiMap
extends ScreenBase
implements IRightClickableElement {
    private static final Component FULL_RELOAD_IN_PROGRESS = Component.m_237115_((String)"gui.xaero_full_reload_in_progress");
    private static final Component UNKNOWN_DIMENSION_TYPE1 = Component.m_237115_((String)"gui.xaero_unknown_dimension_type1");
    private static final Component UNKNOWN_DIMENSION_TYPE2 = Component.m_237115_((String)"gui.xaero_unknown_dimension_type2");
    private static final double ZOOM_STEP = 1.2;
    private static final int white = -1;
    private static final int black = -16777216;
    private static int lastAmountOfRegionsViewed = 1;
    private long loadingAnimationStart;
    private Entity player;
    private double screenScale = 0.0;
    private int mouseDownPosX = -1;
    private int mouseDownPosY = -1;
    private double mouseDownCameraX = -1.0;
    private double mouseDownCameraZ = -1.0;
    private int mouseCheckPosX = -1;
    private int mouseCheckPosY = -1;
    private long mouseCheckTimeNano = -1L;
    private int prevMouseCheckPosX = -1;
    private int prevMouseCheckPosY = -1;
    private long prevMouseCheckTimeNano = -1L;
    private double cameraX = 0.0;
    private double cameraZ = 0.0;
    private boolean shouldResetCameraPos;
    private int[] cameraDestination = null;
    private SlowingAnimation cameraDestinationAnimX = null;
    private SlowingAnimation cameraDestinationAnimZ = null;
    private double scale;
    private double userScale;
    private static double destScale = 3.0;
    private boolean pauseZoomKeys;
    private int lastZoomMethod;
    private double prevPlayerDimDiv;
    private HoveredMapElementHolder<?, ?> viewed = null;
    private boolean viewedInList;
    private HoveredMapElementHolder<?, ?> viewedOnMousePress = null;
    private boolean overWaypointsMenu;
    private Animation zoomAnim;
    public boolean waypointMenu = false;
    private boolean overPlayersMenu;
    public boolean playersMenu = false;
    private static ImprovedFramebuffer primaryScaleFBO = null;
    private float[] colourBuffer = new float[4];
    private ArrayList<MapRegion> regionBuffer = new ArrayList();
    private ArrayList<BranchLeveledRegion> branchRegionBuffer = new ArrayList();
    private boolean prevWaitingForBranchCache = true;
    private boolean prevLoadingLeaves = true;
    private ResourceKey<Level> lastNonNullViewedDimensionId;
    private ResourceKey<Level> lastViewedDimensionId;
    private String lastViewedMultiworldId;
    private int mouseBlockPosX;
    private int mouseBlockPosY;
    private int mouseBlockPosZ;
    private ResourceKey<Level> mouseBlockDim;
    private double mouseBlockCoordinateScale = 1.0;
    private long lastStartTime;
    private final GuiMapSwitching mapSwitchingGui;
    private MapMouseButtonPress leftMouseButton;
    private MapMouseButtonPress rightMouseButton;
    private MapProcessor mapProcessor;
    private MapDimension futureDimension;
    public boolean noUploadingLimits;
    private boolean[] waitingForBranchCache = new boolean[1];
    private Button settingsButton;
    private Button exportButton;
    private Button waypointsButton;
    private Button playersButton;
    private Button radarButton;
    private Button claimsButton;
    private Button zoomInButton;
    private Button zoomOutButton;
    private Button keybindingsButton;
    private Button caveModeButton;
    private Button dimensionToggleButton;
    private Button buttonPressed;
    private GuiRightClickMenu rightClickMenu;
    private int rightClickX;
    private int rightClickY;
    private int rightClickZ;
    private ResourceKey<Level> rightClickDim;
    private double rightClickCoordinateScale;
    private boolean lastFrameRenderedRootTextures;
    private MapTileSelection mapTileSelection;
    private boolean tabPressed;
    private GuiCaveModeOptions caveModeOptions;
    private static final Matrix4f identityMatrix = new Matrix4f();

    public GuiMap(Screen parent, Screen escape, MapProcessor mapProcessor, Entity player) {
        super(parent, escape, (Component)Component.m_237115_((String)"gui.xaero_world_map_screen"));
        this.player = player;
        this.shouldResetCameraPos = true;
        this.leftMouseButton = new MapMouseButtonPress();
        this.rightMouseButton = new MapMouseButtonPress();
        this.mapSwitchingGui = new GuiMapSwitching(mapProcessor);
        ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        boolean openingAnimationConfig = (Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.OPENING_ANIMATION);
        this.userScale = destScale * (double)(openingAnimationConfig ? 1.5f : 1.0f);
        this.zoomAnim = new SlowingAnimation(this.userScale, destScale, 0.88, destScale * 0.001);
        this.mapProcessor = mapProcessor;
        this.caveModeOptions = new GuiCaveModeOptions();
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onMapConstruct();
        }
    }

    private double getScaleMultiplier(int screenShortSide) {
        return screenShortSide <= 1080 ? 1.0 : (double)screenShortSide / 1080.0;
    }

    public <T extends GuiEventListener & Renderable> T m_142416_(T guiEventListener) {
        return (T)super.m_142416_(guiEventListener);
    }

    public <T extends GuiEventListener & Renderable> T addButton(T guiEventListener) {
        return this.m_142416_(guiEventListener);
    }

    public <T extends GuiEventListener & NarratableEntry> T m_7787_(T guiEventListener) {
        return (T)super.m_7787_(guiEventListener);
    }

    public void m_7856_() {
        super.m_7856_();
        ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        MapWorld mapWorld = this.mapProcessor.getMapWorld();
        this.futureDimension = mapWorld == null || mapWorld.getFutureDimensionId() == null ? null : mapWorld.getFutureDimension();
        this.tabPressed = false;
        boolean waypointsEnabled = (Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS);
        this.waypointMenu = this.waypointMenu && waypointsEnabled;
        this.mapSwitchingGui.init(this, this.f_96541_, this.f_96543_, this.f_96544_);
        boolean effectiveCaveModeAllowed = WorldMapClientConfigUtils.getEffectiveCaveModeAllowed();
        Tooltip caveModeButtonTooltip = new Tooltip((Component)Component.m_237115_((String)(effectiveCaveModeAllowed ? "gui.xaero_box_cave_mode" : "gui.xaero_box_cave_mode_not_allowed")));
        this.caveModeButton = new GuiTexturedButton(0, this.f_96544_ - 40, 20, 20, 229, 64, 16, 16, WorldMap.guiTextures, this::onCaveModeButton, () -> caveModeButtonTooltip);
        this.caveModeButton.f_93623_ = effectiveCaveModeAllowed;
        this.addButton(this.caveModeButton);
        this.caveModeOptions.onInit(this, this.mapProcessor);
        Tooltip dimensionToggleButtonTooltip = new Tooltip((Component)Component.m_237110_((String)"gui.xaero_dimension_toggle_button", (Object[])new Object[]{KeyMappingUtils.getKeyName((KeyMapping)ControlsRegister.keyToggleDimension)}));
        this.dimensionToggleButton = new GuiTexturedButton(0, this.f_96544_ - 60, 20, 20, 197, 80, 16, 16, WorldMap.guiTextures, this::onDimensionToggleButton, () -> dimensionToggleButtonTooltip);
        this.addButton(this.dimensionToggleButton);
        this.loadingAnimationStart = System.currentTimeMillis();
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.requestWaypointsRefresh();
        }
        this.screenScale = Minecraft.m_91087_().m_91268_().m_85449_();
        this.pauseZoomKeys = false;
        Tooltip openSettingsTooltip = new Tooltip((Component)Component.m_237110_((String)"gui.xaero_box_open_settings", (Object[])new Object[]{KeyMappingUtils.getKeyName((KeyMapping)ControlsRegister.keyOpenSettings)}));
        this.settingsButton = new GuiTexturedButton(0, 0, 30, 30, 113, 0, 20, 20, WorldMap.guiTextures, this::onSettingsButton, () -> openSettingsTooltip);
        this.addButton(this.settingsButton);
        Tooltip waypointsTooltip = waypointsEnabled ? new Tooltip(this.waypointMenu ? "gui.xaero_box_close_waypoints" : "gui.xaero_box_open_waypoints") : new Tooltip(!SupportMods.minimap() ? "gui.xaero_box_waypoints_minimap_required" : "gui.xaero_box_waypoints_disabled");
        Tooltip playersTooltip = new Tooltip(this.playersMenu ? "gui.xaero_box_close_players" : "gui.xaero_box_open_players");
        boolean displayClaimsConfig = (Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS);
        Tooltip claimsTooltip = SupportMods.pac() ? new Tooltip((Component)Component.m_237110_((String)(displayClaimsConfig ? "gui.xaero_box_pac_displaying_claims" : "gui.xaero_box_pac_not_displaying_claims"), (Object[])new Object[]{Component.m_237113_((String)KeyMappingUtils.getKeyName((KeyMapping)SupportMods.xaeroPac.getPacClaimsKeyBinding())).m_130940_(ChatFormatting.DARK_GREEN)})) : new Tooltip((Component)Component.m_237115_((String)"gui.xaero_box_claims_pac_required"));
        this.waypointsButton = new GuiTexturedButton(this.f_96543_ - 20, this.f_96544_ - 20, 20, 20, 213, 0, 16, 16, WorldMap.guiTextures, this::onWaypointsButton, () -> waypointsTooltip);
        this.addButton(this.waypointsButton);
        this.waypointsButton.f_93623_ = waypointsEnabled;
        this.playersButton = new GuiTexturedButton(this.f_96543_ - 20, this.f_96544_ - 40, 20, 20, 197, 32, 16, 16, WorldMap.guiTextures, this::onPlayersButton, () -> playersTooltip);
        this.addButton(this.playersButton);
        boolean minimapRadarConfig = (Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.MINIMAP_RADAR);
        Tooltip radarButtonTooltip = new Tooltip((Component)Component.m_237110_((String)(minimapRadarConfig ? "gui.xaero_box_minimap_radar" : "gui.xaero_box_no_minimap_radar"), (Object[])new Object[]{Component.m_237113_((String)KeyMappingUtils.getKeyName((KeyMapping)(SupportMods.minimap() ? SupportMods.xaeroMinimap.getToggleRadarKey() : null))).m_130940_(ChatFormatting.DARK_GREEN)}));
        this.radarButton = new GuiTexturedButton(this.f_96543_ - 20, this.f_96544_ - 60, 20, 20, minimapRadarConfig ? 213 : 229, 32, 16, 16, WorldMap.guiTextures, this::onRadarButton, () -> radarButtonTooltip);
        this.addButton(this.radarButton);
        this.getRadarButton().f_93623_ = SupportMods.minimap();
        this.claimsButton = new GuiTexturedButton(this.f_96543_ - 20, this.f_96544_ - 80, 20, 20, displayClaimsConfig ? 197 : 213, 64, 16, 16, WorldMap.guiTextures, this::onClaimsButton, () -> claimsTooltip);
        this.addButton(this.claimsButton);
        this.claimsButton.f_93623_ = SupportMods.pac() && !WorldMapClientConfigUtils.isOptionServerEnforced((ConfigOption<Boolean>)WorldMapProfiledConfigOptions.OPAC_CLAIMS);
        Tooltip exportButtonTooltip = new Tooltip("gui.xaero_box_export");
        this.exportButton = new GuiTexturedButton(this.f_96543_ - 20, this.f_96544_ - 100, 20, 20, 133, 0, 16, 16, WorldMap.guiTextures, this::onExportButton, () -> exportButtonTooltip);
        this.addButton(this.exportButton);
        Tooltip controlsButtonTooltip = new Tooltip(I18n.m_118938_((String)"gui.xaero_box_controls", (Object[])new Object[]{(SupportMods.minimap() ? SupportMods.xaeroMinimap.getControlsTooltip() : "") + (SupportMods.pac() ? SupportMods.xaeroPac.getControlsTooltip() : "")}));
        controlsButtonTooltip.setStartWidth(400);
        this.keybindingsButton = new GuiTexturedButton(this.f_96543_ - 20, this.f_96544_ - 120, 20, 20, 197, 0, 16, 16, WorldMap.guiTextures, this::onKeybindingsButton, () -> controlsButtonTooltip);
        this.addButton(this.keybindingsButton);
        Tooltip zoomInButtonTooltip = new Tooltip((Component)Component.m_237110_((String)"gui.xaero_box_zoom_in", (Object[])new Object[]{Component.m_237113_((String)KeyMappingUtils.getKeyName((KeyMapping)ControlsRegister.keyZoomIn)).m_130940_(ChatFormatting.DARK_GREEN)}));
        this.zoomInButton = new GuiTexturedButton(this.f_96543_ - 20, this.f_96544_ - 160, 20, 20, 165, 0, 16, 16, WorldMap.guiTextures, this::onZoomInButton, () -> zoomInButtonTooltip);
        Tooltip zoomOutButtonTooltip = new Tooltip((Component)Component.m_237110_((String)"gui.xaero_box_zoom_out", (Object[])new Object[]{Component.m_237113_((String)KeyMappingUtils.getKeyName((KeyMapping)ControlsRegister.keyZoomOut)).m_130940_(ChatFormatting.DARK_GREEN)}));
        this.zoomOutButton = new GuiTexturedButton(this.f_96543_ - 20, this.f_96544_ - 140, 20, 20, 181, 0, 16, 16, WorldMap.guiTextures, this::onZoomOutButton, () -> zoomOutButtonTooltip);
        if (((Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.ZOOM_BUTTONS)).booleanValue()) {
            this.addButton(this.zoomOutButton);
            this.addButton(this.zoomInButton);
        }
        if (this.rightClickMenu != null) {
            this.rightClickMenu.setClosed(true);
            this.rightClickMenu = null;
        }
        if (SupportMods.minimap() && this.waypointMenu) {
            SupportMods.xaeroMinimap.onMapInit(this, this.f_96541_, this.f_96543_, this.f_96544_);
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.onMapInit(this, this.f_96541_, this.f_96543_, this.f_96544_);
        }
    }

    private void onCaveModeButton(Button b) {
        this.caveModeOptions.toggle(this);
        this.m_7522_((GuiEventListener)this.caveModeButton);
    }

    private void onDimensionToggleButton(Button b) {
        this.mapProcessor.getMapWorld().toggleDimension(!GuiMap.m_96638_());
        String messageType = this.mapProcessor.getMapWorld().getCustomDimensionId() == null ? "gui.xaero_switched_to_current_dimension" : "gui.xaero_switched_to_dimension";
        ResourceLocation messageDimLoc = this.mapProcessor.getMapWorld().getFutureDimensionId() == null ? null : this.mapProcessor.getMapWorld().getFutureDimensionId().m_135782_();
        this.mapProcessor.getMessageBox().addMessage((Component)Component.m_237110_((String)messageType, (Object[])new Object[]{messageDimLoc}));
        this.m_6575_(this.f_96541_, this.f_96543_, this.f_96544_);
        this.m_7522_((GuiEventListener)this.dimensionToggleButton);
    }

    private void onSettingsButton(Button b) {
        this.f_96541_.m_91152_((Screen)new GuiWorldMapSettings((Screen)this, (Screen)this, BuiltInEditConfigScreenContexts.CLIENT));
    }

    private void onKeybindingsButton(Button b) {
        this.f_96541_.m_91152_((Screen)new KeyBindsScreen((Screen)this, this.f_96541_.f_91066_));
    }

    private void onExportButton(Button b) {
        this.f_96541_.m_91152_((Screen)new ExportScreen((Screen)this, (Screen)this, this.mapProcessor, this.mapTileSelection));
    }

    private void toggleWaypointMenu() {
        if (this.playersMenu) {
            this.togglePlayerMenu();
        }
        boolean bl = this.waypointMenu = !this.waypointMenu;
        if (!this.waypointMenu) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().onMenuClosed();
            this.unfocusAll();
        }
    }

    private void togglePlayerMenu() {
        if (this.waypointMenu) {
            this.toggleWaypointMenu();
        }
        boolean bl = this.playersMenu = !this.playersMenu;
        if (!this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.onMenuClosed();
            this.unfocusAll();
        }
    }

    private void onPlayersButton(Button b) {
        this.togglePlayerMenu();
        this.m_6575_(this.f_96541_, this.f_96543_, this.f_96544_);
        this.m_7522_((GuiEventListener)this.playersButton);
    }

    public void onClaimsButton(Button unused) {
        WorldMapClientConfigUtils.tryTogglingCurrentProfileOption((ConfigOption<Boolean>)WorldMapProfiledConfigOptions.OPAC_CLAIMS);
        this.m_6575_(this.f_96541_, this.f_96543_, this.f_96544_);
        this.m_7522_((GuiEventListener)this.claimsButton);
    }

    private void onWaypointsButton(Button b) {
        this.toggleWaypointMenu();
        this.m_6575_(this.f_96541_, this.f_96543_, this.f_96544_);
        this.m_7522_((GuiEventListener)this.waypointsButton);
    }

    public void onRadarButton(Button b) {
        WorldMapClientConfigUtils.tryTogglingCurrentProfileOption((ConfigOption<Boolean>)WorldMapProfiledConfigOptions.MINIMAP_RADAR);
        this.m_6575_(this.f_96541_, this.f_96543_, this.f_96544_);
        this.m_7522_((GuiEventListener)this.radarButton);
    }

    private void onZoomInButton(Button b) {
        this.buttonPressed = this.buttonPressed == null ? b : null;
    }

    private void onZoomOutButton(Button b) {
        this.buttonPressed = this.buttonPressed == null ? b : null;
    }

    public boolean m_6375_(double par1, double par2, int par3) {
        boolean toReturn = super.m_6375_(par1, par2, par3);
        if (!toReturn) {
            if (par3 == 0) {
                this.leftMouseButton.clicked = true;
                this.leftMouseButton.isDown = true;
                this.leftMouseButton.pressedAtX = (int)Misc.getMouseX(this.f_96541_, SupportMods.vivecraft);
                this.leftMouseButton.pressedAtY = (int)Misc.getMouseY(this.f_96541_, SupportMods.vivecraft);
            } else if (par3 == 1) {
                this.rightMouseButton.clicked = true;
                this.rightMouseButton.isDown = true;
                this.rightMouseButton.pressedAtX = (int)Misc.getMouseX(this.f_96541_, SupportMods.vivecraft);
                this.rightMouseButton.pressedAtY = (int)Misc.getMouseY(this.f_96541_, SupportMods.vivecraft);
                this.viewedOnMousePress = this.viewed;
                this.rightClickX = this.mouseBlockPosX;
                this.rightClickY = this.mouseBlockPosY;
                this.rightClickZ = this.mouseBlockPosZ;
                this.rightClickDim = this.mouseBlockDim;
                this.rightClickCoordinateScale = this.mouseBlockCoordinateScale;
                if (SupportMods.minimap()) {
                    SupportMods.xaeroMinimap.onRightClick();
                }
                if (this.viewedOnMousePress == null || !this.viewedOnMousePress.isRightClickValid()) {
                    this.mapTileSelection = new MapTileSelection(this.rightClickX >> 4, this.rightClickZ >> 4);
                }
            } else {
                toReturn = this.onInputPress(InputConstants.Type.MOUSE, par3);
            }
            if (!toReturn && this.caveModeOptions.isEnabled()) {
                this.caveModeOptions.toggle(this);
                toReturn = true;
            }
        }
        return toReturn;
    }

    public boolean m_6348_(double par1, double par2, int par3) {
        boolean toReturn;
        this.buttonPressed = null;
        int mouseX = (int)Misc.getMouseX(this.f_96541_, SupportMods.vivecraft);
        int mouseY = (int)Misc.getMouseY(this.f_96541_, SupportMods.vivecraft);
        if (this.leftMouseButton.isDown && par3 == 0) {
            this.leftMouseButton.isDown = false;
            if (Math.abs(this.leftMouseButton.pressedAtX - mouseX) < 5 && Math.abs(this.leftMouseButton.pressedAtY - mouseY) < 5) {
                this.mapClicked(0, this.leftMouseButton.pressedAtX, this.leftMouseButton.pressedAtY);
            }
            this.leftMouseButton.pressedAtX = -1;
            this.leftMouseButton.pressedAtY = -1;
        }
        if (this.rightMouseButton.isDown && par3 == 1) {
            this.rightMouseButton.isDown = false;
            this.mapClicked(1, mouseX, mouseY);
            this.rightMouseButton.pressedAtX = -1;
            this.rightMouseButton.pressedAtY = -1;
        }
        if (this.waypointMenu) {
            SupportMods.xaeroMinimap.onMapMouseRelease(par1, par2, par3);
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.onMapMouseRelease(par1, par2, par3);
        }
        if (!(toReturn = super.m_6348_(par1, par2, par3))) {
            toReturn = this.onInputRelease(InputConstants.Type.MOUSE, par3);
        }
        return toReturn;
    }

    public boolean m_6050_(double par1, double par2, double wheel) {
        int direction;
        int n = direction = wheel > 0.0 ? 1 : -1;
        if (this.waypointMenu && this.overWaypointsMenu) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().mouseScrolled(direction);
        } else if (this.playersMenu && this.overPlayersMenu) {
            WorldMap.trackedPlayerMenuRenderer.mouseScrolled(direction);
        } else {
            this.changeZoom(wheel, 0);
        }
        return super.m_6050_(par1, par2, wheel);
    }

    private void changeZoom(double factor, int zoomMethod) {
        this.closeDropdowns();
        this.lastZoomMethod = zoomMethod;
        this.cameraDestinationAnimX = null;
        this.cameraDestinationAnimZ = null;
        if (GuiMap.m_96637_()) {
            double destScaleBefore = destScale;
            if (destScale >= 1.0) {
                destScale = factor > 0.0 ? Math.ceil(destScale) : Math.floor(destScale);
                if (destScaleBefore == destScale) {
                    destScale += factor > 0.0 ? 1.0 : -1.0;
                }
                if (destScale == 0.0) {
                    destScale = 0.5;
                }
            } else {
                double reversedScale = 1.0 / destScale;
                double log2 = Math.log(reversedScale) / Math.log(2.0);
                log2 = factor > 0.0 ? Math.floor(log2) : Math.ceil(log2);
                destScale = 1.0 / Math.pow(2.0, log2);
                if (destScaleBefore == destScale) {
                    destScale = 1.0 / Math.pow(2.0, log2 + (double)(factor > 0.0 ? -1 : 1));
                }
            }
        } else {
            destScale *= Math.pow(1.2, factor);
        }
        if (destScale < 0.0625) {
            destScale = 0.0625;
        } else if (destScale > 50.0) {
            destScale = 50.0;
        }
    }

    public void m_7861_() {
        super.m_7861_();
        this.leftMouseButton.isDown = false;
        this.rightMouseButton.isDown = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void m_88315_(GuiGraphics guiGraphics, int scaledMouseX, int scaledMouseY, float partialTicks) {
        int direction;
        MapDimension currentFutureDim;
        guiGraphics.m_280262_();
        while (GL11.glGetError() != 0) {
        }
        GlStateManager._clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        LibShaders.ensureShaders();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Minecraft mc = Minecraft.m_91087_();
        ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        SingleConfigManager primaryConfigManager = WorldMap.INSTANCE.getConfigs().getPrimaryClientConfigManager();
        long startTime = System.currentTimeMillis();
        MapDimension mapDimension = currentFutureDim = !this.mapProcessor.isMapWorldUsable() ? null : this.mapProcessor.getMapWorld().getFutureDimension();
        if (currentFutureDim != this.futureDimension) {
            this.m_6575_(this.f_96541_, this.f_96543_, this.f_96544_);
        }
        PoseStack matrixStack = guiGraphics.m_280168_();
        double playerDimDiv = this.prevPlayerDimDiv;
        Object object = this.mapProcessor.renderThreadPauseSync;
        synchronized (object) {
            Registry<DimensionType> dimTypes;
            if (!this.mapProcessor.isRenderingPaused() && (dimTypes = this.mapProcessor.getWorldDimensionTypeRegistry()) != null) {
                playerDimDiv = this.mapProcessor.getMapWorld().getCurrentDimension().calculateDimDiv(dimTypes, this.player.m_9236_().m_6042_());
            }
        }
        double scaledPlayerX = this.player.m_20185_() / playerDimDiv;
        double scaledPlayerZ = this.player.m_20189_() / playerDimDiv;
        if (this.shouldResetCameraPos) {
            this.cameraX = (float)scaledPlayerX;
            this.cameraZ = (float)scaledPlayerZ;
            this.shouldResetCameraPos = false;
        } else if (this.prevPlayerDimDiv != 0.0 && playerDimDiv != this.prevPlayerDimDiv) {
            double oldScaledPlayerX = this.player.m_20185_() / this.prevPlayerDimDiv;
            double oldScaledPlayerZ = this.player.m_20189_() / this.prevPlayerDimDiv;
            this.cameraX = this.cameraX - oldScaledPlayerX + scaledPlayerX;
            this.cameraZ = this.cameraZ - oldScaledPlayerZ + scaledPlayerZ;
            this.cameraDestinationAnimX = null;
            this.cameraDestinationAnimZ = null;
            this.cameraDestination = null;
        }
        this.prevPlayerDimDiv = playerDimDiv;
        double cameraXBefore = this.cameraX;
        double cameraZBefore = this.cameraZ;
        double scaleBefore = this.scale;
        this.mapSwitchingGui.preMapRender(this, this.f_96541_, this.f_96543_, this.f_96544_);
        long passed = this.lastStartTime == 0L ? 16L : startTime - this.lastStartTime;
        double passedScrolls = (float)passed / 64.0f;
        int n = this.buttonPressed == this.zoomInButton || KeyMappingUtils.isPhysicallyDown((KeyMapping)ControlsRegister.keyZoomIn) ? 1 : (direction = this.buttonPressed == this.zoomOutButton || KeyMappingUtils.isPhysicallyDown((KeyMapping)ControlsRegister.keyZoomOut) ? -1 : 0);
        if (direction != 0) {
            boolean ctrlKey = GuiMap.m_96637_();
            if (!ctrlKey || !this.pauseZoomKeys) {
                this.changeZoom((double)direction * passedScrolls, this.buttonPressed == this.zoomInButton || this.buttonPressed == this.zoomOutButton ? 2 : 1);
                if (ctrlKey) {
                    this.pauseZoomKeys = true;
                }
            }
        } else {
            this.pauseZoomKeys = false;
        }
        this.lastStartTime = startTime;
        if (this.cameraDestination != null) {
            this.cameraDestinationAnimX = new SlowingAnimation(this.cameraX, this.cameraDestination[0], 0.9, 0.01);
            this.cameraDestinationAnimZ = new SlowingAnimation(this.cameraZ, this.cameraDestination[1], 0.9, 0.01);
            this.cameraDestination = null;
        }
        if (this.cameraDestinationAnimX != null) {
            this.cameraX = this.cameraDestinationAnimX.getCurrent();
            if (this.cameraX == this.cameraDestinationAnimX.getDestination()) {
                this.cameraDestinationAnimX = null;
            }
        }
        if (this.cameraDestinationAnimZ != null) {
            this.cameraZ = this.cameraDestinationAnimZ.getCurrent();
            if (this.cameraZ == this.cameraDestinationAnimZ.getDestination()) {
                this.cameraDestinationAnimZ = null;
            }
        }
        this.lastViewedDimensionId = null;
        this.lastViewedMultiworldId = null;
        this.mouseBlockPosY = Short.MAX_VALUE;
        boolean discoveredForHighlights = false;
        Object object2 = this.mapProcessor.renderThreadPauseSync;
        synchronized (object2) {
            if (!this.mapProcessor.isRenderingPaused()) {
                boolean mapLoaded = this.mapProcessor.getCurrentWorldId() != null && !this.mapProcessor.isWaitingForWorldUpdate() && this.mapProcessor.getMapSaveLoad().isRegionDetectionComplete();
                boolean noWorldMapEffect = mc.f_91074_ == null || Misc.hasEffect((Player)mc.f_91074_, Effects.NO_WORLD_MAP) || Misc.hasEffect((Player)mc.f_91074_, Effects.NO_WORLD_MAP_HARMFUL);
                Item mapItem = this.mapProcessor.getMapItem();
                boolean allowedBasedOnItem = mapItem == null || mc.f_91074_ != null && Misc.hasItem((Player)mc.f_91074_, mapItem);
                boolean isLocked = this.mapProcessor.isCurrentMapLocked();
                if (mapLoaded && !noWorldMapEffect && allowedBasedOnItem && !isLocked) {
                    HoveredMapElementHolder<?, ?> hovered;
                    boolean renderingMenus;
                    String subWorldNameToRender;
                    MapRegion leveledRegion;
                    double secondaryOffsetY;
                    double secondaryOffsetX;
                    MapRegion leafRegion;
                    if (SupportMods.vivecraft) {
                        GlStateManager._clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                        GlStateManager._clear((int)16384, (boolean)Minecraft.f_91002_);
                    }
                    this.mapProcessor.updateCaveStart();
                    this.lastViewedDimensionId = this.mapProcessor.getMapWorld().getCurrentDimension().getDimId();
                    this.lastNonNullViewedDimensionId = this.lastViewedDimensionId;
                    this.lastViewedMultiworldId = this.mapProcessor.getMapWorld().getCurrentDimension().getCurrentMultiworld();
                    if (SupportMods.minimap()) {
                        SupportMods.xaeroMinimap.checkWaypoints(this.mapProcessor.getMapWorld().isMultiplayer(), this.lastViewedDimensionId, this.lastViewedMultiworldId, this.f_96543_, this.f_96544_, this, this.mapProcessor.getMapWorld(), this.mapProcessor.getWorldDimensionTypeRegistry());
                    }
                    int mouseXPos = (int)Misc.getMouseX(mc, false);
                    int mouseYPos = (int)Misc.getMouseY(mc, false);
                    double scaleMultiplier = this.getScaleMultiplier(Math.min(mc.m_91268_().m_85441_(), mc.m_91268_().m_85442_()));
                    this.scale = this.userScale * scaleMultiplier;
                    if (this.mouseCheckPosX == -1 || System.nanoTime() - this.mouseCheckTimeNano > 30000000L) {
                        this.prevMouseCheckPosX = this.mouseCheckPosX;
                        this.prevMouseCheckPosY = this.mouseCheckPosY;
                        this.prevMouseCheckTimeNano = this.mouseCheckTimeNano;
                        this.mouseCheckPosX = mouseXPos;
                        this.mouseCheckPosY = mouseYPos;
                        this.mouseCheckTimeNano = System.nanoTime();
                    }
                    if (!this.leftMouseButton.isDown) {
                        if (this.mouseDownPosX != -1) {
                            this.mouseDownPosX = -1;
                            this.mouseDownPosY = -1;
                            if (this.prevMouseCheckTimeNano != -1L) {
                                double speed_z;
                                double frameTime60FPS;
                                double downTime = 0.0;
                                int draggedX = 0;
                                int draggedY = 0;
                                draggedX = mouseXPos - this.prevMouseCheckPosX;
                                downTime = System.nanoTime() - this.prevMouseCheckTimeNano;
                                double speedScale = downTime / (frameTime60FPS = 1.6666666666666666E7);
                                double speed_x = (double)(-draggedX) / this.scale / speedScale;
                                double speed = Math.sqrt(speed_x * speed_x + (speed_z = (double)(-(draggedY = mouseYPos - this.prevMouseCheckPosY)) / this.scale / speedScale) * speed_z);
                                if (speed > 0.0) {
                                    double cos = speed_x / speed;
                                    double sin = speed_z / speed;
                                    double maxSpeed = 500.0 / this.userScale;
                                    speed = Math.abs(speed) > maxSpeed ? Math.copySign(maxSpeed, speed) : speed;
                                    double speed_factor = 0.9;
                                    double ln = Math.log(speed_factor);
                                    double move_distance = -speed / ln;
                                    double moveX = cos * move_distance;
                                    double moveZ = sin * move_distance;
                                    this.cameraDestinationAnimX = new SlowingAnimation(this.cameraX, this.cameraX + moveX, 0.9, 0.01);
                                    this.cameraDestinationAnimZ = new SlowingAnimation(this.cameraZ, this.cameraZ + moveZ, 0.9, 0.01);
                                }
                            }
                        }
                    } else if (this.viewed == null || !this.viewedInList || this.mouseDownPosX != -1) {
                        if (this.mouseDownPosX != -1) {
                            this.cameraX = (double)(this.mouseDownPosX - mouseXPos) / this.scale + this.mouseDownCameraX;
                            this.cameraZ = (double)(this.mouseDownPosY - mouseYPos) / this.scale + this.mouseDownCameraZ;
                        } else {
                            this.mouseDownPosX = mouseXPos;
                            this.mouseDownPosY = mouseYPos;
                            this.mouseDownCameraX = this.cameraX;
                            this.mouseDownCameraZ = this.cameraZ;
                            this.cameraDestinationAnimX = null;
                            this.cameraDestinationAnimZ = null;
                        }
                    }
                    int mouseFromCentreX = mouseXPos - mc.m_91268_().m_85441_() / 2;
                    int mouseFromCentreY = mouseYPos - mc.m_91268_().m_85442_() / 2;
                    double oldMousePosX = (double)mouseFromCentreX / this.scale + this.cameraX;
                    double oldMousePosZ = (double)mouseFromCentreY / this.scale + this.cameraZ;
                    double preScale = this.scale;
                    if (destScale != this.userScale) {
                        if (this.zoomAnim != null) {
                            this.userScale = this.zoomAnim.getCurrent();
                            this.scale = this.userScale * scaleMultiplier;
                        }
                        if (this.zoomAnim == null || MathUtils.round((double)this.zoomAnim.getDestination(), (int)4) != MathUtils.round((double)destScale, (int)4)) {
                            this.zoomAnim = new SinAnimation(this.userScale, destScale, 100L);
                        }
                    }
                    if (this.scale > preScale && this.lastZoomMethod != 2) {
                        this.cameraX = oldMousePosX - (double)mouseFromCentreX / this.scale;
                        this.cameraZ = oldMousePosZ - (double)mouseFromCentreY / this.scale;
                    }
                    int textureLevel = 0;
                    double fboScale = this.scale >= 1.0 ? Math.max(1.0, Math.floor(this.scale)) : this.scale;
                    if (this.userScale < 1.0) {
                        double reversedScale = 1.0 / this.userScale;
                        double log2 = Math.floor(Math.log(reversedScale) / Math.log(2.0));
                        textureLevel = Math.min((int)log2, 3);
                    }
                    this.mapProcessor.getMapSaveLoad().mainTextureLevel = textureLevel;
                    int leveledRegionShift = 9 + textureLevel;
                    double secondaryScale = this.scale / fboScale;
                    matrixStack.m_85836_();
                    double mousePosX = (double)mouseFromCentreX / this.scale + this.cameraX;
                    double mousePosZ = (double)mouseFromCentreY / this.scale + this.cameraZ;
                    matrixStack.m_85836_();
                    matrixStack.m_252880_(0.0f, 0.0f, 971.0f);
                    this.mouseBlockPosX = (int)Math.floor(mousePosX);
                    this.mouseBlockPosZ = (int)Math.floor(mousePosZ);
                    this.mouseBlockDim = this.mapProcessor.getMapWorld().getCurrentDimension().getDimId();
                    this.mouseBlockCoordinateScale = this.getCurrentMapCoordinateScale();
                    if (SupportMods.minimap()) {
                        SupportMods.xaeroMinimap.onBlockHover();
                    }
                    int mouseRegX = this.mouseBlockPosX >> leveledRegionShift;
                    int mouseRegZ = this.mouseBlockPosZ >> leveledRegionShift;
                    int renderedCaveLayer = this.mapProcessor.getCurrentCaveLayer();
                    LeveledRegion<?> reg = this.mapProcessor.getLeveledRegion(renderedCaveLayer, mouseRegX, mouseRegZ, textureLevel);
                    int maxRegBlockCoord = (1 << leveledRegionShift) - 1;
                    int mouseRegPixelX = (this.mouseBlockPosX & maxRegBlockCoord) >> textureLevel;
                    int mouseRegPixelZ = (this.mouseBlockPosZ & maxRegBlockCoord) >> textureLevel;
                    this.mouseBlockPosX = (mouseRegX << leveledRegionShift) + (mouseRegPixelX << textureLevel);
                    this.mouseBlockPosZ = (mouseRegZ << leveledRegionShift) + (mouseRegPixelZ << textureLevel);
                    if (this.mapTileSelection != null && this.rightClickMenu == null) {
                        this.mapTileSelection.setEnd(this.mouseBlockPosX >> 4, this.mouseBlockPosZ >> 4);
                    }
                    MapTileChunk chunk = (leafRegion = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, this.mouseBlockPosX >> 9, this.mouseBlockPosZ >> 9, false)) == null ? null : leafRegion.getChunk(this.mouseBlockPosX >> 6 & 7, this.mouseBlockPosZ >> 6 & 7);
                    int debugTextureX = this.mouseBlockPosX >> leveledRegionShift - 3 & 7;
                    int debugTextureY = this.mouseBlockPosZ >> leveledRegionShift - 3 & 7;
                    RegionTexture tex = reg != null && reg.hasTextures() ? (RegionTexture)reg.getTexture(debugTextureX, debugTextureY) : null;
                    boolean debugConfig = (Boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG);
                    if (debugConfig) {
                        if (reg != null) {
                            ArrayList<String> debugLines = new ArrayList<String>();
                            if (tex != null) {
                                MapBlock block;
                                MapTile mouseTile;
                                tex.addDebugLines(debugLines);
                                MapTile mapTile = mouseTile = chunk == null ? null : chunk.getTile(this.mouseBlockPosX >> 4 & 3, this.mouseBlockPosZ >> 4 & 3);
                                if (mouseTile != null && (block = mouseTile.getBlock(this.mouseBlockPosX & 0xF, this.mouseBlockPosZ & 0xF)) != null) {
                                    guiGraphics.m_280137_(mc.f_91062_, block.toRenderString(leafRegion.getBiomeRegistry()), this.f_96543_ / 2, 22, -1);
                                    if (block.getNumberOfOverlays() != 0) {
                                        for (int i = 0; i < block.getOverlays().size(); ++i) {
                                            guiGraphics.m_280137_(mc.f_91062_, block.getOverlays().get(i).toRenderString(), this.f_96543_ / 2, 32 + i * 10, -1);
                                        }
                                    }
                                }
                            }
                            debugLines.add("");
                            debugLines.add(reg.toString());
                            reg.addDebugLines(debugLines, this.mapProcessor, debugTextureX, debugTextureY);
                            for (int i = 0; i < debugLines.size(); ++i) {
                                guiGraphics.m_280488_(mc.f_91062_, (String)debugLines.get(i), 5, 15 + 10 * i, -1);
                            }
                        }
                        DimensionType dimType = this.mapProcessor.getMapWorld().getCurrentDimension().getDimensionType(this.mapProcessor.getWorldDimensionTypeRegistry());
                        ResourceLocation dimTypeId = this.mapProcessor.getMapWorld().getCurrentDimension().getDimensionTypeId();
                        guiGraphics.m_280488_(mc.f_91062_, "MultiWorld ID: " + this.mapProcessor.getMapWorld().getCurrentMultiworld() + " Dim Type: " + (Comparable)((Object)(dimType == null ? "unknown" : dimTypeId)), 5, 265, -1);
                        LayeredRegionManager regions = this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions();
                        guiGraphics.m_280488_(mc.f_91062_, String.format("regions: %d loaded: %d processed: %d viewed: %d benchmarks %s", regions.size(), regions.loadedCount(), this.mapProcessor.getProcessedCount(), lastAmountOfRegionsViewed, WorldMap.textureUploadBenchmark.getTotalsString()), 5, 275, -1);
                        guiGraphics.m_280488_(mc.f_91062_, String.format("toLoad: %d toSave: %d tile pool: %d overlays: %d toLoadBranchCache: %d buffers: %d", this.mapProcessor.getMapSaveLoad().getSizeOfToLoad(), this.mapProcessor.getMapSaveLoad().getToSave().size(), this.mapProcessor.getTilePool().size(), this.mapProcessor.getOverlayManager().getNumberOfUniqueOverlays(), this.mapProcessor.getMapSaveLoad().getSizeOfToLoadBranchCache(), WorldMap.textureDirectBufferPool.size()), 5, 285, -1);
                        long i = Runtime.getRuntime().maxMemory();
                        long j = Runtime.getRuntime().totalMemory();
                        long k = Runtime.getRuntime().freeMemory();
                        long l = j - k;
                        int debugFPS = ((IWorldMapMinecraftClient)mc).getXaeroWorldMap_fps();
                        guiGraphics.m_280488_(mc.f_91062_, String.format("FPS: %d", debugFPS), 5, 295, -1);
                        guiGraphics.m_280488_(mc.f_91062_, String.format("Mem: % 2d%% %03d/%03dMB", l * 100L / i, GuiMap.bytesToMb(l), GuiMap.bytesToMb(i)), 5, 315, -1);
                        guiGraphics.m_280488_(mc.f_91062_, String.format("Allocated: % 2d%% %03dMB", j * 100L / i, GuiMap.bytesToMb(j)), 5, 325, -1);
                        guiGraphics.m_280488_(mc.f_91062_, String.format("Available VRAM: %dMB", this.mapProcessor.getMapLimiter().getAvailableVRAM() / 1024), 5, 335, -1);
                    }
                    int pixelInsideTexX = mouseRegPixelX & 0x3F;
                    int pixelInsideTexZ = mouseRegPixelZ & 0x3F;
                    boolean hasAmbiguousHeight = false;
                    int mouseBlockBottomY = Short.MAX_VALUE;
                    int mouseBlockTopY = Short.MAX_VALUE;
                    ResourceKey<Biome> pointedAtBiome = null;
                    if (tex != null) {
                        mouseBlockBottomY = this.mouseBlockPosY = tex.getHeight(pixelInsideTexX, pixelInsideTexZ);
                        mouseBlockTopY = tex.getTopHeight(pixelInsideTexX, pixelInsideTexZ);
                        hasAmbiguousHeight = this.mouseBlockPosY != mouseBlockTopY;
                        pointedAtBiome = tex.getBiome(pixelInsideTexX, pixelInsideTexZ);
                    }
                    if (hasAmbiguousHeight) {
                        if (mouseBlockTopY != Short.MAX_VALUE) {
                            this.mouseBlockPosY = mouseBlockTopY;
                        } else if (((Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DETECT_AMBIGUOUS_Y)).booleanValue()) {
                            this.mouseBlockPosY = Short.MAX_VALUE;
                        }
                    }
                    matrixStack.m_85849_();
                    if (primaryScaleFBO == null || GuiMap.primaryScaleFBO.f_83917_ != mc.m_91268_().m_85441_() || GuiMap.primaryScaleFBO.f_83918_ != mc.m_91268_().m_85442_()) {
                        primaryScaleFBO = new ImprovedFramebuffer(mc.m_91268_().m_85441_(), mc.m_91268_().m_85442_(), false);
                    }
                    if (GuiMap.primaryScaleFBO.f_83920_ == -1) {
                        matrixStack.m_85849_();
                        return;
                    }
                    primaryScaleFBO.bindAsMainTarget(false);
                    GlStateManager._clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GlStateManager._clear((int)16384, (boolean)Minecraft.f_91002_);
                    matrixStack.m_85841_((float)(1.0 / this.screenScale), (float)(1.0 / this.screenScale), 1.0f);
                    matrixStack.m_252880_((float)(mc.m_91268_().m_85441_() / 2), (float)(mc.m_91268_().m_85442_() / 2), 0.0f);
                    matrixStack.m_85836_();
                    int flooredCameraX = (int)Math.floor(this.cameraX);
                    int flooredCameraZ = (int)Math.floor(this.cameraZ);
                    double primaryOffsetX = 0.0;
                    double primaryOffsetY = 0.0;
                    if (fboScale < 1.0) {
                        double pixelInBlocks = 1.0 / fboScale;
                        int xInFullPixels = (int)Math.floor(this.cameraX / pixelInBlocks);
                        int zInFullPixels = (int)Math.floor(this.cameraZ / pixelInBlocks);
                        double fboOffsetX = (double)xInFullPixels * pixelInBlocks;
                        double fboOffsetZ = (double)zInFullPixels * pixelInBlocks;
                        flooredCameraX = (int)Math.floor(fboOffsetX);
                        flooredCameraZ = (int)Math.floor(fboOffsetZ);
                        primaryOffsetX = fboOffsetX - (double)flooredCameraX;
                        primaryOffsetY = fboOffsetZ - (double)flooredCameraZ;
                        secondaryOffsetX = (this.cameraX - fboOffsetX) * fboScale;
                        secondaryOffsetY = (this.cameraZ - fboOffsetZ) * fboScale;
                    } else {
                        int offset;
                        secondaryOffsetX = (this.cameraX - (double)flooredCameraX) * fboScale;
                        secondaryOffsetY = (this.cameraZ - (double)flooredCameraZ) * fboScale;
                        if (secondaryOffsetX >= 1.0) {
                            offset = (int)secondaryOffsetX;
                            matrixStack.m_252880_((float)(-offset), 0.0f, 0.0f);
                            secondaryOffsetX -= (double)offset;
                        }
                        if (secondaryOffsetY >= 1.0) {
                            offset = (int)secondaryOffsetY;
                            matrixStack.m_252880_(0.0f, (float)offset, 0.0f);
                            secondaryOffsetY -= (double)offset;
                        }
                    }
                    matrixStack.m_85841_((float)fboScale, (float)(-fboScale), 1.0f);
                    matrixStack.m_85837_(-primaryOffsetX, -primaryOffsetY, 0.0);
                    double leftBorder = this.cameraX - (double)(mc.m_91268_().m_85441_() / 2) / this.scale;
                    double rightBorder = leftBorder + (double)mc.m_91268_().m_85441_() / this.scale;
                    double topBorder = this.cameraZ - (double)(mc.m_91268_().m_85442_() / 2) / this.scale;
                    double bottomBorder = topBorder + (double)mc.m_91268_().m_85442_() / this.scale;
                    int minRegX = (int)Math.floor(leftBorder) >> leveledRegionShift;
                    int maxRegX = (int)Math.floor(rightBorder) >> leveledRegionShift;
                    int minRegZ = (int)Math.floor(topBorder) >> leveledRegionShift;
                    int maxRegZ = (int)Math.floor(bottomBorder) >> leveledRegionShift;
                    int blockToTextureConversion = 6 + textureLevel;
                    int minTextureX = (int)Math.floor(leftBorder) >> blockToTextureConversion;
                    int maxTextureX = (int)Math.floor(rightBorder) >> blockToTextureConversion;
                    int minTextureZ = (int)Math.floor(topBorder) >> blockToTextureConversion;
                    int maxTextureZ = (int)Math.floor(bottomBorder) >> blockToTextureConversion;
                    int minLeafRegX = minTextureX << blockToTextureConversion >> 9;
                    int maxLeafRegX = (maxTextureX + 1 << blockToTextureConversion) - 1 >> 9;
                    int minLeafRegZ = minTextureZ << blockToTextureConversion >> 9;
                    int maxLeafRegZ = (maxTextureZ + 1 << blockToTextureConversion) - 1 >> 9;
                    lastAmountOfRegionsViewed = (maxRegX - minRegX + 1) * (maxRegZ - minRegZ + 1);
                    if (this.mapProcessor.getMapLimiter().getMostRegionsAtATime() < lastAmountOfRegionsViewed) {
                        this.mapProcessor.getMapLimiter().setMostRegionsAtATime(lastAmountOfRegionsViewed);
                    }
                    this.regionBuffer.clear();
                    this.branchRegionBuffer.clear();
                    float brightness = this.mapProcessor.getBrightness();
                    int globalRegionCacheHashCode = WorldMap.settings.getRegionCacheHashCode();
                    int globalCaveStart = this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().getLayer(renderedCaveLayer).getCaveStart();
                    int globalCaveDepth = (Integer)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_DEPTH);
                    boolean reloadEverything = (Boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED);
                    int globalReloadVersion = (Integer)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED_VERSION);
                    int globalVersion = (Integer)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.GLOBAL_VERSION);
                    boolean prevWaitingForBranchCache = this.prevWaitingForBranchCache;
                    this.waitingForBranchCache[0] = false;
                    Matrix4f matrix = matrixStack.m_85850_().m_252922_();
                    MultiBufferSource.BufferSource renderTypeBuffers = this.mapProcessor.getCvc().getRenderTypeBuffers();
                    MultiTextureRenderTypeRendererProvider rendererProvider = this.mapProcessor.getMultiTextureRenderTypeRenderers();
                    MultiTextureRenderTypeRenderer withLightRenderer = rendererProvider.getRenderer(t -> RenderSystem.setShaderTexture((int)0, (int)t), MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.MAP);
                    MultiTextureRenderTypeRenderer noLightRenderer = rendererProvider.getRenderer(t -> RenderSystem.setShaderTexture((int)0, (int)t), MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.MAP);
                    VertexConsumer overlayBuffer = renderTypeBuffers.m_6299_(CustomRenderTypes.MAP_COLOR_OVERLAY);
                    LeveledRegion.setComparison(this.mouseBlockPosX >> leveledRegionShift, this.mouseBlockPosZ >> leveledRegionShift, textureLevel, this.mouseBlockPosX >> 9, this.mouseBlockPosZ >> 9);
                    LeveledRegion<?> lastUpdatedRootLeveledRegion = null;
                    boolean cacheOnlyMode = this.mapProcessor.getMapWorld().isCacheOnlyMode();
                    boolean frameRenderedRootTextures = false;
                    boolean loadingLeaves = false;
                    for (int leveledRegX = minRegX; leveledRegX <= maxRegX; ++leveledRegX) {
                        for (int leveledRegZ = minRegZ; leveledRegZ <= maxRegZ; ++leveledRegZ) {
                            boolean rootHasTextures;
                            int leveledSideInRegions = 1 << textureLevel;
                            int leveledSideInBlocks = leveledSideInRegions * 512;
                            int leafRegionMinX = leveledRegX * leveledSideInRegions;
                            int leafRegionMinZ = leveledRegZ * leveledSideInRegions;
                            leveledRegion = null;
                            for (int leafX = 0; leafX < leveledSideInRegions; ++leafX) {
                                for (int leafZ = 0; leafZ < leveledSideInRegions; ++leafZ) {
                                    int regZ;
                                    int regX = leafRegionMinX + leafX;
                                    if (regX < minLeafRegX || regX > maxLeafRegX || (regZ = leafRegionMinZ + leafZ) < minLeafRegZ || regZ > maxLeafRegZ) continue;
                                    MapRegion region = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, regX, regZ, false);
                                    if (region == null) {
                                        region = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, regX, regZ, this.mapProcessor.regionExists(renderedCaveLayer, regX, regZ));
                                    }
                                    if (region == null) continue;
                                    if (leveledRegion == null) {
                                        leveledRegion = this.mapProcessor.getLeveledRegion(renderedCaveLayer, leveledRegX, leveledRegZ, textureLevel);
                                    }
                                    if (prevWaitingForBranchCache) continue;
                                    MapRegion mapRegion = region;
                                    synchronized (mapRegion) {
                                        if (textureLevel != 0 && region.getLoadState() == 0 && region.loadingNeededForBranchLevel != 0 && region.loadingNeededForBranchLevel != textureLevel) {
                                            region.loadingNeededForBranchLevel = 0;
                                            region.getParent().setShouldCheckForUpdatesRecursive(true);
                                        }
                                        if (region.canRequestReload_unsynced() && (!cacheOnlyMode && (reloadEverything && region.getReloadVersion() != globalReloadVersion || region.getCacheHashCode() != globalRegionCacheHashCode || region.caveStartOutdated(globalCaveStart, globalCaveDepth) || region.getVersion() != globalVersion || region.getLoadState() != 2 && region.shouldCache()) || region.getLoadState() == 0 && (!region.isMetaLoaded() || textureLevel == 0 || region.loadingNeededForBranchLevel == textureLevel) || (region.isMetaLoaded() || region.getLoadState() != 0 || !region.hasHadTerrain()) && region.getHighlightsHash() != region.getDim().getHighlightHandler().getRegionHash(region.getRegionX(), region.getRegionZ()))) {
                                            loadingLeaves = true;
                                            region.calculateSortingDistance();
                                            Misc.addToListOfSmallest(10, this.regionBuffer, region);
                                        }
                                        continue;
                                    }
                                }
                            }
                            if (leveledRegion == null) continue;
                            LeveledRegion<?> rootLeveledRegion = leveledRegion.getRootRegion();
                            if (rootLeveledRegion == leveledRegion) {
                                rootLeveledRegion = null;
                            }
                            if (rootLeveledRegion != null && !rootLeveledRegion.isLoaded()) {
                                if (!rootLeveledRegion.recacheHasBeenRequested() && !rootLeveledRegion.reloadHasBeenRequested()) {
                                    rootLeveledRegion.calculateSortingDistance();
                                    Misc.addToListOfSmallest(10, this.branchRegionBuffer, (BranchLeveledRegion)rootLeveledRegion);
                                }
                                this.waitingForBranchCache[0] = true;
                                rootLeveledRegion = null;
                            }
                            if (!this.mapProcessor.isUploadingPaused() && !WorldMap.pauseRequests) {
                                if (leveledRegion instanceof BranchLeveledRegion) {
                                    BranchLeveledRegion branchRegion = (BranchLeveledRegion)((Object)leveledRegion);
                                    branchRegion.checkForUpdates(this.mapProcessor, prevWaitingForBranchCache, this.waitingForBranchCache, this.branchRegionBuffer, textureLevel, minLeafRegX, minLeafRegZ, maxLeafRegX, maxLeafRegZ);
                                }
                                if ((textureLevel != 0 && !prevWaitingForBranchCache || textureLevel == 0 && !this.prevLoadingLeaves) && this.lastFrameRenderedRootTextures && rootLeveledRegion != null && rootLeveledRegion != lastUpdatedRootLeveledRegion) {
                                    BranchLeveledRegion branchRegion = (BranchLeveledRegion)rootLeveledRegion;
                                    branchRegion.checkForUpdates(this.mapProcessor, prevWaitingForBranchCache, this.waitingForBranchCache, this.branchRegionBuffer, textureLevel, minLeafRegX, minLeafRegZ, maxLeafRegX, maxLeafRegZ);
                                    lastUpdatedRootLeveledRegion = rootLeveledRegion;
                                }
                                this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().bumpLoadedRegion((LeveledRegion<?>)leveledRegion);
                                if (rootLeveledRegion != null) {
                                    this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().bumpLoadedRegion(rootLeveledRegion);
                                }
                            } else {
                                this.waitingForBranchCache[0] = prevWaitingForBranchCache;
                            }
                            int minXBlocks = leveledRegX * leveledSideInBlocks;
                            int minZBlocks = leveledRegZ * leveledSideInBlocks;
                            int textureSize = 64 * leveledSideInRegions;
                            int firstTextureX = leveledRegX << 3;
                            int firstTextureZ = leveledRegZ << 3;
                            int levelDiff = 3 - textureLevel;
                            int rootSize = 1 << levelDiff;
                            int maxInsideCoord = rootSize - 1;
                            int firstRootTextureX = firstTextureX >> levelDiff & 7;
                            int firstRootTextureZ = firstTextureZ >> levelDiff & 7;
                            int firstInsideTextureX = firstTextureX & maxInsideCoord;
                            int firstInsideTextureZ = firstTextureZ & maxInsideCoord;
                            boolean hasTextures = leveledRegion.hasTextures();
                            boolean bl = rootHasTextures = rootLeveledRegion != null && rootLeveledRegion.hasTextures();
                            if (hasTextures || rootHasTextures) {
                                for (int o = 0; o < 8; ++o) {
                                    int textureX = minXBlocks + o * textureSize;
                                    if ((double)textureX > rightBorder || (double)(textureX + textureSize) < leftBorder) continue;
                                    for (int p = 0; p < 8; ++p) {
                                        RegionTexture<Object> regionTexture;
                                        int textureZ = minZBlocks + p * textureSize;
                                        if ((double)textureZ > bottomBorder || (double)(textureZ + textureSize) < topBorder) continue;
                                        RegionTexture regionTexture2 = regionTexture = hasTextures ? (RegionTexture)leveledRegion.getTexture(o, p) : null;
                                        if (regionTexture == null || regionTexture.getGlColorTexture() == -1) {
                                            int texture;
                                            int insideZ;
                                            int rootTextureZ;
                                            int insideX;
                                            int rootTextureX;
                                            if (!rootHasTextures || (regionTexture = rootLeveledRegion.getTexture(rootTextureX = firstRootTextureX + ((insideX = firstInsideTextureX + o) >> levelDiff), rootTextureZ = firstRootTextureZ + ((insideZ = firstInsideTextureZ + p) >> levelDiff))) == null || (texture = regionTexture.getGlColorTexture()) == -1) continue;
                                            frameRenderedRootTextures = true;
                                            int insideTextureX = insideX & maxInsideCoord;
                                            int insideTextureZ = insideZ & maxInsideCoord;
                                            float textureX1 = (float)insideTextureX / (float)rootSize;
                                            float textureX2 = (float)(insideTextureX + 1) / (float)rootSize;
                                            float textureY1 = (float)insideTextureZ / (float)rootSize;
                                            float textureY2 = (float)(insideTextureZ + 1) / (float)rootSize;
                                            boolean hasLight = regionTexture.getTextureHasLight();
                                            GuiMap.renderTexturedModalSubRectWithLighting(matrix, textureX - flooredCameraX, textureZ - flooredCameraZ, textureX1, textureY1, textureX2, textureY2, textureSize, textureSize, texture, hasLight, hasLight ? withLightRenderer : noLightRenderer);
                                            continue;
                                        }
                                        int texture = regionTexture.getGlColorTexture();
                                        if (texture == -1) continue;
                                        boolean hasLight = regionTexture.getTextureHasLight();
                                        GuiMap.renderTexturedModalRectWithLighting3(matrix, textureX - flooredCameraX, textureZ - flooredCameraZ, textureSize, textureSize, texture, hasLight, hasLight ? withLightRenderer : noLightRenderer);
                                    }
                                }
                            }
                            if (((LeveledRegion)leveledRegion).loadingAnimation()) {
                                matrixStack.m_85836_();
                                matrixStack.m_85837_((double)leveledSideInBlocks * ((double)leveledRegX + 0.5) - (double)flooredCameraX, (double)leveledSideInBlocks * ((double)leveledRegZ + 0.5) - (double)flooredCameraZ, 0.0);
                                float loadingAnimationPassed = System.currentTimeMillis() - this.loadingAnimationStart;
                                if (loadingAnimationPassed > 0.0f) {
                                    int period = 2000;
                                    int numbersOfActors = 3;
                                    float loadingAnimation = loadingAnimationPassed % (float)period / (float)period * 360.0f;
                                    float step = 360.0f / (float)numbersOfActors;
                                    OptimizedMath.rotatePose(matrixStack, loadingAnimation, (Vector3fc)OptimizedMath.ZP);
                                    int numberOfVisibleActors = 1 + (int)loadingAnimationPassed % (3 * period) / period;
                                    matrixStack.m_85841_((float)leveledSideInRegions, (float)leveledSideInRegions, 1.0f);
                                    for (int i = 0; i < numberOfVisibleActors; ++i) {
                                        OptimizedMath.rotatePose(matrixStack, step, (Vector3fc)OptimizedMath.ZP);
                                        MapRenderHelper.fillIntoExistingBuffer(matrixStack.m_85850_().m_252922_(), overlayBuffer, 16, -8, 32, 8, 1.0f, 1.0f, 1.0f, 1.0f);
                                    }
                                }
                                matrixStack.m_85849_();
                            }
                            if (debugConfig && leveledRegion instanceof MapRegion) {
                                MapRegion region = leveledRegion;
                                matrixStack.m_85836_();
                                matrixStack.m_252880_((float)(512 * region.getRegionX() + 32 - flooredCameraX), (float)(512 * region.getRegionZ() + 32 - flooredCameraZ), 0.0f);
                                matrixStack.m_85841_(10.0f, 10.0f, 1.0f);
                                Misc.drawNormalText(matrixStack, "" + region.getLoadState(), 0.0f, 0.0f, -1, true, renderTypeBuffers);
                                matrixStack.m_85849_();
                            }
                            if (!debugConfig || textureLevel <= 0) continue;
                            for (int leafX = 0; leafX < leveledSideInRegions; ++leafX) {
                                for (int leafZ = 0; leafZ < leveledSideInRegions; ++leafZ) {
                                    boolean currentlyLoading;
                                    int regX = leafRegionMinX + leafX;
                                    int regZ = leafRegionMinZ + leafZ;
                                    MapRegion region = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, regX, regZ, false);
                                    if (region == null) continue;
                                    boolean bl2 = currentlyLoading = this.mapProcessor.getMapSaveLoad().getNextToLoadByViewing() == region;
                                    if (!currentlyLoading && !region.isLoaded() && !region.isMetaLoaded()) continue;
                                    matrixStack.m_85836_();
                                    matrixStack.m_252880_((float)(512 * region.getRegionX() - flooredCameraX), (float)(512 * region.getRegionZ() - flooredCameraZ), 0.0f);
                                    float r = 0.0f;
                                    float g = 0.0f;
                                    float b = 0.0f;
                                    float a = 0.1569f;
                                    if (currentlyLoading) {
                                        b = 1.0f;
                                        r = 1.0f;
                                    } else if (region.isLoaded()) {
                                        g = 1.0f;
                                    } else {
                                        g = 1.0f;
                                        r = 1.0f;
                                    }
                                    MapRenderHelper.fillIntoExistingBuffer(matrixStack.m_85850_().m_252922_(), overlayBuffer, 0, 0, 512, 512, r, g, b, a);
                                    matrixStack.m_85849_();
                                }
                            }
                        }
                    }
                    this.lastFrameRenderedRootTextures = frameRenderedRootTextures;
                    LibShaders.WORLD_MAP.setBrightness(brightness);
                    LibShaders.WORLD_MAP.setWithLight(true);
                    rendererProvider.draw(withLightRenderer);
                    LibShaders.WORLD_MAP.setWithLight(false);
                    rendererProvider.draw(noLightRenderer);
                    LeveledRegion<?> nextToLoad = this.mapProcessor.getMapSaveLoad().getNextToLoadByViewing();
                    boolean shouldRequest = false;
                    shouldRequest = nextToLoad != null ? nextToLoad.shouldAllowAnotherRegionToLoad() : true;
                    boolean bl = shouldRequest = shouldRequest && this.mapProcessor.getAffectingLoadingFrequencyCount() < 16;
                    if (shouldRequest && !WorldMap.pauseRequests) {
                        int i;
                        int toRequest = 2;
                        int counter = 0;
                        for (i = 0; i < this.branchRegionBuffer.size() && counter < toRequest; ++i) {
                            BranchLeveledRegion region = this.branchRegionBuffer.get(i);
                            if (region.reloadHasBeenRequested() || region.recacheHasBeenRequested() || region.isLoaded()) continue;
                            region.setReloadHasBeenRequested(true, "Gui");
                            this.mapProcessor.getMapSaveLoad().requestBranchCache(region, "Gui");
                            if (counter == 0) {
                                this.mapProcessor.getMapSaveLoad().setNextToLoadByViewing(region);
                            }
                            ++counter;
                        }
                        toRequest = 1;
                        counter = 0;
                        if (!prevWaitingForBranchCache) {
                            for (i = 0; i < this.regionBuffer.size() && counter < toRequest; ++i) {
                                MapRegion region = this.regionBuffer.get(i);
                                if (region == nextToLoad && this.regionBuffer.size() > 1) continue;
                                leveledRegion = region;
                                synchronized (leveledRegion) {
                                    if (!region.canRequestReload_unsynced()) {
                                        continue;
                                    }
                                    if (region.getLoadState() == 2) {
                                        region.requestRefresh(this.mapProcessor);
                                    } else {
                                        this.mapProcessor.getMapSaveLoad().requestLoad(region, "Gui");
                                    }
                                    if (counter == 0) {
                                        this.mapProcessor.getMapSaveLoad().setNextToLoadByViewing(region);
                                    }
                                    ++counter;
                                    if (region.getLoadState() == 4) {
                                        break;
                                    }
                                    continue;
                                }
                            }
                        }
                    }
                    this.prevWaitingForBranchCache = this.waitingForBranchCache[0];
                    this.prevLoadingLeaves = loadingLeaves;
                    int highlightChunkX = this.mouseBlockPosX >> 4;
                    int highlightChunkZ = this.mouseBlockPosZ >> 4;
                    int chunkHighlightLeftX = highlightChunkX << 4;
                    int chunkHighlightRightX = highlightChunkX + 1 << 4;
                    int chunkHighlightTopZ = highlightChunkZ << 4;
                    int chunkHighlightBottomZ = highlightChunkZ + 1 << 4;
                    MapRenderHelper.renderDynamicHighlight(matrixStack, overlayBuffer, flooredCameraX, flooredCameraZ, chunkHighlightLeftX, chunkHighlightRightX, chunkHighlightTopZ, chunkHighlightBottomZ, 0.0f, 0.0f, 0.0f, 0.2f, 1.0f, 1.0f, 1.0f, 0.1569f);
                    MapTileSelection mapTileSelectionToRender = this.mapTileSelection;
                    if (mapTileSelectionToRender == null && this.f_96541_.f_91080_ instanceof ExportScreen) {
                        mapTileSelectionToRender = ((ExportScreen)this.f_96541_.f_91080_).getSelection();
                    }
                    if (mapTileSelectionToRender != null) {
                        MapRenderHelper.renderDynamicHighlight(matrixStack, overlayBuffer, flooredCameraX, flooredCameraZ, mapTileSelectionToRender.getLeft() << 4, mapTileSelectionToRender.getRight() + 1 << 4, mapTileSelectionToRender.getTop() << 4, mapTileSelectionToRender.getBottom() + 1 << 4, 0.0f, 0.0f, 0.0f, 0.2f, 1.0f, 0.5f, 0.5f, 0.4f);
                        if (SupportMods.pac() && !this.mapProcessor.getMapWorld().isUsingCustomDimension()) {
                            int playerX = (int)Math.floor(this.player.m_20185_());
                            int playerZ = (int)Math.floor(this.player.m_20189_());
                            int playerChunkX = playerX >> 4;
                            int playerChunkZ = playerZ >> 4;
                            int claimDistance = SupportMods.xaeroPac.getClaimDistance();
                            int claimableAreaLeft = playerChunkX - claimDistance;
                            int claimableAreaTop = playerChunkZ - claimDistance;
                            int claimableAreaRight = playerChunkX + claimDistance;
                            int claimableAreaBottom = playerChunkZ + claimDistance;
                            int claimableAreaHighlightLeftX = claimableAreaLeft << 4;
                            int claimableAreaHighlightRightX = claimableAreaRight + 1 << 4;
                            int claimableAreaHighlightTopZ = claimableAreaTop << 4;
                            int claimableAreaHighlightBottomZ = claimableAreaBottom + 1 << 4;
                            MapRenderHelper.renderDynamicHighlight(matrixStack, overlayBuffer, flooredCameraX, flooredCameraZ, claimableAreaHighlightLeftX, claimableAreaHighlightRightX, claimableAreaHighlightTopZ, claimableAreaHighlightBottomZ, 0.0f, 0.0f, 1.0f, 0.3f, 0.0f, 0.0f, 1.0f, 0.15f);
                        }
                    }
                    RenderSystem.disableCull();
                    renderTypeBuffers.m_109911_();
                    RenderSystem.enableCull();
                    primaryScaleFBO.m_83970_();
                    primaryScaleFBO.bindDefaultFramebuffer(mc);
                    matrixStack.m_85849_();
                    matrixStack.m_85836_();
                    matrixStack.m_85841_((float)secondaryScale, (float)secondaryScale, 1.0f);
                    primaryScaleFBO.m_83956_();
                    GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
                    GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
                    RenderSystem.depthMask((boolean)false);
                    VertexConsumer colorBackgroundConsumer = renderTypeBuffers.m_6299_(CustomRenderTypes.MAP_COLOR_FILLER);
                    int lineX = -mc.m_91268_().m_85441_() / 2;
                    int lineY = mc.m_91268_().m_85442_() / 2 - 5;
                    int lineW = mc.m_91268_().m_85441_();
                    int lineH = 6;
                    MapRenderHelper.fillIntoExistingBuffer(matrixStack.m_85850_().m_252922_(), colorBackgroundConsumer, lineX, lineY, lineX + lineW, lineY + lineH, 0.0f, 0.0f, 0.0f, 1.0f);
                    lineX = mc.m_91268_().m_85441_() / 2 - 5;
                    lineY = -mc.m_91268_().m_85442_() / 2;
                    lineW = 6;
                    lineH = mc.m_91268_().m_85442_();
                    MapRenderHelper.fillIntoExistingBuffer(matrixStack.m_85850_().m_252922_(), colorBackgroundConsumer, lineX, lineY, lineX + lineW, lineY + lineH, 0.0f, 0.0f, 0.0f, 1.0f);
                    renderTypeBuffers.m_109911_();
                    RenderType mainFrameRenderType = CustomRenderTypes.GUI_BILINEAR;
                    if (SupportMods.vivecraft) {
                        mainFrameRenderType = CustomRenderTypes.MAP_FRAME_TEXTURE_OVER_TRANSPARENT;
                    }
                    MultiTextureRenderTypeRenderer mainFrameRenderer = rendererProvider.getRenderer(t -> RenderSystem.setShaderTexture((int)0, (int)t), MultiTextureRenderTypeRendererProvider::defaultTextureBind, mainFrameRenderType);
                    BufferBuilder mainFrameVertexConsumer = mainFrameRenderer.begin(primaryScaleFBO.getFramebufferTexture());
                    GuiMap.renderTexturedModalRect(matrixStack.m_85850_().m_252922_(), (VertexConsumer)mainFrameVertexConsumer, (float)(-mc.m_91268_().m_85441_() / 2) - (float)secondaryOffsetX, (float)(-mc.m_91268_().m_85442_() / 2) - (float)secondaryOffsetY, 0, 0, GuiMap.primaryScaleFBO.f_83917_, GuiMap.primaryScaleFBO.f_83918_, GuiMap.primaryScaleFBO.f_83917_, GuiMap.primaryScaleFBO.f_83918_, 1.0f, 1.0f, 1.0f, 1.0f);
                    rendererProvider.draw(mainFrameRenderer);
                    RenderSystem.depthMask((boolean)true);
                    matrixStack.m_85849_();
                    matrixStack.m_85841_((float)this.scale, (float)this.scale, 1.0f);
                    double screenSizeBasedScale = scaleMultiplier;
                    WorldMap.trackedPlayerRenderer.update(mc);
                    try {
                        this.viewed = WorldMap.mapElementRenderHandler.render(this, guiGraphics, renderTypeBuffers, rendererProvider, this.cameraX, this.cameraZ, mc.m_91268_().m_85441_(), mc.m_91268_().m_85442_(), screenSizeBasedScale, this.scale, playerDimDiv, mousePosX, mousePosZ, brightness, renderedCaveLayer != Integer.MAX_VALUE, this.viewed, mc, partialTicks);
                    }
                    catch (Throwable t2) {
                        WorldMap.LOGGER.error("error rendering map elements", t2);
                        throw t2;
                    }
                    this.viewedInList = false;
                    matrixStack.m_85836_();
                    matrixStack.m_252880_(0.0f, 0.0f, 970.0f);
                    VertexConsumer regularUIObjectConsumer = renderTypeBuffers.m_6299_(CustomRenderTypes.GUI_BILINEAR);
                    if (((Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.FOOTSTEPS)).booleanValue()) {
                        ArrayList<Double[]> footprints;
                        ArrayList<Double[]> claimableAreaHighlightBottomZ = footprints = this.mapProcessor.getFootprints();
                        synchronized (claimableAreaHighlightBottomZ) {
                            for (int i = 0; i < footprints.size(); ++i) {
                                Double[] coords = footprints.get(i);
                                this.setColourBuffer(1.0f, 0.1f, 0.1f, 1.0f);
                                this.drawDotOnMap(matrixStack, regularUIObjectConsumer, coords[0] / playerDimDiv - this.cameraX, coords[1] / playerDimDiv - this.cameraZ, 0.0f, 1.0 / this.scale);
                            }
                        }
                    }
                    if (((Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.ARROW)).booleanValue()) {
                        boolean toTheLeft = scaledPlayerX < leftBorder;
                        boolean toTheRight = scaledPlayerX > rightBorder;
                        boolean down = scaledPlayerZ > bottomBorder;
                        boolean up = scaledPlayerZ < topBorder;
                        float configuredR = 1.0f;
                        float configuredG = 1.0f;
                        float configuredB = 1.0f;
                        int effectiveArrowColorIndex = (Integer)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.ARROW_COLOR);
                        if (effectiveArrowColorIndex == -2 && !SupportMods.minimap()) {
                            effectiveArrowColorIndex = 0;
                        }
                        if (effectiveArrowColorIndex == -2 && SupportMods.xaeroMinimap.getArrowColorIndex() == -1) {
                            effectiveArrowColorIndex = -1;
                        }
                        if (effectiveArrowColorIndex == -1) {
                            int rgb = Misc.getTeamColour((Entity)(mc.f_91074_ == null ? mc.m_91288_() : mc.f_91074_));
                            if (rgb == -1) {
                                effectiveArrowColorIndex = 0;
                            } else {
                                configuredR = (float)(rgb >> 16 & 0xFF) / 255.0f;
                                configuredG = (float)(rgb >> 8 & 0xFF) / 255.0f;
                                configuredB = (float)(rgb & 0xFF) / 255.0f;
                            }
                        } else if (effectiveArrowColorIndex == -2) {
                            float[] c = SupportMods.xaeroMinimap.getArrowColor();
                            if (c == null) {
                                effectiveArrowColorIndex = 0;
                            } else {
                                configuredR = c[0];
                                configuredG = c[1];
                                configuredB = c[2];
                            }
                        }
                        if (effectiveArrowColorIndex >= 0) {
                            float[] c = WorldMapConfigConstants.ARROW_COLORS[effectiveArrowColorIndex];
                            configuredR = c[0];
                            configuredG = c[1];
                            configuredB = c[2];
                        }
                        if (toTheLeft || toTheRight || up || down) {
                            double arrowX = scaledPlayerX;
                            double arrowZ = scaledPlayerZ;
                            float a = 0.0f;
                            if (toTheLeft) {
                                a = up ? 1.5f : (down ? 0.5f : 1.0f);
                                arrowX = leftBorder;
                            } else if (toTheRight) {
                                a = up ? 2.5f : (down ? 3.5f : 3.0f);
                                arrowX = rightBorder;
                            }
                            if (down) {
                                arrowZ = bottomBorder;
                            } else if (up) {
                                if (a == 0.0f) {
                                    a = 2.0f;
                                }
                                arrowZ = topBorder;
                            }
                            this.setColourBuffer(0.0f, 0.0f, 0.0f, 0.9f);
                            this.drawFarArrowOnMap(matrixStack, regularUIObjectConsumer, arrowX - this.cameraX, arrowZ + 2.0 * screenSizeBasedScale / this.scale - this.cameraZ, a, screenSizeBasedScale / this.scale);
                            this.setColourBuffer(configuredR, configuredG, configuredB, 1.0f);
                            this.drawFarArrowOnMap(matrixStack, regularUIObjectConsumer, arrowX - this.cameraX, arrowZ - this.cameraZ, a, screenSizeBasedScale / this.scale);
                        } else {
                            this.setColourBuffer(0.0f, 0.0f, 0.0f, 0.9f);
                            this.drawArrowOnMap(matrixStack, regularUIObjectConsumer, scaledPlayerX - this.cameraX, scaledPlayerZ + 2.0 * screenSizeBasedScale / this.scale - this.cameraZ, this.player.m_146908_(), screenSizeBasedScale / this.scale);
                            this.setColourBuffer(configuredR, configuredG, configuredB, 1.0f);
                            this.drawArrowOnMap(matrixStack, regularUIObjectConsumer, scaledPlayerX - this.cameraX, scaledPlayerZ - this.cameraZ, this.player.m_146908_(), screenSizeBasedScale / this.scale);
                        }
                    }
                    this.f_96541_.m_91097_().m_174784_(WorldMap.guiTextures);
                    GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
                    GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
                    renderTypeBuffers.m_109911_();
                    this.f_96541_.m_91097_().m_174784_(WorldMap.guiTextures);
                    GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
                    GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
                    matrixStack.m_85849_();
                    matrixStack.m_85849_();
                    VertexConsumer backgroundVertexBuffer = renderTypeBuffers.m_6299_(CustomRenderTypes.MAP_COLOR_OVERLAY);
                    int cursorDisplayOffset = 0;
                    if (((Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.COORDINATES)).booleanValue()) {
                        String coordsString = "X: " + this.mouseBlockPosX;
                        if (mouseBlockBottomY != Short.MAX_VALUE) {
                            coordsString = coordsString + " Y: " + mouseBlockBottomY;
                        }
                        if (hasAmbiguousHeight && mouseBlockTopY != Short.MAX_VALUE) {
                            coordsString = coordsString + " (" + mouseBlockTopY + ")";
                        }
                        coordsString = coordsString + " Z: " + this.mouseBlockPosZ;
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, this.f_96547_, coordsString, this.f_96543_ / 2, 2 + cursorDisplayOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f, backgroundVertexBuffer);
                        cursorDisplayOffset += 10;
                    }
                    if (((Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_HOVERED_BIOME)).booleanValue() && pointedAtBiome != null) {
                        ResourceLocation biomeRL = pointedAtBiome.m_135782_();
                        String biomeText = biomeRL == null ? I18n.m_118938_((String)"gui.xaero_wm_unknown_biome", (Object[])new Object[0]) : I18n.m_118938_((String)("biome." + biomeRL.m_135827_() + "." + biomeRL.m_135815_()), (Object[])new Object[0]);
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, this.f_96547_, biomeText, this.f_96543_ / 2, 2 + cursorDisplayOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f, backgroundVertexBuffer);
                    }
                    int subtleTooltipOffset = 12;
                    if (((Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_ZOOM)).booleanValue()) {
                        String zoomString = (double)Math.round(destScale * 1000.0) / 1000.0 + "x";
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.f_91062_, zoomString, this.f_96543_ / 2, this.f_96544_ - subtleTooltipOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f, backgroundVertexBuffer);
                    }
                    if (this.mapProcessor.getMapWorld().getCurrentDimension().getFullReloader() != null) {
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.f_91062_, FULL_RELOAD_IN_PROGRESS, this.f_96543_ / 2, this.f_96544_ - (subtleTooltipOffset += 12), -1, 0.0f, 0.0f, 0.0f, 0.4f, backgroundVertexBuffer);
                    }
                    if (this.mapProcessor.getMapWorld().isUsingUnknownDimensionType()) {
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.f_91062_, UNKNOWN_DIMENSION_TYPE2, this.f_96543_ / 2, this.f_96544_ - (subtleTooltipOffset += 24), -1, 0.0f, 0.0f, 0.0f, 0.4f, backgroundVertexBuffer);
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.f_91062_, UNKNOWN_DIMENSION_TYPE1, this.f_96543_ / 2, this.f_96544_ - (subtleTooltipOffset += 12), -1, 0.0f, 0.0f, 0.0f, 0.4f, backgroundVertexBuffer);
                    }
                    if (((Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_CAVE_MODE_START)).booleanValue()) {
                        subtleTooltipOffset += 12;
                        if (globalCaveStart != Integer.MAX_VALUE && globalCaveStart != Integer.MIN_VALUE) {
                            String caveModeStartString = I18n.m_118938_((String)"gui.xaero_wm_cave_mode_start_display", (Object[])new Object[]{globalCaveStart});
                            MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.f_91062_, caveModeStartString, this.f_96543_ / 2, this.f_96544_ - subtleTooltipOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f, backgroundVertexBuffer);
                        }
                    }
                    if (SupportMods.minimap() && (subWorldNameToRender = SupportMods.xaeroMinimap.getSubWorldNameToRender()) != null) {
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.f_91062_, subWorldNameToRender, this.f_96543_ / 2, this.f_96544_ - (subtleTooltipOffset += 24), -1, 0.0f, 0.0f, 0.0f, 0.4f, backgroundVertexBuffer);
                    }
                    discoveredForHighlights = mouseBlockBottomY != Short.MAX_VALUE;
                    Component subtleHighlightTooltip = this.mapProcessor.getMapWorld().getCurrentDimension().getHighlightHandler().getBlockHighlightSubtleTooltip(this.mouseBlockPosX, this.mouseBlockPosZ, discoveredForHighlights);
                    if (subtleHighlightTooltip != null) {
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.f_91062_, subtleHighlightTooltip, this.f_96543_ / 2, this.f_96544_ - (subtleTooltipOffset += 12), -1, 0.0f, 0.0f, 0.0f, 0.4f, backgroundVertexBuffer);
                    }
                    renderTypeBuffers.m_109911_();
                    this.overWaypointsMenu = false;
                    this.overPlayersMenu = false;
                    boolean bl3 = renderingMenus = this.waypointMenu || this.playersMenu;
                    if (renderingMenus) {
                        matrixStack.m_85836_();
                        matrixStack.m_252880_(0.0f, 0.0f, 972.0f);
                    }
                    if (this.waypointMenu) {
                        HoveredMapElementHolder<?, ?> hovered2;
                        if (SupportMods.xaeroMinimap.getWaypointsSorted() != null && (hovered2 = SupportMods.xaeroMinimap.renderWaypointsMenu(guiGraphics, this, this.scale, this.f_96543_, this.f_96544_, scaledMouseX, scaledMouseY, this.leftMouseButton.isDown, this.leftMouseButton.clicked, this.viewed, mc)) != null) {
                            this.overWaypointsMenu = true;
                            if (hovered2.getElement() instanceof Waypoint) {
                                this.viewed = hovered2;
                                this.viewedInList = true;
                                if (this.leftMouseButton.clicked) {
                                    this.cameraDestination = new int[]{(int)((Waypoint)this.viewed.getElement()).getRenderX(), (int)((Waypoint)this.viewed.getElement()).getRenderZ()};
                                    this.leftMouseButton.isDown = false;
                                    boolean closeWaypointsWhenHopping = (Boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.CLOSE_WAYPOINTS_AFTER_HOP);
                                    if (closeWaypointsWhenHopping) {
                                        this.onWaypointsButton(this.waypointsButton);
                                    }
                                }
                            }
                        }
                    } else if (this.playersMenu && (hovered = WorldMap.trackedPlayerMenuRenderer.renderMenu(guiGraphics, this, this.scale, this.f_96543_, this.f_96544_, scaledMouseX, scaledMouseY, this.leftMouseButton.isDown, this.leftMouseButton.clicked, this.viewed, mc)) != null) {
                        this.overPlayersMenu = true;
                        if (hovered.getElement() instanceof PlayerTrackerMapElement && WorldMap.trackedPlayerMenuRenderer.canJumpTo((PlayerTrackerMapElement)hovered.getElement())) {
                            this.viewed = hovered;
                            this.viewedInList = true;
                            if (this.leftMouseButton.clicked) {
                                PlayerTrackerMapElement clickedPlayer = (PlayerTrackerMapElement)this.viewed.getElement();
                                MapDimension clickedPlayerDim = this.mapProcessor.getMapWorld().getDimension(clickedPlayer.getDimension());
                                DimensionType clickedPlayerDimType = MapDimension.getDimensionType(clickedPlayerDim, clickedPlayer.getDimension(), this.mapProcessor.getWorldDimensionTypeRegistry());
                                double clickedPlayerDimDiv = this.mapProcessor.getMapWorld().getCurrentDimension().calculateDimDiv(this.mapProcessor.getWorldDimensionTypeRegistry(), clickedPlayerDimType);
                                double jumpX = clickedPlayer.getX() / clickedPlayerDimDiv;
                                double jumpZ = clickedPlayer.getZ() / clickedPlayerDimDiv;
                                this.cameraDestination = new int[]{(int)jumpX, (int)jumpZ};
                                this.leftMouseButton.isDown = false;
                            }
                        }
                    }
                    if (renderingMenus) {
                        matrixStack.m_85849_();
                    }
                    if (SupportMods.minimap()) {
                        SupportMods.xaeroMinimap.drawSetChange(guiGraphics);
                    }
                    if (SupportMods.pac()) {
                        SupportMods.xaeroPac.onMapRender(this.f_96541_, matrixStack, scaledMouseX, scaledMouseY, partialTicks, this.mapProcessor.getWorld().m_46472_().m_135782_(), highlightChunkX, highlightChunkZ);
                    }
                } else if (!mapLoaded) {
                    this.renderLoadingScreen(guiGraphics);
                } else if (isLocked) {
                    this.renderMessageScreen(guiGraphics, I18n.m_118938_((String)"gui.xaero_current_map_locked1", (Object[])new Object[0]), I18n.m_118938_((String)"gui.xaero_current_map_locked2", (Object[])new Object[0]));
                } else if (noWorldMapEffect) {
                    this.renderMessageScreen(guiGraphics, I18n.m_118938_((String)"gui.xaero_no_world_map_message", (Object[])new Object[0]));
                } else if (!allowedBasedOnItem) {
                    String configuredMapItemString = (String)configManager.getEffective(WorldMapProfiledConfigOptions.MAP_ITEM);
                    this.renderMessageScreen(guiGraphics, I18n.m_118938_((String)"gui.xaero_no_world_map_item_message", (Object[])new Object[0]), mapItem.m_41466_().getString() + " (" + configuredMapItemString + ")");
                }
            } else {
                this.renderLoadingScreen(guiGraphics);
            }
            this.mapSwitchingGui.renderText(guiGraphics, this.f_96541_, scaledMouseX, scaledMouseY, this.f_96543_, this.f_96544_);
            guiGraphics.m_280218_(WorldMap.guiTextures, this.f_96543_ - 34, 2, 0, 37, 32, 32);
        }
        matrixStack.m_85836_();
        matrixStack.m_252880_(0.0f, 0.0f, 973.0f);
        super.m_88315_(guiGraphics, scaledMouseX, scaledMouseY, partialTicks);
        if (this.rightClickMenu != null) {
            this.rightClickMenu.m_88315_(guiGraphics, scaledMouseX, scaledMouseY, partialTicks);
        }
        matrixStack.m_252880_(0.0f, 0.0f, 10.0f);
        if (mc.f_91080_ == this) {
            if (!(this.renderTooltips(guiGraphics, scaledMouseX, scaledMouseY, partialTicks) || this.leftMouseButton.isDown || this.rightMouseButton.isDown)) {
                if (this.viewed != null) {
                    Tooltip hoveredTooltip = this.hoveredElementTooltipHelper(this.viewed, this.viewedInList);
                    if (hoveredTooltip != null) {
                        hoveredTooltip.drawBox(guiGraphics, scaledMouseX, scaledMouseY, this.f_96543_, this.f_96544_);
                    }
                } else {
                    object2 = this.mapProcessor.renderThreadPauseSync;
                    synchronized (object2) {
                        Component bluntHighlightTooltip;
                        if (!this.mapProcessor.isRenderingPaused() && this.mapProcessor.getCurrentWorldId() != null && this.mapProcessor.getMapSaveLoad().isRegionDetectionComplete() && (bluntHighlightTooltip = this.mapProcessor.getMapWorld().getCurrentDimension().getHighlightHandler().getBlockHighlightBluntTooltip(this.mouseBlockPosX, this.mouseBlockPosZ, discoveredForHighlights)) != null) {
                            new Tooltip(bluntHighlightTooltip).drawBox(guiGraphics, scaledMouseX, scaledMouseY, this.f_96543_, this.f_96544_);
                        }
                    }
                }
            }
            matrixStack.m_252880_(0.0f, 0.0f, 1.0f);
            this.mapProcessor.getMessageBoxRenderer().render(guiGraphics, this.mapProcessor.getMessageBox(), this.f_96547_, 1, this.f_96544_ / 2, false);
        }
        matrixStack.m_85849_();
        this.rightMouseButton.clicked = false;
        this.leftMouseButton.clicked = false;
        this.noUploadingLimits = this.cameraX == cameraXBefore && this.cameraZ == cameraZBefore && scaleBefore == this.scale;
        MapRenderHelper.restoreDefaultShaderBlendState();
    }

    protected void renderPreDropdown(GuiGraphics guiGraphics, int scaledMouseX, int scaledMouseY, float partialTicks) {
        super.renderPreDropdown(guiGraphics, scaledMouseX, scaledMouseY, partialTicks);
        if (this.waypointMenu) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().postMapRender(guiGraphics, this, scaledMouseX, scaledMouseY, this.f_96543_, this.f_96544_, partialTicks);
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.postMapRender(guiGraphics, this, scaledMouseX, scaledMouseY, this.f_96543_, this.f_96544_, partialTicks);
        }
        this.mapSwitchingGui.postMapRender(guiGraphics, this.f_96541_, scaledMouseX, scaledMouseY, this.f_96543_, this.f_96544_);
    }

    private <E, C> Tooltip hoveredElementTooltipHelper(HoveredMapElementHolder<E, C> hovered, boolean viewedInList) {
        return hovered.getRenderer().getReader().getTooltip(hovered.getElement(), hovered.getRenderer().getContext(), viewedInList);
    }

    private void renderLoadingScreen(GuiGraphics guiGraphics) {
        this.renderMessageScreen(guiGraphics, "Preparing World Map...");
    }

    private void renderMessageScreen(GuiGraphics guiGraphics, String message) {
        this.renderMessageScreen(guiGraphics, message, null);
    }

    private void renderMessageScreen(GuiGraphics guiGraphics, String message, String message2) {
        PoseStack matrixStack = guiGraphics.m_280168_();
        guiGraphics.m_280509_(0, 0, this.f_96541_.m_91268_().m_85441_(), this.f_96541_.m_91268_().m_85442_(), -16777216);
        matrixStack.m_85836_();
        matrixStack.m_252880_(0.0f, 0.0f, 500.0f);
        guiGraphics.m_280137_(this.f_96541_.f_91062_, message, this.f_96541_.m_91268_().m_85445_() / 2, this.f_96541_.m_91268_().m_85446_() / 2, -1);
        if (message2 != null) {
            guiGraphics.m_280137_(this.f_96541_.f_91062_, message2, this.f_96541_.m_91268_().m_85445_() / 2, this.f_96541_.m_91268_().m_85446_() / 2 + 10, -1);
        }
        matrixStack.m_85849_();
    }

    public void drawDotOnMap(PoseStack matrixStack, VertexConsumer guiLinearBuffer, double x, double z, float angle, double sc) {
        this.drawObjectOnMap(matrixStack, guiLinearBuffer, x, z, angle, sc, 2.5f, 2.5f, 0, 69, 5, 5, 9729);
    }

    public void drawArrowOnMap(PoseStack matrixStack, VertexConsumer guiLinearBuffer, double x, double z, float angle, double sc) {
        this.drawObjectOnMap(matrixStack, guiLinearBuffer, x, z, angle, sc, 13.0f, 5.0f, 0, 0, 26, 28, 9729);
    }

    public void drawFarArrowOnMap(PoseStack matrixStack, VertexConsumer guiLinearBuffer, double x, double z, float angle, double sc) {
        this.drawObjectOnMap(matrixStack, guiLinearBuffer, x, z, angle * 90.0f, sc, 27.0f, 13.0f, 26, 0, 54, 13, 9729);
    }

    public void drawObjectOnMap(PoseStack matrixStack, VertexConsumer guiLinearBuffer, double x, double z, float angle, double sc, float offX, float offY, int textureX, int textureY, int w, int h, int filter) {
        matrixStack.m_85836_();
        matrixStack.m_85837_(x, z, 0.0);
        matrixStack.m_85841_((float)sc, (float)sc, 1.0f);
        if (angle != 0.0f) {
            OptimizedMath.rotatePose(matrixStack, angle, (Vector3fc)OptimizedMath.ZP);
        }
        Matrix4f matrix = matrixStack.m_85850_().m_252922_();
        GuiMap.renderTexturedModalRect(matrix, guiLinearBuffer, -offX, -offY, textureX, textureY, w, h, 256.0f, 256.0f, this.colourBuffer[0], this.colourBuffer[1], this.colourBuffer[2], this.colourBuffer[3]);
        matrixStack.m_85849_();
    }

    public static void renderTexturedModalRectWithLighting2(Matrix4f matrix, float x, float y, float width, float height, int texture, MultiTextureRenderTypeRenderer renderer) {
        GuiMap.buildTexturedModalRectWithLighting(matrix, renderer.begin(texture), x, y, width, height);
    }

    public static void renderTexturedModalRectWithLighting3(Matrix4f matrix, float x, float y, float width, float height, int texture, boolean hasLight, MultiTextureRenderTypeRenderer renderer) {
        GuiMap.buildTexturedModalRectWithLighting(matrix, renderer.begin(texture), x, y, width, height);
    }

    public static void renderTexturedModalSubRectWithLighting(Matrix4f matrix, float x, float y, float textureX1, float textureY1, float textureX2, float textureY2, float width, float height, int texture, boolean hasLight, MultiTextureRenderTypeRenderer renderer) {
        GuiMap.buildTexturedModalSubRectWithLighting(matrix, renderer.begin(texture), x, y, textureX1, textureY1, textureX2, textureY2, width, height);
    }

    public static void buildTexturedModalRectWithLighting(Matrix4f matrix, BufferBuilder vertexBuffer, float x, float y, float width, float height) {
        vertexBuffer.m_252986_(matrix, x + 0.0f, y + height, 0.0f).m_7421_(0.0f, 1.0f).m_5752_();
        vertexBuffer.m_252986_(matrix, x + width, y + height, 0.0f).m_7421_(1.0f, 1.0f).m_5752_();
        vertexBuffer.m_252986_(matrix, x + width, y + 0.0f, 0.0f).m_7421_(1.0f, 0.0f).m_5752_();
        vertexBuffer.m_252986_(matrix, x + 0.0f, y + 0.0f, 0.0f).m_7421_(0.0f, 0.0f).m_5752_();
    }

    public static void buildTexturedModalSubRectWithLighting(Matrix4f matrix, BufferBuilder vertexBuffer, float x, float y, float textureX1, float textureY1, float textureX2, float textureY2, float width, float height) {
        vertexBuffer.m_252986_(matrix, x + 0.0f, y + height, 0.0f).m_7421_(textureX1, textureY2).m_5752_();
        vertexBuffer.m_252986_(matrix, x + width, y + height, 0.0f).m_7421_(textureX2, textureY2).m_5752_();
        vertexBuffer.m_252986_(matrix, x + width, y + 0.0f, 0.0f).m_7421_(textureX2, textureY1).m_5752_();
        vertexBuffer.m_252986_(matrix, x + 0.0f, y + 0.0f, 0.0f).m_7421_(textureX1, textureY1).m_5752_();
    }

    public static void renderTexturedModalRect(Matrix4f matrix, VertexConsumer vertexBuffer, float x, float y, int textureX, int textureY, float width, float height, float textureWidth, float textureHeight, float r, float g, float b, float a) {
        float normalizedTextureX = (float)textureX / textureWidth;
        float normalizedTextureY = (float)textureY / textureHeight;
        float normalizedTextureX2 = ((float)textureX + width) / textureWidth;
        float normalizedTextureY2 = ((float)textureY + height) / textureHeight;
        vertexBuffer.m_252986_(matrix, x + 0.0f, y + height, 0.0f).m_85950_(r, g, b, a).m_7421_(normalizedTextureX, normalizedTextureY2).m_5752_();
        vertexBuffer.m_252986_(matrix, x + width, y + height, 0.0f).m_85950_(r, g, b, a).m_7421_(normalizedTextureX2, normalizedTextureY2).m_5752_();
        vertexBuffer.m_252986_(matrix, x + width, y + 0.0f, 0.0f).m_85950_(r, g, b, a).m_7421_(normalizedTextureX2, normalizedTextureY).m_5752_();
        vertexBuffer.m_252986_(matrix, x + 0.0f, y + 0.0f, 0.0f).m_85950_(r, g, b, a).m_7421_(normalizedTextureX, normalizedTextureY).m_5752_();
    }

    public void mapClicked(int button, int x, int y) {
        if (button == 1) {
            if (this.viewedOnMousePress != null && this.viewedOnMousePress.isRightClickValid() && (!(this.viewedOnMousePress.getElement() instanceof Waypoint) || SupportMods.xaeroMinimap.waypointExists((Waypoint)this.viewedOnMousePress.getElement()))) {
                this.handleRightClick(this.viewedOnMousePress, (int)((double)x / this.screenScale), (int)((double)y / this.screenScale));
                this.mouseDownPosX = -1;
                this.mouseDownPosY = -1;
                this.mapTileSelection = null;
            } else {
                this.handleRightClick(this, (int)((double)x / this.screenScale), (int)((double)y / this.screenScale));
            }
        }
    }

    private void handleRightClick(IRightClickableElement target, int x, int y) {
        if (this.rightClickMenu != null) {
            this.rightClickMenu.setClosed(true);
        }
        this.rightClickMenu = GuiRightClickMenu.getMenu(target, this, x, y, 150);
    }

    public boolean m_5534_(char par1, int par2) {
        boolean result = super.m_5534_(par1, par2);
        if (this.waypointMenu && SupportMods.xaeroMinimap.getWaypointMenuRenderer().charTyped()) {
            return true;
        }
        if (this.playersMenu && WorldMap.trackedPlayerMenuRenderer.charTyped()) {
            return true;
        }
        return result;
    }

    public boolean m_7933_(int par1, int par2, int par3) {
        if (par1 == 258) {
            ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
            boolean minimapRadarConfig = (Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.MINIMAP_RADAR);
            if (this.tabPressed && SupportMods.minimap() && minimapRadarConfig && Minecraft.m_91087_().f_91066_.f_92099_.m_90832_(par1, par2)) {
                return true;
            }
            this.tabPressed = true;
        }
        boolean result = super.m_7933_(par1, par2, par3);
        if (this.isUsingTextField()) {
            if (this.waypointMenu && SupportMods.xaeroMinimap.getWaypointMenuRenderer().keyPressed(this, par1)) {
                result = true;
            } else if (this.playersMenu && WorldMap.trackedPlayerMenuRenderer.keyPressed(this, par1)) {
                result = true;
            }
        } else {
            result = this.onInputPress(par1 != -1 ? InputConstants.Type.KEYSYM : InputConstants.Type.SCANCODE, par1 != -1 ? par1 : par2) || result;
        }
        return result;
    }

    public boolean m_7920_(int par1, int par2, int par3) {
        if (par1 == 258) {
            this.tabPressed = false;
        }
        if (this.onInputRelease(par1 != -1 ? InputConstants.Type.KEYSYM : InputConstants.Type.SCANCODE, par1 != -1 ? par1 : par2)) {
            return true;
        }
        return super.m_7920_(par1, par2, par3);
    }

    private static long bytesToMb(long bytes) {
        return bytes / 1024L / 1024L;
    }

    private void setColourBuffer(float r, float g, float b, float a) {
        this.colourBuffer[0] = r;
        this.colourBuffer[1] = g;
        this.colourBuffer[2] = b;
        this.colourBuffer[3] = a;
    }

    private boolean isUsingTextField() {
        AbstractWidget currentFocused = (AbstractWidget)this.m_7222_();
        return currentFocused != null && currentFocused.m_93696_() && currentFocused instanceof EditBox;
    }

    public void m_86600_() {
        super.m_86600_();
        if (this.waypointMenu) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().tick();
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.tick();
        }
        this.caveModeOptions.tick(this);
    }

    public KeyMapping getTrackedPlayerKeyBinding() {
        if (SupportMods.minimap()) {
            return SupportMods.xaeroMinimap.getToggleAllyPlayersKey();
        }
        return ControlsRegister.keyToggleTrackedPlayers;
    }

    private boolean onInputPress(InputConstants.Type type, int code) {
        IRightClickableElement hoverTarget;
        if (KeyMappingUtils.inputMatches((InputConstants.Type)type, (int)code, (KeyMapping)ControlsRegister.keyOpenSettings, (int)0)) {
            this.onSettingsButton(this.settingsButton);
            return true;
        }
        boolean result = false;
        if (KeyMappingUtils.inputMatches((InputConstants.Type)type, (int)code, (KeyMapping)this.f_96541_.f_91066_.f_92099_, (int)0)) {
            this.f_96541_.f_91066_.f_92099_.m_7249_(true);
            result = true;
        }
        if (KeyMappingUtils.inputMatches((InputConstants.Type)type, (int)code, (KeyMapping)ControlsRegister.keyOpenMap, (int)0)) {
            this.goBack();
            result = true;
        }
        if (KeyMappingUtils.inputMatches((InputConstants.Type)type, (int)code, (KeyMapping)this.getTrackedPlayerKeyBinding(), (int)0)) {
            WorldMap.trackedPlayerMenuRenderer.onShowPlayersButton(this, this.f_96543_, this.f_96544_);
            return true;
        }
        if ((type == InputConstants.Type.KEYSYM && code == 257 || KeyMappingUtils.inputMatches((InputConstants.Type)type, (int)code, (KeyMapping)ControlsRegister.keyQuickConfirm, (int)0)) && this.mapSwitchingGui.active) {
            this.mapSwitchingGui.confirm(this, this.f_96541_, this.f_96543_, this.f_96544_);
            result = true;
        }
        if (KeyMappingUtils.inputMatches((InputConstants.Type)type, (int)code, (KeyMapping)ControlsRegister.keyToggleDimension, (int)1)) {
            this.onDimensionToggleButton(this.dimensionToggleButton);
            result = true;
        }
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onMapKeyPressed(type, code, this);
            result = true;
        }
        if (SupportMods.pac()) {
            boolean bl = result = SupportMods.xaeroPac.onMapKeyPressed(type, code, this) || result;
        }
        if ((hoverTarget = this.getHoverTarget()) != null && type == InputConstants.Type.KEYSYM) {
            boolean isValid = hoverTarget.isRightClickValid();
            if (isValid) {
                if (hoverTarget instanceof HoveredMapElementHolder && ((HoveredMapElementHolder)hoverTarget).getElement() instanceof Waypoint) {
                    switch (code) {
                        case 72: {
                            SupportMods.xaeroMinimap.disableWaypoint((Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                            this.closeRightClick();
                            result = true;
                            break;
                        }
                        case 261: {
                            SupportMods.xaeroMinimap.deleteWaypoint((Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                            this.closeRightClick();
                            result = true;
                        }
                    }
                } else if (SupportMods.pac() && hoverTarget instanceof HoveredMapElementHolder && ((HoveredMapElementHolder)hoverTarget).getElement() instanceof PlayerTrackerMapElement) {
                    switch (code) {
                        case 67: {
                            SupportMods.xaeroPac.openPlayerConfigScreen((Screen)this, (Screen)this, (PlayerTrackerMapElement)((HoveredMapElementHolder)hoverTarget).getElement());
                            this.closeRightClick();
                            result = true;
                        }
                    }
                }
            } else {
                this.closeRightClick();
            }
        }
        return result;
    }

    private double getCurrentMapCoordinateScale() {
        return this.mapProcessor.getMapWorld().getCurrentDimension().calculateDimScale(this.mapProcessor.getWorldDimensionTypeRegistry());
    }

    private boolean onInputRelease(InputConstants.Type type, int code) {
        boolean result = false;
        if (KeyMappingUtils.inputMatches((InputConstants.Type)type, (int)code, (KeyMapping)this.f_96541_.f_91066_.f_92099_, (int)0)) {
            this.f_96541_.f_91066_.f_92099_.m_7249_(false);
            result = true;
        }
        if (SupportMods.minimap() && SupportMods.xaeroMinimap.onMapKeyReleased(type, code, this)) {
            result = true;
        }
        if (SupportMods.minimap() && this.lastViewedDimensionId != null && !this.isUsingTextField()) {
            IRightClickableElement hoverTarget;
            ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
            boolean waypointsConfig = (Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS);
            int waypointDestinationX = this.mouseBlockPosX;
            int waypointDestinationY = this.mouseBlockPosY;
            int waypointDestinationZ = this.mouseBlockPosZ;
            double waypointDestinationCoordinateScale = this.mouseBlockCoordinateScale;
            boolean waypointDestinationRightClick = false;
            if (this.rightClickMenu != null && this.rightClickMenu.getTarget() == this) {
                waypointDestinationX = this.rightClickX;
                waypointDestinationY = this.rightClickY;
                waypointDestinationZ = this.rightClickZ;
                waypointDestinationCoordinateScale = this.rightClickCoordinateScale;
                waypointDestinationRightClick = true;
            }
            if (KeyMappingUtils.inputMatches((InputConstants.Type)type, (int)code, (KeyMapping)SupportMods.xaeroMinimap.getWaypointKeyBinding(), (int)0) && waypointsConfig) {
                SupportMods.xaeroMinimap.createWaypoint(this, waypointDestinationX, waypointDestinationY == Short.MAX_VALUE ? Short.MAX_VALUE : waypointDestinationY + 1, waypointDestinationZ, waypointDestinationCoordinateScale, waypointDestinationRightClick);
                this.closeRightClick();
                result = true;
            }
            if (KeyMappingUtils.inputMatches((InputConstants.Type)type, (int)code, (KeyMapping)SupportMods.xaeroMinimap.getTempWaypointKeyBinding(), (int)0) && waypointsConfig) {
                this.closeRightClick();
                SupportMods.xaeroMinimap.createTempWaypoint(waypointDestinationX, waypointDestinationY == Short.MAX_VALUE ? Short.MAX_VALUE : waypointDestinationY + 1, waypointDestinationZ, waypointDestinationCoordinateScale, waypointDestinationRightClick);
                result = true;
            }
            if ((hoverTarget = this.getHoverTarget()) != null && !KeyMappingUtils.inputMatches((InputConstants.Type)type, (int)code, (KeyMapping)ControlsRegister.keyOpenMap, (int)0) && type == InputConstants.Type.KEYSYM) {
                boolean isValid = hoverTarget.isRightClickValid();
                if (isValid) {
                    if (hoverTarget instanceof HoveredMapElementHolder && ((HoveredMapElementHolder)hoverTarget).getElement() instanceof Waypoint) {
                        switch (code) {
                            case 84: {
                                SupportMods.xaeroMinimap.teleportToWaypoint((Screen)this, (Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                                this.closeRightClick();
                                result = true;
                                break;
                            }
                            case 69: {
                                SupportMods.xaeroMinimap.openWaypoint(this, (Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                                this.closeRightClick();
                                result = true;
                            }
                        }
                    } else if (hoverTarget instanceof HoveredMapElementHolder && ((HoveredMapElementHolder)hoverTarget).getElement() instanceof PlayerTrackerMapElement) {
                        switch (code) {
                            case 84: {
                                new PlayerTeleporter().teleportToPlayer((Screen)this, this.mapProcessor.getMapWorld(), (PlayerTrackerMapElement)((HoveredMapElementHolder)hoverTarget).getElement());
                                this.closeRightClick();
                                result = true;
                            }
                        }
                    }
                } else {
                    this.closeRightClick();
                }
            }
        }
        return result;
    }

    private IRightClickableElement getHoverTarget() {
        return this.rightClickMenu != null ? this.rightClickMenu.getTarget() : this.viewed;
    }

    private void unfocusAll() {
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().unfocusAll();
        }
        WorldMap.trackedPlayerMenuRenderer.unfocusAll();
        this.caveModeOptions.unfocusAll();
        this.m_7522_(null);
    }

    public void closeRightClick() {
        if (this.rightClickMenu != null) {
            this.rightClickMenu.setClosed(true);
        }
    }

    public void onRightClickClosed() {
        this.rightClickMenu = null;
        this.mapTileSelection = null;
    }

    private void closeDropdowns() {
        if (this.openDropdown != null) {
            this.openDropdown.setClosed(true);
        }
    }

    @Override
    public ArrayList<RightClickOption> getRightClickOptions() {
        ArrayList<RightClickOption> options = new ArrayList<RightClickOption>();
        options.add(new RightClickOption("gui.xaero_right_click_map_title", options.size(), this){

            @Override
            public void onAction(Screen screen) {
            }
        });
        ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        boolean coordinatesConfig = (Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.COORDINATES);
        boolean waypointsConfig = (Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS);
        if (!(!coordinatesConfig || SupportMods.minimap() && SupportMods.xaeroMinimap.hidingWaypointCoordinates())) {
            if (this.mapTileSelection != null) {
                String chunkOption = this.mapTileSelection.getStartX() != this.mapTileSelection.getEndX() || this.mapTileSelection.getStartZ() != this.mapTileSelection.getEndZ() ? String.format("C: (%d;%d):(%d;%d)", this.mapTileSelection.getLeft(), this.mapTileSelection.getTop(), this.mapTileSelection.getRight(), this.mapTileSelection.getBottom()) : String.format("C: (%d;%d)", this.mapTileSelection.getLeft(), this.mapTileSelection.getTop());
                options.add(new RightClickOption(chunkOption, options.size(), this){

                    @Override
                    public void onAction(Screen screen) {
                    }
                });
            }
            options.add(new RightClickOption(String.format(this.rightClickY != Short.MAX_VALUE ? "X: %1$d, Y: %2$d, Z: %3$d" : "X: %1$d, Z: %3$d", this.rightClickX, this.rightClickY, this.rightClickZ), options.size(), this){

                @Override
                public void onAction(Screen screen) {
                }
            });
        }
        if (SupportMods.minimap() && waypointsConfig) {
            options.add(new RightClickOption("gui.xaero_right_click_map_create_waypoint", options.size(), this){

                @Override
                public void onAction(Screen screen) {
                    SupportMods.xaeroMinimap.createWaypoint(GuiMap.this, GuiMap.this.rightClickX, GuiMap.this.rightClickY == Short.MAX_VALUE ? Short.MAX_VALUE : GuiMap.this.rightClickY + 1, GuiMap.this.rightClickZ, GuiMap.this.rightClickCoordinateScale, true);
                }
            }.setNameFormatArgs(KeyMappingUtils.getKeyName((KeyMapping)SupportMods.xaeroMinimap.getWaypointKeyBinding())));
            options.add(new RightClickOption("gui.xaero_right_click_map_create_temporary_waypoint", options.size(), this){

                @Override
                public void onAction(Screen screen) {
                    SupportMods.xaeroMinimap.createTempWaypoint(GuiMap.this.rightClickX, GuiMap.this.rightClickY == Short.MAX_VALUE ? Short.MAX_VALUE : GuiMap.this.rightClickY + 1, GuiMap.this.rightClickZ, GuiMap.this.rightClickCoordinateScale, true);
                }
            }.setNameFormatArgs(KeyMappingUtils.getKeyName((KeyMapping)SupportMods.xaeroMinimap.getTempWaypointKeyBinding())));
        }
        MapDimension currentDimension = this.mapProcessor.getMapWorld().getCurrentDimension();
        if (!this.f_96541_.f_91072_.m_105205_() || currentDimension != null) {
            boolean teleportAllowed = (Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.MAP_TELEPORT_ALLOWED);
            if (teleportAllowed && (this.rightClickY != Short.MAX_VALUE || !this.f_96541_.f_91072_.m_105205_())) {
                options.add(new RightClickOption("gui.xaero_right_click_map_teleport", options.size(), this){

                    @Override
                    public void onAction(Screen screen) {
                        MapDimension currentDimension = GuiMap.this.mapProcessor.getMapWorld().getCurrentDimension();
                        if (!(((GuiMap)GuiMap.this).f_96541_.f_91072_.m_105205_() && currentDimension == null || GuiMap.this.rightClickY == Short.MAX_VALUE && ((GuiMap)GuiMap.this).f_96541_.f_91072_.m_105205_())) {
                            ResourceKey<Level> tpDim = GuiMap.this.rightClickDim != ((GuiMap)GuiMap.this).f_96541_.f_91073_.m_46472_() ? GuiMap.this.rightClickDim : null;
                            new MapTeleporter().teleport((Screen)GuiMap.this, GuiMap.this.mapProcessor.getMapWorld(), GuiMap.this.rightClickX, GuiMap.this.rightClickY == Short.MAX_VALUE ? Short.MAX_VALUE : GuiMap.this.rightClickY + 1, GuiMap.this.rightClickZ, tpDim);
                        }
                    }
                });
            } else if (!teleportAllowed) {
                options.add(new RightClickOption("gui.xaero_wm_right_click_map_teleport_not_allowed", options.size(), this){

                    @Override
                    public void onAction(Screen screen) {
                    }
                });
            } else {
                options.add(new RightClickOption("gui.xaero_right_click_map_cant_teleport", options.size(), this){

                    @Override
                    public void onAction(Screen screen) {
                    }
                });
            }
        } else {
            options.add(new RightClickOption("gui.xaero_right_click_map_cant_teleport_world", options.size(), this){

                @Override
                public void onAction(Screen screen) {
                }
            });
        }
        if (SupportMods.minimap()) {
            options.add(new RightClickOption("gui.xaero_right_click_map_share_location", options.size(), this){

                @Override
                public void onAction(Screen screen) {
                    SupportMods.xaeroMinimap.shareLocation(GuiMap.this, GuiMap.this.rightClickX, GuiMap.this.rightClickY == Short.MAX_VALUE ? Short.MAX_VALUE : GuiMap.this.rightClickY + 1, GuiMap.this.rightClickZ);
                }
            });
            if (waypointsConfig) {
                options.add(new RightClickOption("gui.xaero_right_click_map_waypoints_menu", options.size(), this){

                    @Override
                    public void onAction(Screen screen) {
                        SupportMods.xaeroMinimap.openWaypointsMenu(GuiMap.this.f_96541_, GuiMap.this);
                    }
                }.setNameFormatArgs(KeyMappingUtils.getKeyName((KeyMapping)SupportMods.xaeroMinimap.getTempWaypointsMenuKeyBinding())));
            }
        }
        if (SupportMods.pac()) {
            SupportMods.xaeroPac.addRightClickOptions(this, options, this.mapTileSelection, this.mapProcessor);
        }
        options.add(new RightClickOption("gui.xaero_right_click_box_map_export", options.size(), this){

            @Override
            public void onAction(Screen screen) {
                GuiMap.this.onExportButton(GuiMap.this.exportButton);
            }
        });
        options.add(new RightClickOption("gui.xaero_right_click_box_map_settings", options.size(), this){

            @Override
            public void onAction(Screen screen) {
                GuiMap.this.onSettingsButton(GuiMap.this.settingsButton);
            }
        }.setNameFormatArgs(KeyMappingUtils.getKeyName((KeyMapping)ControlsRegister.keyOpenSettings)));
        return options;
    }

    @Override
    public boolean isRightClickValid() {
        return true;
    }

    @Override
    public int getRightClickTitleBackgroundColor() {
        return -10461088;
    }

    public boolean shouldSkipWorldRender() {
        return true;
    }

    public double getUserScale() {
        return this.userScale;
    }

    public Button getRadarButton() {
        return this.radarButton;
    }

    public void onDropdownOpen(DropDownWidget menu) {
        super.onDropdownOpen(menu);
        this.unfocusAll();
    }

    public void onDropdownClosed(DropDownWidget menu) {
        super.onDropdownClosed(menu);
        if (menu == this.rightClickMenu) {
            this.onRightClickClosed();
        }
    }

    public void onCaveModeStartSet() {
        this.caveModeOptions.onCaveModeStartSet(this);
    }

    public MapDimension getFutureDimension() {
        return this.futureDimension;
    }

    public MapProcessor getMapProcessor() {
        return this.mapProcessor;
    }

    public void enableCaveModeOptions() {
        if (!this.caveModeOptions.isEnabled()) {
            this.caveModeOptions.toggle(this);
        }
    }

    public void m_169411_(GuiEventListener current) {
        super.m_169411_(current);
    }

    static {
        identityMatrix.identity();
    }
}

