package restudio.reglass.client.api;

import restudio.reglass.client.runtime.ReGlassAnim;

public class WidgetStyle {
    private boolean hasTint; private int tintColor; private float tintAlpha;
    private boolean hasSmoothing; private float smoothingFactor;
    private boolean hasBlurRadius; private int blurRadius;
    private boolean hasShadow; private float shadowExpand, shadowFactor, shadowOffsetX, shadowOffsetY; private int shadowColor; private float shadowColorAlpha;
    private boolean hasRefraction; private float refThickness, refFactor, refDispersion, refFresnelRange, refFresnelHardness, refFresnelFactor;
    private boolean hasGlare; private float glareRange, glareHardness, glareConvergence, glareOppositeFactor, glareFactor, glareAngleRad;

    public static WidgetStyle create() { return new WidgetStyle(); }
    public WidgetStyle tint(int color,float alpha){hasTint=true;tintColor=color;tintAlpha=alpha;return this;}
    public WidgetStyle smoothing(float v){hasSmoothing=true;smoothingFactor=v;return this;}
    public WidgetStyle blurRadius(int v){hasBlurRadius=true;blurRadius=Math.max(0,v);return this;}
    public WidgetStyle shadow(float e,float f,float x,float y){hasShadow=true;shadowExpand=e;shadowFactor=f;shadowOffsetX=x;shadowOffsetY=y;return this;}
    public WidgetStyle shadowColor(int c,float a){hasShadow=true;shadowColor=c;shadowColorAlpha=a;return this;}
    public WidgetStyle refractionThickness(float v){hasRefraction=true;refThickness=v;return this;}
    public WidgetStyle refractionFactor(float v){hasRefraction=true;refFactor=v;return this;}
    public WidgetStyle refractionDispersion(float v){hasRefraction=true;refDispersion=v;return this;}
    public WidgetStyle fresnelRange(float v){hasRefraction=true;refFresnelRange=v;return this;}
    public WidgetStyle fresnelHardness(float v){hasRefraction=true;refFresnelHardness=v;return this;}
    public WidgetStyle fresnelFactor(float v){hasRefraction=true;refFresnelFactor=v;return this;}
    public WidgetStyle glareRange(float v){hasGlare=true;glareRange=v;return this;}
    public WidgetStyle glareHardness(float v){hasGlare=true;glareHardness=v;return this;}
    public WidgetStyle glareConvergence(float v){hasGlare=true;glareConvergence=v;return this;}
    public WidgetStyle glareOppositeFactor(float v){hasGlare=true;glareOppositeFactor=v;return this;}
    public WidgetStyle glareFactor(float v){hasGlare=true;glareFactor=v;return this;}
    public WidgetStyle glareAngleRad(float v){hasGlare=true;glareAngleRad=v;return this;}

    public int getTintColor(){return hasTint?tintColor:ReGlassConfig.INSTANCE.defaultTintColor;}
    public float getTintAlpha(){return hasTint?tintAlpha:ReGlassAnim.INSTANCE.tintAlpha();}
    public float getSmoothing(){return hasSmoothing?smoothingFactor:ReGlassAnim.INSTANCE.smoothing();}
    public int getBlurRadius(){return hasBlurRadius?blurRadius:ReGlassAnim.INSTANCE.blurRadiusInt();}
    public float getShadowExpand(){return hasShadow?shadowExpand:ReGlassAnim.INSTANCE.shadowExpand();}
    public float getShadowFactor(){return hasShadow?shadowFactor:ReGlassAnim.INSTANCE.shadowFactor();}
    public float getShadowOffsetX(){return hasShadow?shadowOffsetX:ReGlassAnim.INSTANCE.shadowOffsetX();}
    public float getShadowOffsetY(){return hasShadow?shadowOffsetY:ReGlassAnim.INSTANCE.shadowOffsetY();}
    public int getShadowColor(){return hasShadow?shadowColor:ReGlassConfig.INSTANCE.defaultShadowColor;}
    public float getShadowColorAlpha(){return hasShadow?shadowColorAlpha:ReGlassConfig.INSTANCE.defaultShadowColorAlpha;}
    public float getRefThickness(){return hasRefraction?refThickness:ReGlassAnim.INSTANCE.refThickness();}
    public float getRefFactor(){return hasRefraction?refFactor:ReGlassAnim.INSTANCE.refFactor();}
    public float getRefDispersion(){return hasRefraction?refDispersion:ReGlassAnim.INSTANCE.refDispersion();}
    public float getRefFresnelRange(){return hasRefraction?refFresnelRange:ReGlassAnim.INSTANCE.refFresnelRange();}
    public float getRefFresnelHardness(){return hasRefraction?refFresnelHardness:ReGlassAnim.INSTANCE.refFresnelHardness();}
    public float getRefFresnelFactor(){return hasRefraction?refFresnelFactor:ReGlassAnim.INSTANCE.refFresnelFactor();}
    public float getGlareRange(){return hasGlare?glareRange:ReGlassAnim.INSTANCE.glareRange();}
    public float getGlareHardness(){return hasGlare?glareHardness:ReGlassAnim.INSTANCE.glareHardness();}
    public float getGlareConvergence(){return hasGlare?glareConvergence:ReGlassAnim.INSTANCE.glareConvergence();}
    public float getGlareOppositeFactor(){return hasGlare?glareOppositeFactor:ReGlassAnim.INSTANCE.glareOppositeFactor();}
    public float getGlareFactor(){return hasGlare?glareFactor:ReGlassAnim.INSTANCE.glareFactor();}
    public float getGlareAngleRad(){return hasGlare?glareAngleRad:ReGlassAnim.INSTANCE.glareAngleRad();}
    public float getHoverScalePx(){return ReGlassConfig.INSTANCE.hoverScalePx;}
    public float getFocusScalePx(){return ReGlassConfig.INSTANCE.focusScalePx;}
    public float getFocusBorderWidthPx(){return ReGlassConfig.INSTANCE.focusBorderWidthPx;}
    public float getFocusBorderIntensity(){return ReGlassConfig.INSTANCE.focusBorderIntensity;}
}
