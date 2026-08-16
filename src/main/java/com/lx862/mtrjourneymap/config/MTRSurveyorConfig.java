package com.lx862.mtrjourneymap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lx862.mtrjourneymap.MTRSurveyor;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MTRSurveyorConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("mtrjourneymap.json");
    public static MTRSurveyorConfig INSTANCE = new MTRSurveyorConfig();

    public boolean formalInitLog = false;
    public boolean debugLog = false;
    public boolean enabled = true;
    public Visibility visibility = new Visibility();

    public static class Visibility {
        public boolean showStationLandmarks = true;
        public boolean showDepotLandmarks = false;
        public boolean showEmptyStation = false;
        public boolean showHiddenRoute = false;
    }

    public static void init() {
        load();
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                INSTANCE = GSON.fromJson(json, MTRSurveyorConfig.class);
                if (INSTANCE == null)
                    INSTANCE = new MTRSurveyorConfig();
            } catch (IOException e) {
                MTRSurveyor.LOGGER.error("[{}] Failed to load config", MTRSurveyor.MOD_NAME, e);
                INSTANCE = new MTRSurveyorConfig();
            }
        }
        INSTANCE.save();
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            MTRSurveyor.LOGGER.error("[{}] Failed to save config", MTRSurveyor.MOD_NAME, e);
        }
    }
}
