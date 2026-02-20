package com.lx862.mtrsurveyor;

import com.lx862.mtrsurveyor.config.MTRSurveyorConfig;
import com.lx862.mtrsurveyor.integration.XaeroIntegration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MTRSurveyor.MOD_ID)
public class MTRSurveyor {

    public static final String MOD_ID = "mtrsurveyor";
    public static final String MOD_NAME = "[CRTools]MTR:Xaero Mapper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    private static MinecraftServer serverInstance = null;

    public MTRSurveyor() {
        MTRSurveyorConfig.init();

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        if (MTRSurveyorConfig.INSTANCE.formalInitLog.get()) {
            LOGGER.info("[{}] Mod loaded!", MOD_NAME);
        } else {
            LOGGER.info("[{}] You get a landmark, you get a landmark, every-nyan gets a landmark! >w<", MOD_NAME);
        }

        if (XaeroIntegration.isXaeroLoaded()) {
            LOGGER.info("[{}] Xaero's Minimap detected - waypoint sync enabled", MOD_NAME);
        } else {
            LOGGER.info("[{}] Xaero's Minimap not found - waypoint sync disabled", MOD_NAME);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        serverInstance = event.getServer();
    }

    /**
     * Register client-side commands. This fires on the client and works
     * even when connected to a remote server that doesn't have this mod.
     */
    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandRegistration.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        if (!XaeroIntegration.isXaeroLoaded())
            return;

        XaeroIntegration.onClientTick();
    }

    public static MinecraftServer getServerInstance() {
        return serverInstance;
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
