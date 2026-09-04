package com.lx862.mtrjourneymap;

import com.lx862.mtrjourneymap.config.MTRSurveyorConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MTRSurveyor.MOD_ID)
public class MTRSurveyor {

    public static final String MOD_ID = "mtrjourneymap";
    public static final String MOD_NAME = "MTR JourneyMap Integration";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public MTRSurveyor() {
        MTRSurveyorConfig.init();

        if (MTRSurveyorConfig.INSTANCE.formalInitLog) {
            LOGGER.info("[{}] Mod loaded!", MOD_NAME);
        } else {
            LOGGER.info("[{}] You get a landmark, you get a landmark, every-nyan gets a landmark! >w<", MOD_NAME);
        }

        MinecraftForge.EVENT_BUS.register(this);
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

        ClientSyncHandler.onClientTick();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
