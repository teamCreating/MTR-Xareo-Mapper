package com.lx862.mtrjourneymap;

import com.lx862.mtrjourneymap.config.MTRSurveyorConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MTRSurveyor.MOD_ID)
public class MTRSurveyor {

    public static final String MOD_ID = "mtrjourneymap";
    public static final String MOD_NAME = "MTR JourneyMap Integration";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    private static MinecraftServer serverInstance = null;

    public MTRSurveyor() {
        MTRSurveyorConfig.init();

        if (MTRSurveyorConfig.INSTANCE.formalInitLog) {
            LOGGER.info("[{}] Mod loaded!", MOD_NAME);
        } else {
            LOGGER.info("[{}] You get a landmark, you get a landmark, every-nyan gets a landmark! >w<", MOD_NAME);
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        serverInstance = event.getServer();
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        serverInstance = null;
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        Commands.register(event.getDispatcher());
    }

    public static MinecraftServer getServerInstance() {
        return serverInstance;
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
