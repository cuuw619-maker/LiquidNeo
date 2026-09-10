package restudio.reglass.client.api;

import java.util.HashSet;
import java.util.Set;

public final class ReGlassConfig {
    public static final ReGlassConfig INSTANCE = new ReGlassConfig();

    public final Features features = new Features();

    public int defaultTintColor = 0x000000;
    public float defaultTintAlpha = 0.0f;
    public float defaultSmoothing = 0.003f;
    public int defaultBlurRadius = 12;

    public float defaultShadowExpand = 30.0f;
    public float defaultShadowFactor = 0.25f;
    public float defaultShadowOffsetX = 0.0f;
    public float defaultShadowOffsetY = 2.0f;
    public int defaultShadowColor = 0x000000;
    public float defaultShadowColorAlpha = 1.0f;

    public float defaultRefThickness = 20.0f;
    public float defaultRefFactor = 1.4f;
    public float defaultRefDispersion = 7.0f;
    public float defaultRefFresnelRange = 30.0f;
    public float defaultRefFresnelHardness = 20.0f;
    public float defaultRefFresnelFactor = 20.0f;

    public float defaultGlareRange = 30.0f;
    public float defaultGlareHardness = 20.0f;
    public float defaultGlareConvergence = 50.0f;
    public float defaultGlareOppositeFactor = 80.0f;
    public float defaultGlareFactor = 90.0f;
    public float defaultGlareAngleRad = (float) (-45.0 * Math.PI / 180.0);

    public float pixelEpsilon = 2.0f;
    public float debugStep = 9.0f;
    public float pixelatedGridSize = 8.0f;

    public float hoverScalePx = 1.5f;
    public float focusScalePx = 2.5f;
    public float focusBorderWidthPx = 2.0f;
    public float focusBorderIntensity = 0.75f;
    public float focusBorderSpeed = 1.6f;

    private ReGlassConfig() {
    }

    public static final class Features {
        public boolean enableRedesign = true;
        public boolean buttons = true;
        public boolean sliders = true;
        public boolean hotbar = true;
        public boolean cancelScreenDarkening = true;
        public boolean pixelatedGrid = false;

        public final Set<String> classWhitelist = new HashSet<>();
        public final Set<String> classBlacklist = new HashSet<>();

        public boolean isClassExcluded(Class<?> clazz) {
            String name = clazz.getName();
            if (!classWhitelist.isEmpty()) {
                return !classWhitelist.contains(name);
            }
            return classBlacklist.contains(name);
        }
    }
}
