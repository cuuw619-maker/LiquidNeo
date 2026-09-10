package restudio.reglassneo.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import restudio.reglassneo.client.LiquidGlassPipelines;
import restudio.reglassneo.client.LiquidGlassUniforms;
import restudio.reglassneo.client.api.WidgetStyle;
import restudio.reglassneo.client.runtime.ReGlassAnim;

public final class LiquidGlassRenderer {
    private LiquidGlassRenderer() {}

    private static TextureTarget backgroundTarget;
    private static int backgroundWidth = -1;
    private static int backgroundHeight = -1;

    public static void registerShaders(RegisterShadersEvent event) {
        LiquidGlassPipelines.registerShaders(event);
    }

    public static void beginFrame() {
        // Animation state is advanced once per client tick by ReGlassClient.
        // Keep this hook cheap so every rendered widget does not touch the clock.
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
        renderCapsule(graphics, x, y, width, height, progress, style,
                Math.min(width, height) * 0.5f, 0.0f, 0.0f);
    }

    public static void renderCapsule(GuiGraphics graphics, int x, int y, int width, int height,
                                     float progress, WidgetStyle style, float radius,
                                     float hover, float focus) {
        if (width <= 0 || height <= 0 || !LiquidGlassPipelines.ready()) return;
        renderInternal(graphics, x, y, width, height,
                Math.min(radius, Math.min(width, height) * 0.5f), progress, style, hover, focus);
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

        graphics.flush();
        captureBackground(mc, sw, sh);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(() -> LiquidGlassPipelines.glassShader());
        // Sampler1 is the dedicated background input used by the frosted-glass pass.
        RenderSystem.setShaderTexture(1, backgroundTarget.getColorTextureId());

        ReGlassAnim anim = ReGlassAnim.INSTANCE;
        LiquidGlassUniforms.get().applyWidget(
                shader, sw, sh, px, py, pw, ph, radius * scale,
                style, hover, focus, progress
        );
        var time = shader.getUniform("Time");
        if (time != null) time.set(anim.timeSeconds());

        drawQuad(px - shadow, sh - py - ph - shadow,
                pw + shadow * 2, ph + shadow * 2);

        graphics.flush();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        mc.getMainRenderTarget().bindWrite(false);
        graphics.flush();
    }

    private static void captureBackground(Minecraft mc, int width, int height) {
        RenderTarget main = mc.getMainRenderTarget();
        if (backgroundTarget == null || backgroundWidth != width || backgroundHeight != height) {
            if (backgroundTarget != null) backgroundTarget.destroyBuffers();
            backgroundTarget = new TextureTarget(width, height, false, Minecraft.ON_OSX);
            backgroundWidth = width;
            backgroundHeight = height;
            backgroundTarget.setFilterMode(GL30.GL_LINEAR);
        }

        main.bindRead();
        RenderSystem.bindTexture(backgroundTarget.getColorTextureId());
        GL11.glCopyTexSubImage2D(
                GL11.GL_TEXTURE_2D, 0,
                0, 0,
                0, 0,
                width, height
        );
        main.bindWrite(false);
    }

    private static void drawQuad(int x, int y, int width, int height) {
        BufferBuilder builder = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.addVertex(x, y, 0.0f).setUv(0.0f, 0.0f);
        builder.addVertex(x + width, y, 0.0f).setUv(1.0f, 0.0f);
        builder.addVertex(x + width, y + height, 0.0f).setUv(1.0f, 1.0f);
        builder.addVertex(x, y + height, 0.0f).setUv(0.0f, 1.0f);
        BufferUploader.drawWithShader(builder.buildOrThrow());
    }
}
