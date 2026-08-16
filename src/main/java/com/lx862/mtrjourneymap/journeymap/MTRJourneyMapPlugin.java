package com.lx862.mtrjourneymap.journeymap;

import com.lx862.mtrjourneymap.MTRSurveyor;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

/**
 * JourneyMap plugin for displaying MTR station/depot markers on the map.
 * This class is discovered and instantiated by JourneyMap via the @ClientPlugin
 * annotation.
 * Do NOT reference this class from anywhere else in the mod to avoid
 * ClassNotFoundException
 * when JourneyMap is not installed.
 */
@ParametersAreNonnullByDefault
@ClientPlugin
public class MTRJourneyMapPlugin implements IClientPlugin {
    private static IClientAPI clientAPI = null;

    @Override
    public void initialize(IClientAPI api) {
        this.clientAPI = api;
        MTRSurveyor.LOGGER.info("[{}] JourneyMap API initialized!", MTRSurveyor.MOD_NAME);
    }

    @Override
    public String getModId() {
        return MTRSurveyor.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent event) {
        // No events needed for now
    }

    /**
     * Get the JourneyMap client API instance.
     * 
     * @return the API instance, or null if JourneyMap is not loaded or not yet
     *         initialized
     */
    public static IClientAPI getAPI() {
        return clientAPI;
    }
}
