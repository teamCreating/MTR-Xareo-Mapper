package com.lx862.mtrsurveyor.integration;

import com.lx862.mtrsurveyor.MTRDataSummary;
import com.lx862.mtrsurveyor.MTRSurveyor;
import com.lx862.mtrsurveyor.config.MTRSurveyorConfig;
import net.minecraftforge.fml.ModList;
import org.mtr.core.data.AreaBase;
import org.mtr.core.data.Depot;
import org.mtr.core.data.Platform;
import org.mtr.core.data.Position;
import org.mtr.core.data.Route;
import org.mtr.core.data.RoutePlatformData;
import org.mtr.core.data.Station;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XaeroIntegration {

    private static final String WAYPOINT_PREFIX = "[MTR] ";
    private static final int STATION_COLOR = 9; // Blue
    private static final int DEPOT_COLOR = 6; // Gold
    private static final int PLATFORM_COLOR = 3; // Light blue

    // Sync state
    public static volatile boolean needsSync = false;
    private static int tickCounter = 0;
    private static final int SYNC_INTERVAL_TICKS = 100; // 5 seconds

    /**
     * Check if Xaero's Minimap mod is loaded (safe to call anywhere).
     */
    public static boolean isXaeroLoaded() {
        return ModList.get().isLoaded("xaerominimap");
    }

    /**
     * Called every client tick. Checks if sync is needed and Xaero is ready.
     */
    public static void onClientTick() {
        if (!needsSync)
            return;
        if (!MTRSurveyorConfig.INSTANCE.enabled.get())
            return;

        tickCounter++;
        if (tickCounter < SYNC_INTERVAL_TICKS)
            return;
        tickCounter = 0;

        try {
            MinecraftClientData clientData = MinecraftClientData.getInstance();
            if (clientData == null) {
                MTRSurveyor.LOGGER.debug("[MTRSurveyor] Client data not available yet, will retry...");
                return;
            }

            MTRDataSummary dataSummary = MTRDataSummary.of(clientData);
            boolean success = doSync(dataSummary);
            if (success) {
                needsSync = false;
                tickCounter = 0;
            }
        } catch (Throwable e) {
            MTRSurveyor.LOGGER.error("[MTRSurveyor] Error during waypoint sync tick", e);
        }
    }

    /**
     * Mark that waypoint sync is needed (called from mixins).
     */
    public static void requestSync() {
        needsSync = true;
        tickCounter = SYNC_INTERVAL_TICKS - 1; // Try on next tick cycle
    }

    /**
     * Perform the actual Xaero waypoint sync. Returns true on success.
     */
    private static boolean doSync(MTRDataSummary data) {
        try {
            return XaeroSyncHelper.performSync(data);
        } catch (NoClassDefFoundError e) {
            MTRSurveyor.LOGGER.warn("[MTRSurveyor] Xaero classes not available: {}", e.getMessage());
            needsSync = false;
            return false;
        }
    }

    /**
     * Inner helper class that contains all Xaero class references.
     * Separated so the outer class can be loaded without triggering Xaero class
     * loading.
     */
    static class XaeroSyncHelper {
        static boolean performSync(MTRDataSummary data) {
            xaero.common.XaeroMinimapSession session = xaero.common.XaeroMinimapSession.getCurrentSession();
            if (session == null) {
                MTRSurveyor.LOGGER.debug("[MTRSurveyor] Xaero session not available, will retry...");
                return false;
            }

            xaero.common.minimap.waypoints.WaypointsManager waypointsManager = session.getWaypointsManager();
            if (waypointsManager == null) {
                MTRSurveyor.LOGGER.debug("[MTRSurveyor] Waypoints manager not available, will retry...");
                return false;
            }

            xaero.common.minimap.waypoints.WaypointWorld waypointWorld = waypointsManager.getCurrentWorld();
            if (waypointWorld == null) {
                MTRSurveyor.LOGGER.debug("[MTRSurveyor] Waypoint world not available, will retry...");
                return false;
            }

            xaero.common.minimap.waypoints.WaypointSet currentSet = waypointWorld.getCurrentSet();
            if (currentSet == null) {
                MTRSurveyor.LOGGER.debug("[MTRSurveyor] No active waypoint set, will retry...");
                return false;
            }

            List<xaero.common.minimap.waypoints.Waypoint> existingWaypoints = currentSet.getList();

            // Remove old MTR waypoints
            Iterator<xaero.common.minimap.waypoints.Waypoint> iterator = existingWaypoints.iterator();
            while (iterator.hasNext()) {
                xaero.common.minimap.waypoints.Waypoint wp = iterator.next();
                if (wp.getName() != null && wp.getName().startsWith(WAYPOINT_PREFIX)) {
                    iterator.remove();
                }
            }

            String mode = MTRSurveyorConfig.INSTANCE.waypointMode.get();
            if ("platform".equalsIgnoreCase(mode)) {
                return syncPlatformMode(data, existingWaypoints);
            } else {
                return syncStationMode(data, existingWaypoints);
            }
        }

        /**
         * Station mode: One waypoint per station.
         * Name = station name, Symbol = first 2 chars of station name.
         * Y = average platform Y (or station maxY if no platforms).
         */
        private static boolean syncStationMode(MTRDataSummary data,
                List<xaero.common.minimap.waypoints.Waypoint> existingWaypoints) {
            Set<String> addedNames = new HashSet<>();
            int stationCount = 0;
            int depotCount = 0;
            int skippedCount = 0;

            // Add station waypoints
            if (MTRSurveyorConfig.INSTANCE.showStationLandmarks.get()) {
                for (AreaBase<?, ?> area : new ArrayList<>(data.getData().stations)) {
                    if (area instanceof Station station) {
                        String name = station.getName();
                        if (name == null || name.isEmpty())
                            continue;

                        // Skip empty stations unless configured to show them
                        List<MTRDataSummary.BasicRouteInfo> routes = data.getRoutesInStation(station);
                        if (!MTRSurveyorConfig.INSTANCE.showEmptyStation.get()
                                && (routes == null || routes.isEmpty())) {
                            skippedCount++;
                            continue;
                        }

                        String wpName = WAYPOINT_PREFIX + name;
                        if (addedNames.contains(wpName))
                            continue;
                        addedNames.add(wpName);

                        // Use station center X/Z, but fix Y using platform positions
                        Position center = station.getCenter();
                        int x = (int) center.getX();
                        int z = (int) center.getZ();
                        int y = calculateStationY(station);

                        // Symbol: full station name (Xaero renders it on the icon)
                        String symbol = name;

                        xaero.common.minimap.waypoints.Waypoint waypoint = new xaero.common.minimap.waypoints.Waypoint(
                                x, y, z, wpName, symbol, STATION_COLOR, 0, false);
                        waypoint.setDisabled(false);
                        existingWaypoints.add(waypoint);
                        stationCount++;

                        if (MTRSurveyorConfig.INSTANCE.debugLog.get()) {
                            MTRSurveyor.LOGGER.info("[MTRSurveyor] Station waypoint: {} at ({}, {}, {})", name, x, y,
                                    z);
                        }
                    }
                }
            }

            // Add depot waypoints
            if (MTRSurveyorConfig.INSTANCE.showDepotLandmarks.get()) {
                for (AreaBase<?, ?> area : new ArrayList<>(data.getData().depots)) {
                    if (area instanceof Depot depot) {
                        String name = depot.getName();
                        if (name == null || name.isEmpty())
                            continue;

                        String wpName = WAYPOINT_PREFIX + "Depot: " + name;
                        if (addedNames.contains(wpName))
                            continue;
                        addedNames.add(wpName);

                        Position center = depot.getCenter();
                        int x = (int) center.getX();
                        int y = (int) depot.getMaxY(); // Use top of depot area
                        int z = (int) center.getZ();

                        xaero.common.minimap.waypoints.Waypoint waypoint = new xaero.common.minimap.waypoints.Waypoint(
                                x, y, z, wpName, "D", DEPOT_COLOR, 0, false);
                        waypoint.setDisabled(false);
                        existingWaypoints.add(waypoint);
                        depotCount++;
                    }
                }
            }

            MTRSurveyor.LOGGER.info(
                    "[MTRSurveyor] Station mode sync: {} stations, {} depots ({} skipped)",
                    stationCount, depotCount, skippedCount);
            return true;
        }

        /**
         * Platform mode: One waypoint per platform.
         * Symbol = platform name/number, Name = [MTR] StationName | RouteName(s) |
         * Destination.
         * Y = platform mid position Y.
         */
        private static boolean syncPlatformMode(MTRDataSummary data,
                List<xaero.common.minimap.waypoints.Waypoint> existingWaypoints) {
            int platformCount = 0;

            if (!MTRSurveyorConfig.INSTANCE.showStationLandmarks.get()) {
                MTRSurveyor.LOGGER.info("[MTRSurveyor] Platform mode: station landmarks disabled, skipping");
                return true;
            }

            // Build a map of platformId -> list of routes, since platform.routes
            // is not populated on the client side
            Map<Long, List<Route>> platformRouteMap = new HashMap<>();
            try {
                for (Route route : new ArrayList<>(data.getData().routes)) {
                    List<RoutePlatformData> rpList = route.getRoutePlatforms();
                    if (rpList == null)
                        continue;
                    for (RoutePlatformData rpd : rpList) {
                        Platform rp = rpd.getPlatform();
                        if (rp != null) {
                            platformRouteMap.computeIfAbsent(rp.getId(), k -> new ArrayList<>()).add(route);
                        }
                    }
                }
            } catch (Exception e) {
                MTRSurveyor.LOGGER.debug("[MTRSurveyor] Error building platform route map: {}", e.getMessage());
            }

            for (AreaBase<?, ?> area : new ArrayList<>(data.getData().stations)) {
                if (!(area instanceof Station station))
                    continue;
                String stationName = station.getName();
                if (stationName == null || stationName.isEmpty())
                    continue;

                // Iterate over platforms (savedRails) in this station
                for (Object railObj : new ArrayList<>(station.savedRails)) {
                    if (!(railObj instanceof Platform platform))
                        continue;

                    Position midPos = platform.getMidPosition();
                    int x = (int) midPos.getX();
                    int y = (int) midPos.getY();
                    int z = (int) midPos.getZ();

                    // Platform name/number for the symbol
                    String platformName = platform.getName();
                    if (platformName == null || platformName.isEmpty()) {
                        platformName = String.valueOf(platform.getId());
                    }
                    String symbol = platformName;

                    // Build the full waypoint name: [MTR] StationName | Route1(→Dest),
                    // Route2(→Dest)
                    StringBuilder nameBuilder = new StringBuilder(WAYPOINT_PREFIX);
                    nameBuilder.append(stationName);

                    // Find routes using the pre-built map
                    List<String> routeInfos = new ArrayList<>();
                    List<Route> routesForPlatform = platformRouteMap.get(platform.getId());
                    if (routesForPlatform != null) {
                        // Deduplicate by route name
                        Set<String> seenRoutes = new HashSet<>();
                        for (Route route : routesForPlatform) {
                            String routeName = route.getName();
                            if (routeName == null || routeName.isEmpty())
                                continue;
                            if (!seenRoutes.add(routeName))
                                continue;

                            String destination = findDestinationForPlatform(route, platform);

                            StringBuilder routeInfo = new StringBuilder(routeName);
                            if (destination != null && !destination.isEmpty()) {
                                routeInfo.append("→").append(destination);
                            }
                            routeInfos.add(routeInfo.toString());
                        }
                    }

                    if (!routeInfos.isEmpty()) {
                        nameBuilder.append(" | ").append(String.join(", ", routeInfos));
                    }

                    String wpName = nameBuilder.toString();

                    xaero.common.minimap.waypoints.Waypoint waypoint = new xaero.common.minimap.waypoints.Waypoint(
                            x, y, z, wpName, symbol, PLATFORM_COLOR, 0, false);
                    waypoint.setDisabled(false);
                    existingWaypoints.add(waypoint);
                    platformCount++;

                    if (MTRSurveyorConfig.INSTANCE.debugLog.get()) {
                        MTRSurveyor.LOGGER.info("[MTRSurveyor] Platform waypoint: {} [{}] at ({}, {}, {})",
                                wpName, symbol, x, y, z);
                    }
                }
            }

            MTRSurveyor.LOGGER.info("[MTRSurveyor] Platform mode sync: {} platforms", platformCount);
            return true;
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

                // Find the index of current platform in the route
                int currentIndex = -1;
                for (int i = 0; i < routePlatforms.size(); i++) {
                    Platform rp = routePlatforms.get(i).getPlatform();
                    if (rp != null && rp.getId() == currentPlatform.getId()) {
                        currentIndex = i;
                        break;
                    }
                }

                if (currentIndex < 0)
                    return null;

                // The last platform's station name is the terminal/destination
                RoutePlatformData lastPlatformData = routePlatforms.get(routePlatforms.size() - 1);

                // Check if there's a custom destination set
                String customDest = lastPlatformData.getCustomDestination();
                if (customDest != null && !customDest.isEmpty()
                        && !Route.destinationIsReset(customDest)) {
                    return customDest;
                }

                // Use the last platform's station name
                Platform lastPlatform = lastPlatformData.getPlatform();
                if (lastPlatform != null) {
                    return lastPlatform.getStationName();
                }
            } catch (Exception e) {
                MTRSurveyor.LOGGER.debug("[MTRSurveyor] Error finding destination: {}", e.getMessage());
            }
            return null;
        }

        /**
         * Calculate a better Y coordinate for a station waypoint.
         * Uses the average Y of all platforms in the station.
         * Falls back to station maxY if no platforms exist.
         */
        private static int calculateStationY(Station station) {
            long totalY = 0;
            int count = 0;

            for (Object railObj : new ArrayList<>(station.savedRails)) {
                if (railObj instanceof Platform platform) {
                    Position midPos = platform.getMidPosition();
                    totalY += (long) midPos.getY();
                    count++;
                }
            }

            if (count > 0) {
                return (int) (totalY / count);
            }

            // Fallback: use the top of the station area
            return (int) station.getMaxY();
        }
    }
}
