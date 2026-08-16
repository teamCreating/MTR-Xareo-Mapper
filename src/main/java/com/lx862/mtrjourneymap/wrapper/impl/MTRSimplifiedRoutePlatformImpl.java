package com.lx862.mtrjourneymap.wrapper.impl;

import com.lx862.mtrjourneymap.wrapper.MTRRoutePlatform;
import org.mtr.core.data.SimplifiedRoutePlatform;

public class MTRSimplifiedRoutePlatformImpl implements MTRRoutePlatform {
    private final SimplifiedRoutePlatform instance;

    public MTRSimplifiedRoutePlatformImpl(SimplifiedRoutePlatform simplifiedRoutePlatform) {
        this.instance = simplifiedRoutePlatform;
    }

    @Override
    public long getPlatformId() {
        return this.instance.getPlatformId();
    }
}
