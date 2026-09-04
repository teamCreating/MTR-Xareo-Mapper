package com.lx862.mtrsurveyor.mapdata;

import java.util.List;

/**
 * Render-ready representation of one MTR route for the map path layer.
 * Points are world X/Z coordinates; each point corresponds to one stop
 * (platform) on the route and carries its display info for hover tooltips.
 */
public class MapRoute {

    public final String name;
    public final int color;
    /** True when the route is circular, in which case the polyline closes back to the first stop. */
    public final boolean circular;
    public final List<Stop> stops;

    public MapRoute(String name, int color, boolean circular, List<Stop> stops) {
        this.name = name;
        this.color = color;
        this.circular = circular;
        this.stops = stops;
    }

    /**
     * One stop on a route: its world position plus display strings for tooltips.
     */
    public static class Stop {

        public final double x;
        public final double z;
        public final String stationName;
        public final String destination;

        public Stop(double x, double z, String stationName, String destination) {
            this.x = x;
            this.z = z;
            this.stationName = stationName;
            this.destination = destination;
        }
    }
}
