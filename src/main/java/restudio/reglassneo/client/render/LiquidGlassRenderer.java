package restudio.reglassneo.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import restudio.reglassneo.client.LiquidGlassPipelines;
import restudio.reglassneo.client.LiquidGlassUniforms;
import restudio.reglassneo.client.api.WidgetStyle;

public final class LiquidGlassRenderer {
    private LiquidGlassRenderer() {}

    private static float cachedTime;

    public static void registerShaders(RegisterShadersEvent event) {
        LiquidGlassPipelines.registerShaders(event);
    }

    public static void beginFrame() {
        updateTimeCache();
    }

    private static void updateTimeCache() {
        cachedTime = (float) ((System.nanoTime() / 1_000_000_000.0) % 100000.0);
    }

    public static void render(GuiGraphics graphics, int x, int y, int width, int height,
                              float radius, WidgetStyle style, float hover, float focus,
                              net.minecraft.network.chat.Component text) {
        if (width <= 0 || height <= 0 || !LiquidGlassPipelines.ready()) return;

        renderInternal(graphics, x, y, width, height, radius, -1.0f, style, hover, focus);
        if (text != null && !text.getString().isEmpty()) {
            graphics.drawCenteredString(Minecraft.getInstance().font, text,
                    x + width / 2, y + (height - 8) / 2, 0xFFFFFFFF);
        }
    }

    public static void renderCapsule(GuiGraphics graphics, int x, int y, int width, int height,
                                     float progress, WidgetStyle style) {
        if (width <= 0 || height <= 0 || !LiquidGlassPipelines.ready()) return;
        renderInternal(graphics, x, y, width, height,
                Math.min(width, height) * 0.5f, progress, style, 0.0f, 0.0f);
    }

    private static void renderInternal(GuiGraphics graphics, int x, int y, int width, int height,
                                       float radius, float progress, WidgetStyle style,
                                       float hover, float focus) {
        Minecraft mc = Minecraft.getInstance();
        ShaderInstance shader = LiquidGlassPipelines.glassShader();
        if (shader == null) return;

        float scale = (float) mc.getWindow().getGuiScale();
        int px = Math.round(x * scale);
        int py = Math.round(y * scale);
        int pw = Math.max(1, Math.round(width * scale));
        int ph = Math.max(1, Math.round(height * scale));
        int shadow = Math.max(2, Math.round(style.getShadowExpand() * scale));
        int sw = mc.getMainRenderTarget().width;
        int sh = mc.getMainRenderTarget().height;

        // Finish all pending GuiGraphics batches before changing the active shader/state.
        graphics.flush();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        // Bind the registered mobile SDF shader explicitly before building the widget quad.
        RenderSystem.setShader(() -> LiquidGlassPipelines.glassShader());

        // Upload every widget uniform immediately before the draw. Time is refreshed every frame
        // so the inner highlight cannot become a static optimized-away value.
        updateTimeCache();
        LiquidGlassUniforms.get().applyWidget(
                shader, sw, sh, px, py, pw, ph, radius * scale,
                style, hover, focus, progress
        );

        drawQuad(px - shadow, sh - py - ph - shadow,
                pw + shadow * 2, ph + shadow * 2);

        // Restore a clean GuiGraphics boundary after the custom draw.
        graphics.flush();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        graphics.flush();
    }

    private static void drawQuad(int x, int y, int width, int height) {
        // Do not select another shader here: renderInternal has already explicitly bound
        // the registered ReGlassNeo SDF shader and uploaded its uniforms.
        BufferBuilder builder = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.addVertex(x, y, 0.0f).setUv(0.0f, 0.0f);
        builder.addVertex(x + width, y, 0.0f).setUv(1.0f, 0.0f);
        builder.addVertex(x + width, y + height, 0.0f).setUv(1.0f, 1.0f);
        builder.addVertex(x, y + height, 0.0f).setUv(0.0f, 1.0f);
        BufferUploader.drawWithShader(builder.buildOrThrow());
    }
}
