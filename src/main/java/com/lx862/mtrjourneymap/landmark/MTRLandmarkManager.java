package com.lx862.mtrjourneymap.landmark;

import com.lx862.mtrjourneymap.*;
import com.lx862.mtrjourneymap.config.MTRSurveyorConfig;
import com.lx862.mtrjourneymap.util.MTRUtil;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.model.MapImage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.mtr.core.data.*;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.mtr.mod.data.IGui;

import java.util.*;

public class MTRLandmarkManager {

    // Track currently displayed markers so we can diff on sync
    private static final Map<String, MarkerOverlay> activeMarkers = new HashMap<>();

    public static void syncLandmarks(SyncOrigin syncOrigin, Level world, MTRDataSummary dataSummary,
            MTRSurveyorConfig config) {
        if (world == null)
            return;

        // Try to get the JourneyMap API without directly referencing the plugin class
        IClientAPI api = getJourneyMapAPI();
        if (api == null) {
            // JourneyMap not loaded or not initialized yet
            return;
        }

        long startMs = System.currentTimeMillis();

        if (config.debugLog) {
            MTRSurveyor.LOGGER.info("[{}] Syncing {} landmarks for world {} ({})", MTRSurveyor.MOD_NAME,
                    syncOrigin.sourceName(), world.dimension().location(), syncOrigin.reason());
        }

        Long2ObjectOpenHashMap<AreaBase<?, ?>> mtrAreas = new Long2ObjectOpenHashMap<>();
        Set<String> desiredMarkerIds = new HashSet<>();

        if (config.enabled) {
            if (config.visibility.showStationLandmarks) {
                for (AreaBase<?, ?> area : new ArrayList<>(dataSummary.getData().stations)) {
                    mtrAreas.put(area.getId(), area);
                }
            }

            if (config.visibility.showDepotLandmarks) {
                for (AreaBase<?, ?> area : new ArrayList<>(dataSummary.getData().depots)) {
                    mtrAreas.put(area.getId(), area);
                }
            }

            for (AreaBase<?, ?> area : mtrAreas.values()) {
                if (shouldBeFilteredOut(area, dataSummary, config.visibility.showEmptyStation))
                    continue;

                String markerId = getMarkerId(area);
                desiredMarkerIds.add(markerId);

                try {
                    MarkerOverlay marker = createMarker(area, dataSummary, world);
                    if (marker != null) {
                        api.show(marker);
                        activeMarkers.put(markerId, marker);
                    }
                } catch (Exception e) {
                    MTRSurveyor.LOGGER.error("[{}] Failed to show marker for {}", MTRSurveyor.MOD_NAME, area.getName(),
                            e);
                }
            }
        }

        // Remove markers that are no longer desired
        Iterator<Map.Entry<String, MarkerOverlay>> it = activeMarkers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, MarkerOverlay> entry = it.next();
            if (!desiredMarkerIds.contains(entry.getKey())) {
                api.remove(entry.getValue());
                it.remove();
            }
        }

