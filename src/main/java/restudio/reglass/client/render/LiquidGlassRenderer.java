package restudio.reglass.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.WidgetStyle;

/** Hardware-compatible Liquid Glass renderer; no custom fragment/framebuffer shaders are used. */
public final class LiquidGlassRenderer {
    private static double timeSeconds;

    private LiquidGlassRenderer() {}

    public static void registerShaders(RegisterShadersEvent event) {
        // Compatibility hook. The mobile-safe renderer intentionally does not load core shaders.
    }

    public static void beginFrame() {
        updateTime();
    }

    public static void render(GuiGraphics graphics, int x, int y, int width, int height, float radius,
                              WidgetStyle style, float hover, float focus, net.minecraft.network.chat.Component text) {
        renderCapsule(graphics, x, y, width, height, 0.0f, style);
        if (text != null && !text.getString().isEmpty()) {
            graphics.drawCenteredString(Minecraft.getInstance().font, text,
                    x + width / 2, y + (height - 8) / 2, 0xFFFFFFFF);
        }
    }

    public static void renderCapsule(GuiGraphics graphics, int x, int y, int width, int height,
                                     float progress, WidgetStyle style) {
        if (width <= 0 || height <= 0) return;
        updateTime();
        ReGlassConfig config = ReGlassConfig.INSTANCE;

        int spread = Math.max(4, Math.round(config.defaultShadowExpand));
        int offsetX = Math.round(config.defaultShadowOffsetX);
        int offsetY = Math.round(config.defaultShadowOffsetY) + 2;

        // 1. Soft volumetric drop shadow.
        int sx = x + offsetX - spread / 2;
        int sy = y + offsetY;
        graphics.fillGradient(sx, sy, sx + width + spread, sy + height + spread,
                0x30000000, 0x00000000);
        graphics.fillGradient(sx + spread / 4, sy, sx + width + spread - spread / 4, sy + height + spread / 2,
                0x24000000, 0x00000000);

        float radius = Math.min(width, height) * 0.5f;

        // 2. Shader-free Frosted Glass approximation.
        int frostAlpha = clampByte(Math.round(config.defaultTintAlpha * 46.0f));
        int frost = argb(frostAlpha, 235, 240, 255);
        graphics.fillGradient(x - 2, y - 2, x + width + 2, y + height + 2,
                argb(frostAlpha, 235, 240, 255), argb(Math.max(1, frostAlpha / 4), 235, 240, 255));
        drawCapsule(graphics, x - 1, y - 1, width + 2, height + 2, radius + 1.0f, frost);

        // 3. Main tint layer.
        int tintAlpha = clampByte(Math.round(config.defaultTintAlpha * 255.0f));
        int body = (tintAlpha << 24) | (config.defaultTintColor & 0x00FFFFFF);
        drawCapsule(graphics, x, y, width, height, radius, body);

        int depthAlpha = Math.min(76, Math.max(18, tintAlpha / 4));
        drawBottomBand(graphics, x + 1, y + 1, width - 2, height - 2, radius - 1.0f,
                argb(depthAlpha, 0, 0, 0));

        if (progress >= 0.0f) {
            float p = Math.max(0.0f, Math.min(1.0f, progress));
            int fillWidth = Math.round(width * p);
            if (fillWidth > 0) {
                drawProgress(graphics, x, y, width, height, radius, fillWidth,
                        argb(Math.max(70, tintAlpha / 2), 255, 255, 255));
            }
        }

        // 4. Thin glossy upper rim with restrained shimmer.
        int highlightAlpha = clampByte((int) Math.round(150.0f * (0.92f + 0.08f * Math.sin(timeSeconds * 0.8))));
        int inset = Math.max(2, Math.round(radius * 0.72f));
        if (width > inset * 2) {
            graphics.fillGradient(x + inset, y + 1, x + width - inset, y + 3,
                    argb(highlightAlpha, 255, 255, 255), argb(highlightAlpha / 6, 255, 255, 255));
        }
        drawTopArcHighlight(graphics, x, y, width, height, radius, highlightAlpha);
    }

