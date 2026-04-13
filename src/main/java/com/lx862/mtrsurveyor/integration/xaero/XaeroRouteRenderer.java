package com.lx862.mtrsurveyor.integration.xaero;

import com.lx862.mtrsurveyor.MTRSurveyor;
import com.lx862.mtrsurveyor.config.MTRSurveyorConfig;
import com.lx862.mtrsurveyor.mixin.client.xaero.XaeroWorldMapAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.mtr.core.data.*;
import org.mtr.mod.client.MinecraftClientData;
import xaero.map.gui.GuiMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Renders MTR route lines on the Xaero World Map.
 * Called from XaeroWorldMapMixin during GuiMap rendering.
 */
public class XaeroRouteRenderer {

    private static final float LINE_HALF_WIDTH = 3.0f;

    public static boolean showMTRRoutes = true;

    private static final int WIDGET_X = 5;
    private static final int WIDGET_Y = 60;
    private static final int WIDGET_WIDTH = 40;
    private static final int WIDGET_HEIGHT = 16;

    public static boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            if (mouseX >= WIDGET_X && mouseX <= WIDGET_X + WIDGET_WIDTH &&
                    mouseY >= WIDGET_Y && mouseY <= WIDGET_Y + WIDGET_HEIGHT) {
                showMTRRoutes = !showMTRRoutes;
                return true;
            }
        }
        return false;
    }

    /**
     * Main render entry point, called from the mixin.
     */
    public static void onRender(GuiGraphics graphics, GuiMap screen, int mouseX, int mouseY, float partialTick) {
        if (!MTRSurveyorConfig.INSTANCE.enabled.get()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        // Render Toggle Widget
        int color = showMTRRoutes ? 0xAA00AA00 : 0xAAAA0000;
        int hoverColor = showMTRRoutes ? 0xDD00FF00 : 0xDDFF5555;
        boolean hovering = mouseX >= WIDGET_X && mouseX <= WIDGET_X + WIDGET_WIDTH &&
                mouseY >= WIDGET_Y && mouseY <= WIDGET_Y + WIDGET_HEIGHT;

        graphics.fill(WIDGET_X, WIDGET_Y, WIDGET_X + WIDGET_WIDTH, WIDGET_Y + WIDGET_HEIGHT,
                hovering ? hoverColor : color);
        graphics.drawCenteredString(mc.font, "MTR", WIDGET_X + WIDGET_WIDTH / 2,
                WIDGET_Y + (WIDGET_HEIGHT - mc.font.lineHeight) / 2, 0xFFFFFF);

        if (!showMTRRoutes) {
            return;
        }

        MinecraftClientData clientData = MinecraftClientData.getInstance();
        if (clientData == null) {
            return;
        }

        XaeroWorldMapAccessor accessor = (XaeroWorldMapAccessor) (Object) screen;
        double cameraX = accessor.getCameraX();
        double cameraZ = accessor.getCameraZ();
        double mapScale = accessor.getScale();
        double guiScale = (double) mc.getWindow().getWidth() / (double) mc.getWindow().getGuiScaledWidth();
        double scale = mapScale / guiScale;

        PoseStack pose = graphics.pose();
        pose.pushPose();

        net.minecraft.client.gui.screens.Screen mcScreen = (net.minecraft.client.gui.screens.Screen) (Object) screen;
        // Transform: center of screen -> apply scale -> offset by camera
        pose.translate((float) mcScreen.width / 2.0f, (float) mcScreen.height / 2.0f, 0.0f);
        pose.scale((float) scale, (float) scale, 1.0f);
        pose.translate(-cameraX, -cameraZ, 0.0);

        renderRouteLines(graphics, clientData, pose, scale);

        pose.popPose();
    }

    /**
     * Iterate over all routes and draw lines between consecutive platforms.
     */
    private static void renderRouteLines(GuiGraphics graphics, MinecraftClientData clientData, PoseStack pose,
            double scale) {
        if (!showMTRRoutes)
            return;

        java.util.Set<org.mtr.core.data.SimplifiedRoute> allRoutes = new java.util.HashSet<>();
        java.util.Map<Long, org.mtr.core.data.Platform> allPlatforms = new java.util.HashMap<>();

        try {
            org.mtr.mod.client.MinecraftClientData instance = org.mtr.mod.client.MinecraftClientData.getInstance();
            if (instance != null) {
                allRoutes.addAll(instance.simplifiedRouteIdMap.values());
                allPlatforms.putAll(instance.platformIdMap);
            }
            org.mtr.mod.client.MinecraftClientData dashboard = org.mtr.mod.client.MinecraftClientData
                    .getDashboardInstance();
            if (dashboard != null) {
                allRoutes.addAll(dashboard.simplifiedRouteIdMap.values());
                allPlatforms.putAll(dashboard.platformIdMap);
            }
        } catch (Exception e) {
            MTRSurveyor.LOGGER.error("[MTRSurveyor] Error accessing MTR route datasets: ", e);
        }

        if (System.currentTimeMillis() % 2000 < 50) {
            MTRSurveyor.LOGGER.info("[MTRSurveyor] renderRouteLines called, rendering {} routes", allRoutes.size());
        }

        for (org.mtr.core.data.SimplifiedRoute route : allRoutes) {
            try {
                int routeColor = route.getColor();
                org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList<org.mtr.core.data.SimplifiedRoutePlatform> platforms = route
                        .getPlatforms();

                if (platforms == null || platforms.size() < 2) {
                    continue;
                }

                for (int i = 0; i < platforms.size() - 1; i++) {
                    org.mtr.core.data.SimplifiedRoutePlatform p1 = platforms.get(i);
                    org.mtr.core.data.SimplifiedRoutePlatform p2 = platforms.get(i + 1);

                    org.mtr.core.data.Platform platform1 = allPlatforms.get(p1.getPlatformId());
                    org.mtr.core.data.Platform platform2 = allPlatforms.get(p2.getPlatformId());

                    if (platform1 == null || platform2 == null) {
                        continue;
                    }

                    org.mtr.core.data.Position pos1 = platform1.getMidPosition();
                    org.mtr.core.data.Position pos2 = platform2.getMidPosition();

                    float x1 = (float) pos1.getX();
                    float z1 = (float) pos1.getZ();
                    float x2 = (float) pos2.getX();
                    float z2 = (float) pos2.getZ();

                    drawLine(graphics, pose, x1, z1, x2, z2, routeColor, 200, scale);
                }
            } catch (Exception e) {
                MTRSurveyor.LOGGER.info("[MTRSurveyor] Error rendering route line: {}", e.getMessage());
            }
        }

        // Force flush the buffer so the lines actually draw right now, without culling
        com.mojang.blaze3d.systems.RenderSystem.disableCull();
        graphics.bufferSource().endBatch(net.minecraft.client.renderer.RenderType.gui());
        com.mojang.blaze3d.systems.RenderSystem.enableCull();
    }

    /**
     * Draw a thick line (as a quad) between two world coordinates on the map.
     */
    private static void drawLine(GuiGraphics graphics, PoseStack pose, float x1, float z1, float x2, float z2,
            int color, int alpha, double scale) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        // Calculate perpendicular direction for line width
        float dx = x2 - x1;
        float dz = z2 - z1;
        float length = (float) Math.sqrt(dx * dx + dz * dz);
        if (length < 0.01f)
            return;

        float lineWidthInWorld = (float) (LINE_HALF_WIDTH / scale);

        // Perpendicular unit vector
        float px = -dz / length * lineWidthInWorld;
        float pz = dx / length * lineWidthInWorld;

        Matrix4f matrix = pose.last().pose();
        MultiBufferSource.BufferSource bufferSource = graphics.bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(net.minecraft.client.renderer.RenderType.gui());

        // Build quad: four corners of the thick line
        consumer.vertex(matrix, x1 + px, z1 + pz, 0).color(r, g, b, alpha).endVertex();
        consumer.vertex(matrix, x1 - px, z1 - pz, 0).color(r, g, b, alpha).endVertex();
        consumer.vertex(matrix, x2 - px, z2 - pz, 0).color(r, g, b, alpha).endVertex();
        consumer.vertex(matrix, x2 + px, z2 + pz, 0).color(r, g, b, alpha).endVertex();
    }
}