        if (config.debugLog) {
            MTRSurveyor.LOGGER.info("[{}] Took {}ms to sync. {} markers active.", MTRSurveyor.MOD_NAME,
                    (System.currentTimeMillis() - startMs), activeMarkers.size());
        }
    }

    private static String getMarkerId(AreaBase<?, ?> areaBase) {
        String type = (areaBase instanceof Station) ? "station" : "depot";
        return MTRUtil.getTransportModeName(areaBase.getTransportMode()) + "_" + type + "_"
                + areaBase.getHexId().toLowerCase();
    }

    private static MarkerOverlay createMarker(AreaBase<?, ?> areaBase, MTRDataSummary mtrDataSummary, Level world) {
        if (areaBase instanceof Station station) {
            return createStationMarker(station, mtrDataSummary, world);
        } else if (areaBase instanceof Depot depot) {
            return createDepotMarker(depot, mtrDataSummary, world);
        }
        return null;
    }

    private static MarkerOverlay createStationMarker(Station station, MTRDataSummary mtrDataSummary, Level world) {
        String markerId = getMarkerId(station);
        String stationName = IGui.formatStationName(station.getName());
        BlockPos pos = new BlockPos(
                (int) station.getCenter().getX(),
                (int) station.getCenter().getY(),
                (int) station.getCenter().getZ());

        // Build the icon from our marker texture
        ResourceLocation iconRL = getMarkerIcon(station.getTransportMode(), false);
        MapImage icon = new MapImage(iconRL, 16, 16);
        icon.setAnchorX(8);
        icon.setAnchorY(8);
        icon.setColor(station.getColor() | 0xFF000000); // ensure alpha

        MarkerOverlay marker = new MarkerOverlay(MTRSurveyor.MOD_ID, markerId, pos, icon);
        marker.setDimension(world.dimension());
        marker.setLabel(stationName);

        // Build description with routes
        List<MTRDataSummary.BasicRouteInfo> routesInStation = mtrDataSummary.getRoutesInStation(station);
        StringBuilder desc = new StringBuilder();
        desc.append("Fare zone: ").append(station.getZone1());
        if (routesInStation != null && !routesInStation.isEmpty()) {
            desc.append("\nRoutes: ");
            for (int i = 0; i < routesInStation.size(); i++) {
                if (i > 0)
                    desc.append(", ");
                desc.append(IGui.formatStationName(routesInStation.get(i).name()));
            }
        }
        marker.setTitle(desc.toString());

        return marker;
    }

    private static MarkerOverlay createDepotMarker(Depot depot, MTRDataSummary mtrDataSummary, Level world) {
        String markerId = getMarkerId(depot);
        String depotName = IGui.formatStationName(depot.getName());
        BlockPos pos = new BlockPos(
                (int) depot.getCenter().getX(),
                (int) depot.getCenter().getY(),
                (int) depot.getCenter().getZ());

        ResourceLocation iconRL = getMarkerIcon(depot.getTransportMode(), true);
        MapImage icon = new MapImage(iconRL, 16, 16);
        icon.setAnchorX(8);
        icon.setAnchorY(8);
        icon.setColor(depot.getColor() | 0xFF000000);

        MarkerOverlay marker = new MarkerOverlay(MTRSurveyor.MOD_ID, markerId, pos, icon);
        marker.setDimension(world.dimension());
        marker.setLabel(depotName);

        return marker;
    }

    private static ResourceLocation getMarkerIcon(TransportMode transportMode, boolean isDepot) {
        String modeName = MTRUtil.getTransportModeName(transportMode);
        String type = isDepot ? "depot" : "station";
        return MTRSurveyor.id("textures/atlas/marker/" + modeName + "_" + type + ".png");
    }

    private static boolean shouldBeFilteredOut(AreaBase<?, ?> areaBase, MTRDataSummary dataSummary,
            boolean showEmptyStation) {
        if (areaBase instanceof Station station) {
            List<MTRDataSummary.BasicRouteInfo> routes = dataSummary.getRoutesInStation(station);
            return !showEmptyStation && (routes == null || routes.isEmpty());
        }
        return false;
    }

    public record SyncOrigin(String sourceName, String reason) {
        public static SyncOrigin ofServer(String reason) {
            return new SyncOrigin("server", reason);
        }

        public static SyncOrigin ofClient(String reason) {
            return new SyncOrigin("client", reason);
        }
    }

    /**
     * Gets the JourneyMap API via reflection to avoid direct class reference to
     * MTRJourneyMapPlugin.
     * This prevents ClassNotFoundException when JourneyMap is not installed.
     */
    private static IClientAPI getJourneyMapAPI() {
        try {
            Class<?> pluginClass = Class.forName("com.lx862.mtrjourneymap.journeymap.MTRJourneyMapPlugin");
            return (IClientAPI) pluginClass.getMethod("getAPI").invoke(null);
        } catch (ClassNotFoundException e) {
            // JourneyMap API not available - this is expected when JourneyMap is not
            // installed
            return null;
        } catch (Exception e) {
            MTRSurveyor.LOGGER.error("[{}] Failed to get JourneyMap API", MTRSurveyor.MOD_NAME, e);
            return null;
        }
    }
}
