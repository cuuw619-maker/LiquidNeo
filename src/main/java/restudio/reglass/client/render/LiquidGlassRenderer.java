package restudio.reglass.client.render;

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
import restudio.reglass.client.LiquidGlassPipelines;
import restudio.reglass.client.LiquidGlassUniforms;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.WidgetStyle;

public final class LiquidGlassRenderer {
    private static TextureTarget sceneCopy;
    private static TextureTarget blurA;
    private static TextureTarget blurB;
    private static int targetWidth;
    private static int targetHeight;
    private static boolean prepared;

    private LiquidGlassRenderer() {
    }

    public static void registerShaders(RegisterShadersEvent event) {
        LiquidGlassPipelines.registerShaders(event);
    }

    public static void beginFrame() {
        prepared = false;
    }

    public static void render(GuiGraphics graphics, int x, int y, int width, int height, float radius,
                              WidgetStyle style, float hover, float focus, net.minecraft.network.chat.Component text) {
        if (width <= 0 || height <= 0 || !LiquidGlassPipelines.ready()) return;

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        ensureTargets(main.width, main.height);
        prepareBackground(main);

        ShaderInstance shader = LiquidGlassPipelines.glassShader();
        if (shader == null) return;

        float scale = (float) minecraft.getWindow().getGuiScale();
        int px = Math.round(x * scale);
        int py = Math.round(y * scale);
        int pw = Math.max(1, Math.round(width * scale));
        int ph = Math.max(1, Math.round(height * scale));
        float pr = radius * scale;

        main.bindWrite(false);
        RenderSystem.viewport(0, 0, main.width, main.height);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(() -> shader);

        LiquidGlassUniforms uniforms = LiquidGlassUniforms.get();
        uniforms.applyWidget(shader, main.width, main.height, px, py, pw, ph, pr, style, hover, focus);
        LiquidGlassUniforms.bindSampler(shader, "SceneSampler", sceneCopy);
        LiquidGlassUniforms.bindSampler(shader, "BlurSampler", blurB);
        drawQuad(px, main.height - py - ph, pw, ph);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        if (text != null && !text.getString().isEmpty()) {
            graphics.drawCenteredString(minecraft.font, text, x + width / 2, y + height / 2 - 4, 0xFFFFFFFF);
        }
    }

    public static void renderCapsule(GuiGraphics graphics, int x, int y, int width, int height, float progress, WidgetStyle style) {
        if (width <= 0 || height <= 0 || !LiquidGlassPipelines.ready()) return;

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        ensureTargets(main.width, main.height);
        prepareBackground(main);

        ShaderInstance shader = LiquidGlassPipelines.glassShader();
        if (shader == null) return;
        float scale = (float) minecraft.getWindow().getGuiScale();
        int px = Math.round(x * scale);
        int py = Math.round(y * scale);
        int pw = Math.max(1, Math.round(width * scale));
        int ph = Math.max(1, Math.round(height * scale));

        main.bindWrite(false);
        RenderSystem.viewport(0, 0, main.width, main.height);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(() -> shader);

        LiquidGlassUniforms.get().applyCapsule(shader, main.width, main.height, px, py, pw, ph, progress, style);
        LiquidGlassUniforms.bindSampler(shader, "SceneSampler", sceneCopy);
        LiquidGlassUniforms.bindSampler(shader, "BlurSampler", blurB);
        drawQuad(px, main.height - py - ph, pw, ph);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    private static void prepareBackground(RenderTarget main) {
        if (prepared) return;
        prepared = true;
        ensureTargets(main.width, main.height);

        ShaderInstance copy = LiquidGlassPipelines.copyShader();
        ShaderInstance blur = LiquidGlassPipelines.blurShader();
        if (copy == null || blur == null) return;

        sceneCopy.bindWrite(true);
        RenderSystem.viewport(0, 0, main.width, main.height);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShader(() -> copy);
        LiquidGlassUniforms.get().applyCommon(copy, main.width, main.height);
        LiquidGlassUniforms.bindSampler(copy, "SceneSampler", main);
        drawQuad(0, 0, main.width, main.height);

        blurA.bindWrite(true);
        RenderSystem.viewport(0, 0, main.width, main.height);
        RenderSystem.setShader(() -> blur);
        LiquidGlassUniforms.get().applyBlur(blur, main.width, main.height, ReGlassConfig.INSTANCE.defaultBlurRadius, 1.0f, 0.0f);
        LiquidGlassUniforms.bindSampler(blur, "DiffuseSampler", sceneCopy);
        drawQuad(0, 0, main.width, main.height);

        blurB.bindWrite(true);
        RenderSystem.viewport(0, 0, main.width, main.height);
        RenderSystem.setShader(() -> blur);
        LiquidGlassUniforms.get().applyBlur(blur, main.width, main.height, ReGlassConfig.INSTANCE.defaultBlurRadius, 0.0f, 1.0f);
        LiquidGlassUniforms.bindSampler(blur, "DiffuseSampler", blurA);
        drawQuad(0, 0, main.width, main.height);

        main.bindWrite(false);
        RenderSystem.viewport(0, 0, main.width, main.height);
        RenderSystem.enableDepthTest();
    }

    private static void ensureTargets(int width, int height) {
        if (width <= 0 || height <= 0) return;
        if (sceneCopy != null && width == targetWidth && height == targetHeight) return;
        destroyTargets();
        sceneCopy = new TextureTarget(width, height, false, Minecraft.ON_OSX);
        blurA = new TextureTarget(width, height, false, Minecraft.ON_OSX);
        blurB = new TextureTarget(width, height, false, Minecraft.ON_OSX);
        sceneCopy.setFilterMode(9729);
        blurA.setFilterMode(9729);
        blurB.setFilterMode(9729);
        targetWidth = width;
        targetHeight = height;
    }

    private static void destroyTargets() {
        if (sceneCopy != null) sceneCopy.destroyBuffers();
        if (blurA != null) blurA.destroyBuffers();
        if (blurB != null) blurB.destroyBuffers();
        sceneCopy = null;
        blurA = null;
        blurB = null;
        targetWidth = 0;
        targetHeight = 0;
    }

    private static void drawQuad(int x, int y, int width, int height) {
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.addVertex(x, y, 0.0f).setUv(0.0f, 0.0f);
        builder.addVertex(x + width, y, 0.0f).setUv(1.0f, 0.0f);
        builder.addVertex(x + width, y + height, 0.0f).setUv(1.0f, 1.0f);
        builder.addVertex(x, y + height, 0.0f).setUv(0.0f, 1.0f);
        BufferUploader.drawWithShader(builder.buildOrThrow());
    }
}
