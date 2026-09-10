package restudio.reglassneo.client.api;

import java.util.HashSet;
import java.util.Set;

public final class ReGlassConfig {
 public static final ReGlassConfig INSTANCE=new ReGlassConfig();public final Features features=new Features();
 public int defaultTintColor=0x000000;public float defaultTintAlpha=.12f;public float defaultSmoothing=.003f;public int defaultBlurRadius=12;public float defaultShadowExpand=30f;public float defaultShadowFactor=.25f;public float defaultShadowOffsetX=0f;public float defaultShadowOffsetY=2f;public int defaultShadowColor=0x000000;public float defaultShadowColorAlpha=1f;public float defaultRefThickness=20f;public float defaultRefFactor=1.4f;public float defaultRefDispersion=7f;public float defaultRefFresnelRange=30f;public float defaultRefFresnelHardness=20f;public float defaultRefFresnelFactor=20f;public float defaultGlareRange=30f;public float defaultGlareHardness=20f;public float defaultGlareConvergence=50f;public float defaultGlareOppositeFactor=80f;public float defaultGlareFactor=90f;public float defaultGlareAngleRad=(float)(-45*Math.PI/180);public float pixelEpsilon=2f,debugStep=9f,pixelatedGridSize=8f,hoverScalePx=1.5f,focusScalePx=2.5f,focusBorderWidthPx=2f,focusBorderIntensity=.75f,focusBorderSpeed=1.6f;private ReGlassConfig(){}
 public static final class Features {public boolean enableRedesign=true,buttons=true,sliders=true,hotbar=true,cancelScreenDarkening=true,pixelatedGrid=false;public final Set<String> classWhitelist=new HashSet<>(),classBlacklist=new HashSet<>();public boolean isClassExcluded(Class<?> clazz){String name=clazz.getName();if(!classWhitelist.isEmpty())return !classWhitelist.contains(name);return classBlacklist.contains(name);}}
}
