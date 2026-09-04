package com.lx862.mtrjourneymap.landmark;

import com.lx862.mtrjourneymap.MTRDataSummary;
import com.lx862.mtrjourneymap.MTRSurveyor;
import com.lx862.mtrjourneymap.config.MTRSurveyorConfig;
import com.lx862.mtrjourneymap.util.MTRUtil;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.model.MapImage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.mtr.core.data.Depot;
import org.mtr.core.data.NameColorDataBase;
import org.mtr.core.data.Platform;
import org.mtr.core.data.Position;
import org.mtr.core.data.Route;
import org.mtr.core.data.RoutePlatformData;
import org.mtr.core.data.Station;
import org.mtr.core.data.TransportMode;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.data.IGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds JourneyMap {@link MarkerOverlay} markers from MTR data.
 *
 * <p>Feature parity with the Xaero edition: a "station" mode (one marker per
 * station), a "platform" mode (one marker per platform, with route &amp;
 * destination info on hover) and optional depot markers. Markers are rendered
 * with per-transport-mode icons.</p>
 */
public class MTRLandmarkManager {

    // Track currently displayed markers so we can clean them up on the next sync
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

        // Build the full set of markers that should be displayed right now
        Map<String, MarkerOverlay> desiredMarkers = new LinkedHashMap<>();
        if (config.enabled) {
            if (MTRSurveyorConfig.MODE_PLATFORM.equalsIgnoreCase(config.waypointMode)) {
                collectPlatformMarkers(desiredMarkers, world, config);
            } else {
                collectStationMarkers(desiredMarkers, dataSummary, world, config);
            }

            if (config.visibility.showDepotLandmarks) {
                collectDepotMarkers(desiredMarkers, world);
            }
        }

        // JourneyMap rejects api.show() for an ID that is already displayed, so
        // every sync removes the previous set and re-adds fresh markers
        for (MarkerOverlay marker : activeMarkers.values()) {
            try {
                api.remove(marker);
            } catch (Exception e) {
                MTRSurveyor.LOGGER.warn("[{}] Failed to remove marker during resync: {}", MTRSurveyor.MOD_NAME,
                        e.getMessage());
            }
        }
        activeMarkers.clear();

        for (Map.Entry<String, MarkerOverlay> entry : desiredMarkers.entrySet()) {
            try {
                api.show(entry.getValue());
                activeMarkers.put(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                MTRSurveyor.LOGGER.error("[{}] Failed to show marker for {}", MTRSurveyor.MOD_NAME, entry.getKey(), e);
            }
        }

        if (config.debugLog) {
            MTRSurveyor.LOGGER.info("[{}] [{}] Took {}ms to sync. {} markers active (mode: {}).",
                    MTRSurveyor.MOD_NAME, syncOrigin.sourceName(), (System.currentTimeMillis() - startMs),
                    activeMarkers.size(), config.waypointMode);
        }
    }

    // Station mode: one marker per station, placed at the station centre
    private static void collectStationMarkers(Map<String, MarkerOverlay> out, MTRDataSummary dataSummary, Level world,
            MTRSurveyorConfig config) {
        if (!config.visibility.showStationLandmarks)
            return;

        for (Station station : collectStations()) {
            if (shouldBeFilteredOut(station, dataSummary, config.visibility.showEmptyStation))
                continue;

            out.put(getMarkerId(station), createStationMarker(station, dataSummary, world));
        }
    }

