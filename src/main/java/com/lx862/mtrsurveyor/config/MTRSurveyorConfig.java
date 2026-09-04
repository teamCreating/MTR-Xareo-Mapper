package com.lx862.mtrsurveyor.config;

import com.lx862.mtrsurveyor.MTRSurveyor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class MTRSurveyorConfig {

        public static final ForgeConfigSpec SPEC;
        public static final MTRSurveyorConfig INSTANCE;

        // General
        public final ForgeConfigSpec.BooleanValue formalInitLog;
        public final ForgeConfigSpec.BooleanValue debugLog;
        public final ForgeConfigSpec.BooleanValue enabled;

        // Waypoint mode: "station" or "platform"
        public final ForgeConfigSpec.ConfigValue<String> waypointMode;

        // World map path layers
        public final ForgeConfigSpec.BooleanValue routeLinesEnabled;
        public final ForgeConfigSpec.BooleanValue trackLinesEnabled;

        // Visibility
        public final ForgeConfigSpec.BooleanValue showStationLandmarks;
        public final ForgeConfigSpec.BooleanValue showDepotLandmarks;
        public final ForgeConfigSpec.BooleanValue showEmptyStation;
        public final ForgeConfigSpec.BooleanValue showHiddenRoute;

        static {
                ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
                INSTANCE = new MTRSurveyorConfig(builder);
                SPEC = builder.build();
        }

        private MTRSurveyorConfig(ForgeConfigSpec.Builder builder) {
                builder.comment("MTR Surveyor Configuration");

                formalInitLog = builder
                                .comment("Change the mod initialization log message to be something more formal")
                                .define("formalInitLog", false);

                debugLog = builder
                                .comment("Log all landmark sync events to the console")
                                .define("debugLog", false);

                enabled = builder
                                .comment("Whether waypoints should be automatically created & synced when an MTR-related change occurs")
                                .define("enabled", true);

                waypointMode = builder
                                .comment("Waypoint display mode: 'station' shows one waypoint per station, 'platform' shows one waypoint per platform with route info")
                                .define("waypointMode", "station");

                routeLinesEnabled = builder
                                .comment("Whether MTR route lines should be drawn on the Xaero's World Map")
                                .define("routeLinesEnabled", true);

                trackLinesEnabled = builder
                                .comment("Whether the MTR track layer (actual rail geometry) should be drawn on the Xaero's World Map")
                                .define("trackLinesEnabled", true);

                builder.push("visibility");

                showStationLandmarks = builder
                                .comment("Whether station waypoints should be added to the map")
                                .define("showStationLandmarks", true);

                showDepotLandmarks = builder
                                .comment("Whether depot waypoints should be added to the map")
                                .define("showDepotLandmarks", false);

                showEmptyStation = builder
                                .comment("Whether empty stations (with no routes) should be added to the map")
                                .define("showEmptyStation", false);

                showHiddenRoute = builder
                                .comment("Whether MTR routes marked as hidden should be appended to the station description")
                                .define("showHiddenRoute", false);

                builder.pop();
        }

        public static void init() {
                ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "mtrsurveyor.toml");
        }
}
