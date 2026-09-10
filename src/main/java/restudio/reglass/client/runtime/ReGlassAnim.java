package restudio.reglass.client.runtime;

import restudio.reglass.client.api.ReGlassConfig;

public final class ReGlassAnim {
    public static final ReGlassAnim INSTANCE = new ReGlassAnim();

    private boolean initialized;
    private float tintAlpha;
    private float smoothing;
    private float blurRadius;
    private float shadowExpand;
    private float shadowFactor;
    private float shadowOffsetX;
    private float shadowOffsetY;
    private float refThickness;
    private float refFactor;
    private float refDispersion;
    private float refFresnelRange;
    private float refFresnelHardness;
    private float refFresnelFactor;
    private float glareRange;
    private float glareHardness;
    private float glareConvergence;
    private float glareOppositeFactor;
    private float glareFactor;
    private float glareAngleRad;
    private float debugStep;
    private float pixelatedGridSize;
    private float hoverScalePx;
    private float focusScalePx;
    private float focusBorderWidthPx;
    private float focusBorderIntensity;
    private float focusBorderSpeed;

    private ReGlassAnim() {
    }

    public void update(ReGlassConfig cfg, double dtSeconds) {
        float alpha = dtSeconds <= 0 ? 0 : (float) (1.0 - Math.exp(-dtSeconds / 0.15));
        if (!initialized) {
            tintAlpha = cfg.defaultTintAlpha;
            smoothing = cfg.defaultSmoothing;
            blurRadius = cfg.defaultBlurRadius;
            shadowExpand = cfg.defaultShadowExpand;
            shadowFactor = cfg.defaultShadowFactor;
            shadowOffsetX = cfg.defaultShadowOffsetX;
            shadowOffsetY = cfg.defaultShadowOffsetY;
            refThickness = cfg.defaultRefThickness;
            refFactor = cfg.defaultRefFactor;
            refDispersion = cfg.defaultRefDispersion;
            refFresnelRange = cfg.defaultRefFresnelRange;
            refFresnelHardness = cfg.defaultRefFresnelHardness;
            refFresnelFactor = cfg.defaultRefFresnelFactor;
            glareRange = cfg.defaultGlareRange;
            glareHardness = cfg.defaultGlareHardness;
            glareConvergence = cfg.defaultGlareConvergence;
            glareOppositeFactor = cfg.defaultGlareOppositeFactor;
            glareFactor = cfg.defaultGlareFactor;
            glareAngleRad = cfg.defaultGlareAngleRad;
            debugStep = cfg.debugStep;
            pixelatedGridSize = cfg.pixelatedGridSize;
            hoverScalePx = cfg.hoverScalePx;
            focusScalePx = cfg.focusScalePx;
            focusBorderWidthPx = cfg.focusBorderWidthPx;
            focusBorderIntensity = cfg.focusBorderIntensity;
            focusBorderSpeed = cfg.focusBorderSpeed;
            initialized = true;
            return;
        }
        tintAlpha = lerp(tintAlpha, cfg.defaultTintAlpha, alpha);
        smoothing = lerp(smoothing, cfg.defaultSmoothing, alpha);
        blurRadius = lerp(blurRadius, cfg.defaultBlurRadius, alpha);
        shadowExpand = lerp(shadowExpand, cfg.defaultShadowExpand, alpha);
        shadowFactor = lerp(shadowFactor, cfg.defaultShadowFactor, alpha);
        shadowOffsetX = lerp(shadowOffsetX, cfg.defaultShadowOffsetX, alpha);
        shadowOffsetY = lerp(shadowOffsetY, cfg.defaultShadowOffsetY, alpha);
        refThickness = lerp(refThickness, cfg.defaultRefThickness, alpha);
        refFactor = lerp(refFactor, cfg.defaultRefFactor, alpha);
        refDispersion = lerp(refDispersion, cfg.defaultRefDispersion, alpha);
        refFresnelRange = lerp(refFresnelRange, cfg.defaultRefFresnelRange, alpha);
        refFresnelHardness = lerp(refFresnelHardness, cfg.defaultRefFresnelHardness, alpha);
        refFresnelFactor = lerp(refFresnelFactor, cfg.defaultRefFresnelFactor, alpha);
        glareRange = lerp(glareRange, cfg.defaultGlareRange, alpha);
        glareHardness = lerp(glareHardness, cfg.defaultGlareHardness, alpha);
        glareConvergence = lerp(glareConvergence, cfg.defaultGlareConvergence, alpha);
        glareOppositeFactor = lerp(glareOppositeFactor, cfg.defaultGlareOppositeFactor, alpha);
        glareFactor = lerp(glareFactor, cfg.defaultGlareFactor, alpha);
        glareAngleRad = lerp(glareAngleRad, cfg.defaultGlareAngleRad, alpha);
        debugStep = lerp(debugStep, cfg.debugStep, alpha);
        pixelatedGridSize = lerp(pixelatedGridSize, cfg.pixelatedGridSize, alpha);
        hoverScalePx = lerp(hoverScalePx, cfg.hoverScalePx, alpha);
        focusScalePx = lerp(focusScalePx, cfg.focusScalePx, alpha);
        focusBorderWidthPx = lerp(focusBorderWidthPx, cfg.focusBorderWidthPx, alpha);
        focusBorderIntensity = lerp(focusBorderIntensity, cfg.focusBorderIntensity, alpha);
        focusBorderSpeed = lerp(focusBorderSpeed, cfg.focusBorderSpeed, alpha);
    }

    private static float lerp(float a, float b, float t) { return a + (b - a) * t; }

    public float tintAlpha() { return tintAlpha; }
    public float smoothing() { return smoothing; }
    public int blurRadiusInt() { return Math.max(0, Math.round(blurRadius)); }
    public float shadowExpand() { return shadowExpand; }
    public float shadowFactor() { return shadowFactor; }
    public float shadowOffsetX() { return shadowOffsetX; }
    public float shadowOffsetY() { return shadowOffsetY; }
    public float refThickness() { return refThickness; }
    public float refFactor() { return refFactor; }
    public float refDispersion() { return refDispersion; }
    public float refFresnelRange() { return refFresnelRange; }
    public float refFresnelHardness() { return refFresnelHardness; }
    public float refFresnelFactor() { return refFresnelFactor; }
    public float glareRange() { return glareRange; }
    public float glareHardness() { return glareHardness; }
    public float glareConvergence() { return glareConvergence; }
    public float glareOppositeFactor() { return glareOppositeFactor; }
    public float glareFactor() { return glareFactor; }
    public float glareAngleRad() { return glareAngleRad; }
    public float debugStep() { return debugStep; }
    public float pixelatedGridSize() { return pixelatedGridSize; }
    public float hoverScalePx() { return hoverScalePx; }
    public float focusScalePx() { return focusScalePx; }
    public float focusBorderWidthPx() { return focusBorderWidthPx; }
    public float focusBorderIntensity() { return focusBorderIntensity; }
    public float focusBorderSpeed() { return focusBorderSpeed; }
}