    private static void updateTime() {
        timeSeconds = (System.nanoTime() / 1_000_000_000.0) % 100000.0;
    }

    private static void drawCapsule(GuiGraphics graphics, int x, int y, int width, int height,
                                    float radius, int color) {
        if (width <= 0 || height <= 0) return;
        int r = Math.max(1, Math.round(Math.min(radius, Math.min(width, height) * 0.5f)));
        if (width >= height) {
            if (width > 2 * r) graphics.fill(x + r, y, x + width - r, y + height, color);
            drawCircle(graphics, x + r, y + r, r, color);
            drawCircle(graphics, x + width - r, y + r, r, color);
        } else {
            if (height > 2 * r) graphics.fill(x, y + r, x + width, y + height - r, color);
            drawCircle(graphics, x + r, y + r, r, color);
            drawCircle(graphics, x + r, y + height - r, r, color);
        }
    }

    private static void drawBottomBand(GuiGraphics graphics, int x, int y, int width, int height,
                                       float radius, int color) {
        if (width <= 0 || height <= 0) return;
        int r = Math.max(1, Math.round(Math.min(radius, Math.min(width, height) * 0.5f)));
        int band = Math.max(1, Math.min(2, height / 6));
        if (width > 2 * r) graphics.fill(x + r, y + height - band, x + width - r, y + height, color);
    }

    private static void drawProgress(GuiGraphics graphics, int x, int y, int width, int height,
                                     float radius, int fillWidth, int color) {
        int r = Math.max(1, Math.round(Math.min(radius, height * 0.5f)));
        int w = Math.min(width, fillWidth);
        if (w <= 0) return;
        graphics.fill(x, y + r, x + w, y + height - r, color);
        if (w > r) graphics.fill(x + r, y, x + w, y + height, color);
        drawCircle(graphics, x + r, y + r, Math.min(r, w), color);
        if (w > r) drawCircle(graphics, x + w - r, y + r, r, color);
    }

    private static void drawTopArcHighlight(GuiGraphics graphics, int x, int y, int width, int height,
                                            float radius, int alpha) {
        int r = Math.max(1, Math.round(Math.min(radius, height * 0.5f)));
        int segments = Math.max(8, Math.min(24, r * 2));
        if (width >= height) {
            int[] centers = {x + r, x + width - r};
            for (int cx : centers) {
                for (int i = 0; i <= segments / 2; i++) {
                    double angle = Math.PI + Math.PI * i / segments;
                    int px = Math.round(cx + (float) Math.cos(angle) * r);
                    int py = Math.round(y + r + (float) Math.sin(angle) * r);
                    if (py <= y + 2) graphics.fill(px, py, px + 2, py + 2, argb(alpha, 255, 255, 255));
                }
            }
        }
    }

    private static void drawCircle(GuiGraphics graphics, float cx, float cy, float radius, int color) {
        if (radius <= 0.5f) return;
        graphics.flush();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN,
                DefaultVertexFormat.POSITION_COLOR);
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;
        builder.addVertex(cx, cy, 0.0f).setColor(r, g, b, a);
        int segments = Math.max(12, Math.min(32, Math.round(radius * 2.0f)));
        for (int i = 0; i <= segments; i++) {
            double angle = Math.PI * 2.0 * i / segments;
            builder.addVertex(cx + (float) Math.cos(angle) * radius,
                    cy + (float) Math.sin(angle) * radius, 0.0f).setColor(r, g, b, a);
        }
        BufferUploader.drawWithShader(builder.buildOrThrow());
        graphics.flush();
    }

    private static int argb(int alpha, int r, int g, int b) {
        return (clampByte(alpha) << 24) | (clampByte(r) << 16) | (clampByte(g) << 8) | clampByte(b);
    }

    private static int clampByte(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
