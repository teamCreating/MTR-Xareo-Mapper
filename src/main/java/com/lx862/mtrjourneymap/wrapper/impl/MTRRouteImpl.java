package com.lx862.mtrjourneymap.wrapper.impl;

import com.lx862.mtrjourneymap.wrapper.MTRRoute;
import com.lx862.mtrjourneymap.wrapper.MTRRoutePlatform;
import org.mtr.core.data.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MTRRouteImpl implements MTRRoute {
    private final Route instance;

    public MTRRouteImpl(Route route) {
        this.instance = route;
    }

    @Override
    public String getName() {
        return this.instance.getName();
    }

    @Override
    public int getColor() {
        return this.instance.getColor();
    }

    @Override
    public boolean isHidden() {
        return this.instance.getHidden();
    }

    @Override
    public List<MTRRoutePlatform> getRoutePlatforms() {
        return new ArrayList<>(this.instance.getRoutePlatforms()).stream().map(MTRRoutePlatformDataImpl::new)
                .collect(Collectors.toList());
    }
}