    // Platform mode: one marker per platform, labelled with the platform
    // number and showing the station name plus route/destination info on hover
    private static void collectPlatformMarkers(Map<String, MarkerOverlay> out, Level world,
            MTRSurveyorConfig config) {
        if (!config.visibility.showStationLandmarks)
            return;

        // Build a map of platformId -> routes passing through it, since
        // platform.routes is not populated on the client side
        Map<Long, List<Route>> platformRouteMap = new HashMap<>();
        for (Route route : collectRoutes()) {
            List<RoutePlatformData> rpList = route.getRoutePlatforms();
            if (rpList == null)
                continue;
            for (RoutePlatformData rpd : rpList) {
                if (rpd.getPlatform() != null) {
                    platformRouteMap.computeIfAbsent(rpd.getPlatform().getId(), k -> new ArrayList<>()).add(route);
                }
            }
        }

        for (Station station : collectStations()) {
            String stationName = IGui.formatStationName(station.getName());
            if (stationName == null || stationName.isEmpty())
                continue;

            for (Object railObj : new ArrayList<>(station.savedRails)) {
                if (!(railObj instanceof Platform platform))
                    continue;

                String markerId = getMarkerId(platform);
                Position midPos = platform.getMidPosition();
                BlockPos pos = new BlockPos((int) midPos.getX(), (int) midPos.getY(), (int) midPos.getZ());

                // Label = platform name/number
                String platformName = platform.getName();
                if (platformName == null || platformName.isEmpty()) {
                    platformName = String.valueOf(platform.getId());
                }

                // Hover text = station name + route names with destinations
                StringBuilder title = new StringBuilder(stationName);
                List<String> routeInfos = buildRouteInfos(platform, platformRouteMap.get(platform.getId()));
                if (!routeInfos.isEmpty()) {
                    title.append("\n").append(String.join("\n", routeInfos));
                }

                out.put(markerId, createMarker(markerId, pos, platformName, title.toString(),
                        station.getTransportMode(), false, station.getColor(), world));
            }
        }
    }

    private static void collectDepotMarkers(Map<String, MarkerOverlay> out, Level world) {
        for (Depot depot : collectDepots()) {
            String depotName = IGui.formatStationName(depot.getName());
            String markerId = getMarkerId(depot);
            BlockPos pos = new BlockPos(
                    (int) depot.getCenter().getX(),
                    (int) depot.getMaxY(), // Use top of depot area
                    (int) depot.getCenter().getZ());
            out.put(markerId, createMarker(markerId, pos, depotName, depotName, depot.getTransportMode(), true,
                    depot.getColor(), world));
        }
    }

    /**
     * Build "RouteName → Destination" strings for a platform. Routes are
     * deduplicated by name; the destination is the last station on the route
     * (or its custom destination, if set).
     */
    private static List<String> buildRouteInfos(Platform platform, List<Route> routes) {
        List<String> routeInfos = new ArrayList<>();
        if (routes == null) {
            return routeInfos;
        }

        Set<String> seenRoutes = new HashSet<>();
        for (Route route : routes) {
            String routeName = route.getName();
            if (routeName == null || routeName.isEmpty())
                continue;
            if (!seenRoutes.add(routeName))
                continue;

            StringBuilder routeInfo = new StringBuilder(IGui.formatStationName(routeName.split("\\|\\|")[0]));
            String destination = findDestinationForPlatform(route, platform);
            if (destination != null && !destination.isEmpty()) {
                routeInfo.append(" → ").append(destination);
            }
            routeInfos.add(routeInfo.toString());
        }
        return routeInfos;
    }

