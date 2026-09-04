package com.lx862.mtrsurveyor.mapdata;

import java.util.List;

/**
 * Render-ready representation of one stretch of physical rail for the map
 * track layer. Points are sampled along the rail's actual curve (including
 * arcs and slopes projected to X/Z).
 */
public class MapTrack {

    public final List<double[]> points;

    public MapTrack(List<double[]> points) {
        this.points = points;
    }
}
