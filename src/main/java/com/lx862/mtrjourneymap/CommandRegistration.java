package com.lx862.mtrjourneymap;

import com.lx862.mtrjourneymap.config.MTRSurveyorConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Client-side commands, mirroring the Xaero edition's command surface so both
 * editions behave identically. Registered via RegisterClientCommandsEvent, so
 * they work on remote servers that do not have this mod installed.
 */
public class CommandRegistration {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> rootNode = Commands.literal("mtrjourneymap");

        // /mtrjourneymap syncLandmarks
        LiteralArgumentBuilder<CommandSourceStack> forceSyncNode = Commands.literal("syncLandmarks");
        forceSyncNode.executes(ctx -> {
            ClientSyncHandler.requestSync();
            ctx.getSource().sendSuccess(
                    () -> Component.literal("Landmark sync requested! Markers will update shortly.")
                            .withStyle(ChatFormatting.GREEN),
                    true);
            return 1;
        });

        // /mtrjourneymap mode station|platform
        LiteralArgumentBuilder<CommandSourceStack> modeNode = Commands.literal("mode");

        modeNode.then(Commands.literal("station").executes(ctx -> {
            MTRSurveyorConfig.INSTANCE.waypointMode = MTRSurveyorConfig.MODE_STATION;
            onConfigChanged(ctx, "Marker mode set to: station (one marker per station)");
            return 1;
        }));

        modeNode.then(Commands.literal("platform").executes(ctx -> {
            MTRSurveyorConfig.INSTANCE.waypointMode = MTRSurveyorConfig.MODE_PLATFORM;
            onConfigChanged(ctx, "Marker mode set to: platform (one marker per platform with route info)");
            return 1;
        }));

        // /mtrjourneymap mode (query current mode)
        modeNode.executes(ctx -> {
            ctx.getSource().sendSuccess(
                    () -> Component.literal("Current marker mode: " + MTRSurveyorConfig.INSTANCE.waypointMode)
                            .withStyle(ChatFormatting.AQUA),
                    false);
            return 1;
        });

        // Config sub-commands
        LiteralArgumentBuilder<CommandSourceStack> configNode = Commands.literal("config");
        configNode.then(createBoolConfigNode("enabled", "Landmark sync",
                () -> MTRSurveyorConfig.INSTANCE.enabled,
                v -> MTRSurveyorConfig.INSTANCE.enabled = v));
        configNode.then(createBoolConfigNode("showStations", "Station markers",
                () -> MTRSurveyorConfig.INSTANCE.visibility.showStationLandmarks,
                v -> MTRSurveyorConfig.INSTANCE.visibility.showStationLandmarks = v));
        configNode.then(createBoolConfigNode("showDepots", "Depot markers",
                () -> MTRSurveyorConfig.INSTANCE.visibility.showDepotLandmarks,
                v -> MTRSurveyorConfig.INSTANCE.visibility.showDepotLandmarks = v));
        configNode.then(createBoolConfigNode("showEmptyStation", "Empty stations",
                () -> MTRSurveyorConfig.INSTANCE.visibility.showEmptyStation,
                v -> MTRSurveyorConfig.INSTANCE.visibility.showEmptyStation = v));

        rootNode.then(forceSyncNode);
        rootNode.then(modeNode);
        rootNode.then(configNode);
        dispatcher.register(rootNode);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createBoolConfigNode(String configName,
            String friendlyName, Supplier<Boolean> getValue, Consumer<Boolean> setValue) {
        LiteralArgumentBuilder<CommandSourceStack> cfgNode = Commands.literal(configName);
        cfgNode.then(Commands.argument("enabled", BoolArgumentType.bool())
                .executes(ctx -> {
                    boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                    setValue.accept(enabled);
                    onConfigChanged(ctx, friendlyName + " set to " + enabled);
                    return 1;
                }))
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(
                            () -> Component.literal(friendlyName + " is currently set to " + getValue.get())
                                    .withStyle(ChatFormatting.AQUA),
                            false);
                    return 1;
                });
        return cfgNode;
    }

    /**
     * Persist the config and queue a landmark sync so the change takes
     * effect immediately.
     */
    private static void onConfigChanged(CommandContext<CommandSourceStack> ctx, String successMessage) {
        MTRSurveyorConfig.INSTANCE.save();
        ClientSyncHandler.requestSync();
        ctx.getSource().sendSuccess(
                () -> Component.literal(successMessage).withStyle(ChatFormatting.GREEN),
                true);
    }
}
