package com.lx862.mtrjourneymap;

import com.lx862.mtrjourneymap.config.MTRSurveyorConfig;
import com.lx862.mtrjourneymap.landmark.MTRLandmarkManager;
import com.lx862.mtrjourneymap.mixin.MTRAccessorMixin;
import com.lx862.mtrjourneymap.mixin.MainAccessorMixin;
import com.lx862.mtrjourneymap.util.MTRUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.mtr.core.Main;
import org.mtr.core.simulation.Simulator;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Commands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> rootNode = net.minecraft.commands.Commands.literal("mtrjourneymap");
        rootNode.requires(ctx -> ctx.hasPermission(4));

        LiteralArgumentBuilder<CommandSourceStack> forceSyncNode = net.minecraft.commands.Commands
                .literal("syncLandmarks");
        forceSyncNode
                .executes(ctx -> {
                    MTRLandmarkManager.SyncOrigin syncOrigin = MTRLandmarkManager.SyncOrigin
                            .ofServer("Player initiated sync");
                    MinecraftServer minecraftServer = ctx.getSource().getServer();
                    Main main = MTRAccessorMixin.getMain();

                    for (Simulator simulator : ((MainAccessorMixin) main).getSimulators()) {
                        ResourceLocation dimensionId = MTRUtil.dimensionToId(simulator.dimension);
                        MTRDataSummary mtrDataSummary = MTRDataSummary.of(simulator);
                        MTRLandmarkManager.syncLandmarks(syncOrigin,
                                minecraftServer.getLevel(ResourceKey.create(Registries.DIMENSION, dimensionId)),
                                mtrDataSummary, MTRSurveyorConfig.INSTANCE);
                        ctx.getSource()
                                .sendSuccess(() -> Component
                                        .literal("Synced MTR landmarks for dimension " + dimensionId + "!")
                                        .withStyle(ChatFormatting.GREEN), true);
                    }
                    return 1;
                })
                .then(net.minecraft.commands.Commands.argument("dimension", DimensionArgument.dimension())
                        .executes(ctx -> {
                            MTRLandmarkManager.SyncOrigin syncOrigin = MTRLandmarkManager.SyncOrigin
                                    .ofServer("Player initiated sync");
                            Level targetWorld = DimensionArgument.getDimension(ctx, "dimension");
                            ResourceLocation targetWorldId = targetWorld.dimension().location();
                            Main main = MTRAccessorMixin.getMain();

                            for (Simulator simulator : ((MainAccessorMixin) main).getSimulators()) {
                                ResourceLocation dimensionId = MTRUtil.dimensionToId(simulator.dimension);
                                if (targetWorldId.equals(dimensionId)) {
                                    MTRDataSummary mtrDataSummary = MTRDataSummary.of(simulator);
                                    MTRLandmarkManager.syncLandmarks(syncOrigin, targetWorld, mtrDataSummary,
                                            MTRSurveyorConfig.INSTANCE);
                                    ctx.getSource()
                                            .sendSuccess(() -> Component
                                                    .literal(
                                                            "Synced MTR landmarks for dimension " + targetWorldId + "!")
                                                    .withStyle(ChatFormatting.GREEN), true);
                                }
                            }
                            return 1;
                        }));

        rootNode.then(forceSyncNode);
        dispatcher.register(rootNode);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createBoolConfigNode(String configName,
            String friendlyName, Supplier<Boolean> getValue, Consumer<Boolean> setValue) {
        LiteralArgumentBuilder<CommandSourceStack> cfgNode = net.minecraft.commands.Commands.literal(configName);
        cfgNode.then(net.minecraft.commands.Commands.argument("enabled", BoolArgumentType.bool())
                .executes(ctx -> {
                    boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                    setValue.accept(enabled);
                    saveConfig(MTRSurveyorConfig.INSTANCE, ctx,
                            Component.literal(friendlyName + " set to " + enabled).withStyle(ChatFormatting.GREEN));
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

    private static void saveConfig(MTRSurveyorConfig configInstance, CommandContext<CommandSourceStack> ctx,
            Component successMessage) {
        ctx.getSource().sendSuccess(() -> successMessage, true);
        configInstance.save();
    }
}
