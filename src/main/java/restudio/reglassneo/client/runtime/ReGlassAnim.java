package restudio.reglassneo.client.runtime;

import restudio.reglassneo.client.api.ReGlassConfig;

/**
 * Frame-rate independent animation state. All expensive configuration
 * interpolation happens once per client tick; shaders receive the cached time
 * instead of querying a clock for every widget.
 */
public final class ReGlassAnim {
    public static final ReGlassAnim INSTANCE = new ReGlassAnim();

    private boolean initialized;
    private float tintAlpha;
    private float timeSeconds;
    private float shimmerPhase;
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

    private ReGlassAnim() {}

    public void update(ReGlassConfig c, double dt) {
        float delta = dt <= 0.0 ? 0.0f : (float) Math.min(dt, 0.1);
        timeSeconds += delta;
        if (timeSeconds >= 100000.0f) timeSeconds -= 100000.0f;
        shimmerPhase = timeSeconds * 1.6f;

        float alpha = delta <= 0.0f ? 0.0f : 1.0f - (float) Math.exp(-delta / 0.15f);
        if (!initialized) {
            tintAlpha = c.defaultTintAlpha;
            smoothing = c.defaultSmoothing;
            blurRadius = c.defaultBlurRadius;
            shadowExpand = c.defaultShadowExpand;
            shadowFactor = c.defaultShadowFactor;
            shadowOffsetX = c.defaultShadowOffsetX;
            shadowOffsetY = c.defaultShadowOffsetY;
            refThickness = c.defaultRefThickness;
            refFactor = c.defaultRefFactor;
            refDispersion = c.defaultRefDispersion;
            refFresnelRange = c.defaultRefFresnelRange;
            refFresnelHardness = c.defaultRefFresnelHardness;
            refFresnelFactor = c.defaultRefFresnelFactor;
            glareRange = c.defaultGlareRange;
            glareHardness = c.defaultGlareHardness;
            glareConvergence = c.defaultGlareConvergence;
            glareOppositeFactor = c.defaultGlareOppositeFactor;
            glareFactor = c.defaultGlareFactor;
            glareAngleRad = c.defaultGlareAngleRad;
            debugStep = c.debugStep;
            pixelatedGridSize = c.pixelatedGridSize;
            hoverScalePx = c.hoverScalePx;
            focusScalePx = c.focusScalePx;
            focusBorderWidthPx = c.focusBorderWidthPx;
            focusBorderIntensity = c.focusBorderIntensity;
            focusBorderSpeed = c.focusBorderSpeed;
            initialized = true;
            return;
        }

        tintAlpha = lerp(tintAlpha, c.defaultTintAlpha, alpha);
        smoothing = lerp(smoothing, c.defaultSmoothing, alpha);
        blurRadius = lerp(blurRadius, c.defaultBlurRadius, alpha);
        shadowExpand = lerp(shadowExpand, c.defaultShadowExpand, alpha);
        shadowFactor = lerp(shadowFactor, c.defaultShadowFactor, alpha);
        shadowOffsetX = lerp(shadowOffsetX, c.defaultShadowOffsetX, alpha);
        shadowOffsetY = lerp(shadowOffsetY, c.defaultShadowOffsetY, alpha);
        refThickness = lerp(refThickness, c.defaultRefThickness, alpha);
        refFactor = lerp(refFactor, c.defaultRefFactor, alpha);
        refDispersion = lerp(refDispersion, c.defaultRefDispersion, alpha);
        refFresnelRange = lerp(refFresnelRange, c.defaultRefFresnelRange, alpha);
        refFresnelHardness = lerp(refFresnelHardness, c.defaultRefFresnelHardness, alpha);
        refFresnelFactor = lerp(refFresnelFactor, c.defaultRefFresnelFactor, alpha);
        glareRange = lerp(glareRange, c.defaultGlareRange, alpha);
        glareHardness = lerp(glareHardness, c.defaultGlareHardness, alpha);
        glareConvergence = lerp(glareConvergence, c.defaultGlareConvergence, alpha);
        glareOppositeFactor = lerp(glareOppositeFactor, c.defaultGlareOppositeFactor, alpha);
        glareFactor = lerp(glareFactor, c.defaultGlareFactor, alpha);
        glareAngleRad = lerp(glareAngleRad, c.defaultGlareAngleRad, alpha);
        debugStep = lerp(debugStep, c.debugStep, alpha);
        pixelatedGridSize = lerp(pixelatedGridSize, c.pixelatedGridSize, alpha);
        hoverScalePx = lerp(hoverScalePx, c.hoverScalePx, alpha);
        focusScalePx = lerp(focusScalePx, c.focusScalePx, alpha);
        focusBorderWidthPx = lerp(focusBorderWidthPx, c.focusBorderWidthPx, alpha);
        focusBorderIntensity = lerp(focusBorderIntensity, c.focusBorderIntensity, alpha);
        focusBorderSpeed = lerp(focusBorderSpeed, c.focusBorderSpeed, alpha);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public float timeSeconds() { return timeSeconds; }
    public float shimmerPhase() { return shimmerPhase; }
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
