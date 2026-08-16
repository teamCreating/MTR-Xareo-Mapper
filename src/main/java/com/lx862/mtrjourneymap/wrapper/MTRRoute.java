package com.lx862.mtrjourneymap.wrapper;

import java.util.List;

public interface MTRRoute {
    String getName();

    int getColor();

    boolean isHidden();

    List<MTRRoutePlatform> getRoutePlatforms();
}