    /**
     * Find the destination station name for a route from a given platform.
     * Returns the last station name in the route (terminal).
     */
    private static String findDestinationForPlatform(Route route, Platform currentPlatform) {
        try {
            List<RoutePlatformData> routePlatforms = route.getRoutePlatforms();
            if (routePlatforms == null || routePlatforms.isEmpty())
                return null;

            // The last platform's station name is the terminal/destination
            RoutePlatformData lastPlatformData = routePlatforms.get(routePlatforms.size() - 1);

            // Check if there's a custom destination set
            String customDest = lastPlatformData.getCustomDestination();
            if (customDest != null && !customDest.isEmpty() && !Route.destinationIsReset(customDest)) {
                return customDest;
            }

            Platform lastPlatform = lastPlatformData.getPlatform();
            if (lastPlatform != null) {
                return IGui.formatStationName(lastPlatform.getStationName());
            }        } catch (Exception e) {
            MTRSurveyor.LOGGER.debug("[{}] Error finding destination: {}", MTRSurveyor.MOD_NAME, e.getMessage());
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

        return createMarker(markerId, pos, stationName, desc.toString(), station.getTransportMode(), false,
                station.getColor(), world);
    }

    private static MarkerOverlay createMarker(String markerId, BlockPos pos, String label, String title,
            TransportMode transportMode, boolean isDepot, int color, Level world) {
        ResourceLocation iconRL = getMarkerIcon(transportMode, isDepot);
        MapImage icon = new MapImage(iconRL, 16, 16);
        icon.setAnchorX(8);
        icon.setAnchorY(8);
        icon.setColor(color | 0xFF000000); // ensure alpha

        MarkerOverlay marker = new MarkerOverlay(MTRSurveyor.MOD_ID, markerId, pos, icon);
        marker.setDimension(world.dimension());
        marker.setLabel(label);
        marker.setTitle(title);
        return marker;
    }

    private static ResourceLocation getMarkerIcon(TransportMode transportMode, boolean isDepot) {
        String modeName = MTRUtil.getTransportModeName(transportMode);
        String type = isDepot ? "depot" : "station";
        return MTRSurveyor.id("textures/atlas/marker/" + modeName + "_" + type + ".png");
    }

    private static String getMarkerId(NameColorDataBase data) {
        String type = (data instanceof Station) ? "station"
                : (data instanceof Depot) ? "depot" : "platform";
        return MTRUtil.getTransportModeName(data.getTransportMode()) + "_" + type + "_"
                + data.getHexId().toLowerCase();
    }

    private static boolean shouldBeFilteredOut(Station station, MTRDataSummary dataSummary,
            boolean showEmptyStation) {
        List<MTRDataSummary.BasicRouteInfo> routes = dataSummary.getRoutesInStation(station);
        return !showEmptyStation && (routes == null || routes.isEmpty());
    }

    /**
     * Aggregate stations from BOTH the local streaming instance AND the
     * dashboard instance (if the user opened it).
     */
    private static Set<Station> collectStations() {
        Set<Station> allStations = new HashSet<>();
        try {
            MinecraftClientData instance = MinecraftClientData.getInstance();
            if (instance != null) {
                allStations.addAll(instance.stations);
            }
            MinecraftClientData dashboard = MinecraftClientData.getDashboardInstance();
            if (dashboard != null) {
                allStations.addAll(dashboard.stations);
            }
        } catch (Exception e) {
            MTRSurveyor.LOGGER.error("[{}] Error accessing MTR station datasets: ", MTRSurveyor.MOD_NAME, e);
        }
        return allStations;
    }

    private static Set<Depot> collectDepots() {
        Set<Depot> allDepots = new HashSet<>();
        try {
            MinecraftClientData instance = MinecraftClientData.getInstance();
            if (instance != null) {
                allDepots.addAll(instance.depots);
            }
            MinecraftClientData dashboard = MinecraftClientData.getDashboardInstance();
            if (dashboard != null) {
                allDepots.addAll(dashboard.depots);
            }
        } catch (Exception e) {
            MTRSurveyor.LOGGER.error("[{}] Error accessing MTR depot datasets: ", MTRSurveyor.MOD_NAME, e);
        }
        return allDepots;
    }

    private static Set<Route> collectRoutes() {
        Set<Route> allRoutes = new HashSet<>();
        try {
            MinecraftClientData instance = MinecraftClientData.getInstance();
            if (instance != null) {
                allRoutes.addAll(instance.routes);
            }
            MinecraftClientData dashboard = MinecraftClientData.getDashboardInstance();
            if (dashboard != null) {
                allRoutes.addAll(dashboard.routes);
            }
        } catch (Exception e) {
            MTRSurveyor.LOGGER.error("[{}] Error accessing MTR route datasets: ", MTRSurveyor.MOD_NAME, e);
        }
        return allRoutes;
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
