package restudio.reglassneo.client;

import net.minecraft.client.renderer.ShaderInstance;
import restudio.reglassneo.client.api.WidgetStyle;
import restudio.reglassneo.client.runtime.ReGlassAnim;

public final class LiquidGlassUniforms {
    private static final LiquidGlassUniforms INSTANCE = new LiquidGlassUniforms();
    private LiquidGlassUniforms() {}
    public static LiquidGlassUniforms get() { return INSTANCE; }

    public void updateTime(ShaderInstance shader) {
        setFloat(shader, "Time", ReGlassAnim.INSTANCE.timeSeconds());
    }

    public void applyWidget(ShaderInstance shader, int sw, int sh, int x, int y, int w, int h,
                            float radius, WidgetStyle style, float hover, float focus, float progress) {
        setVec2(shader, "ScreenSize", sw, sh);
        setVec4(shader, "Rect", x, sh - y - h, w, h);
        setFloat(shader, "Radius", Math.min(radius, Math.min(w, h) * .5f));
        setColor(shader, "Tint", style.getTintColor(), style.getTintAlpha());
        setVec4(shader, "Refraction", style.getRefThickness(), style.getRefFactor(),
                style.getRefDispersion(), style.getRefFresnelRange());
        setVec4(shader, "Fresnel", style.getRefFresnelHardness(), style.getRefFresnelFactor(),
                style.getRefFresnelRange(), 0f);
        setVec4(shader, "Glare", style.getGlareRange(), style.getGlareHardness(),
                style.getGlareFactor(), style.getGlareAngleRad());
        setVec4(shader, "Shadow", style.getShadowExpand(), style.getShadowFactor(),
                style.getShadowOffsetX(), -style.getShadowOffsetY());
        setColor(shader, "ShadowColor", style.getShadowColor(), style.getShadowColorAlpha());
        setVec2(shader, "HoverFocus", clamp(hover), clamp(focus));
        setFloat(shader, "Progress", progress);
        setVec4(shader, "ProgressColor", 1f, 1f, 1f, .72f);
        updateTime(shader);
    }

    private static float clamp(float v) {
        return Float.isFinite(v) ? Math.max(0f, Math.min(1f, v)) : 0f;
    }
    private static void setFloat(ShaderInstance s, String n, float v) { var u=s.getUniform(n); if(u!=null) u.set(v); }
    private static void setVec2(ShaderInstance s, String n, float x, float y) { var u=s.getUniform(n); if(u!=null) u.set(x,y); }
    private static void setVec4(ShaderInstance s, String n, float x, float y, float z, float w) { var u=s.getUniform(n); if(u!=null) u.set(x,y,z,w); }
    private static void setColor(ShaderInstance s, String n, int rgb, float a) {
        setVec4(s,n,((rgb>>16)&255)/255f,((rgb>>8)&255)/255f,(rgb&255)/255f,clamp(a));
    }
}
