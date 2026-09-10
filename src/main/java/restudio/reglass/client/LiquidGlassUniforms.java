package restudio.reglass.client;

import net.minecraft.client.renderer.ShaderInstance;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.WidgetStyle;

public final class LiquidGlassUniforms {
    private static final LiquidGlassUniforms INSTANCE = new LiquidGlassUniforms();

    private LiquidGlassUniforms() {
    }

    public static LiquidGlassUniforms get() {
        return INSTANCE;
    }

    public void updateTime(ShaderInstance shader) {
        setFloat(shader, "Time", (float) (System.nanoTime() / 1_000_000_000.0));
    }

    public void applyCommon(ShaderInstance shader, int screenWidth, int screenHeight) {
        setVec2(shader, "ScreenSize", screenWidth, screenHeight);
    }

    public void applyBlur(ShaderInstance shader, int width, int height, float radius, float dx, float dy) {
        applyCommon(shader, width, height);
        setVec2(shader, "Direction", dx, dy);
        setFloat(shader, "Radius", Math.max(0.0f, Math.min(32.0f, radius)));
    }

    public void applyWidget(ShaderInstance shader, int width, int height, int x, int y, int widgetWidth,
                            int widgetHeight, float radius, WidgetStyle style, float hover, float focus) {
        applyCommon(shader, width, height);
        setVec4(shader, "Rect", x, height - y - widgetHeight, widgetWidth, widgetHeight);
        setFloat(shader, "Radius", Math.min(radius, Math.min(widgetWidth, widgetHeight) * 0.5f));
        setColor(shader, "Tint", style.getTintColor(), style.getTintAlpha());
        setVec4(shader, "Refraction", style.getRefThickness(), style.getRefFactor(), style.getRefDispersion(), style.getRefFresnelRange());
        setVec4(shader, "Fresnel", style.getRefFresnelHardness(), style.getRefFresnelFactor(), style.getRefFresnelRange(), 0.0f);
        setVec4(shader, "Glare", style.getGlareRange(), style.getGlareHardness(), style.getGlareFactor(), style.getGlareAngleRad());
        setVec4(shader, "Shadow", style.getShadowExpand(), style.getShadowFactor(), style.getShadowOffsetX(), -style.getShadowOffsetY());
        setColor(shader, "ShadowColor", style.getShadowColor(), style.getShadowColorAlpha());
        setVec2(shader, "HoverFocus", clamp(hover), clamp(focus));
        setFloat(shader, "Progress", -1.0f);
        setVec4(shader, "ProgressColor", 1.0f, 1.0f, 1.0f, 0.75f);
    }

    public void applyCapsule(ShaderInstance shader, int width, int height, int x, int y, int capsuleWidth,
                             int capsuleHeight, float progress, WidgetStyle style) {
        applyWidget(shader, width, height, x, y, capsuleWidth, capsuleHeight,
                Math.min(capsuleWidth, capsuleHeight) * 0.5f, style, 0.0f, 0.0f);
        setFloat(shader, "Progress", Math.max(0.0f, Math.min(1.0f, progress)));
        setVec4(shader, "ProgressColor", 1.0f, 1.0f, 1.0f, 0.72f);
    }

    private static float clamp(float value) {
        return Float.isFinite(value) ? Math.max(0.0f, Math.min(1.0f, value)) : 0.0f;
    }

    private static void setFloat(ShaderInstance shader, String name, float value) {
        var uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(value);
    }

    private static void setVec2(ShaderInstance shader, String name, float x, float y) {
        var uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(x, y);
    }

    private static void setVec4(ShaderInstance shader, String name, float x, float y, float z, float w) {
        var uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(x, y, z, w);
    }

    private static void setColor(ShaderInstance shader, String name, int rgb, float alpha) {
        setVec4(shader, name,
                ((rgb >> 16) & 255) / 255.0f,
                ((rgb >> 8) & 255) / 255.0f,
                (rgb & 255) / 255.0f,
                clamp(alpha));
    }
}
