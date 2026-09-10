package restudio.reglassneo.client.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import restudio.reglassneo.client.render.LiquidGlassRenderer;

public final class ReGlassApi {
    public static final WidgetStyle inactiveStyle=new WidgetStyle().tint(0x000000,.30f);private ReGlassApi(){}
    public static ReGlassConfig getGlobalConfig(){return ReGlassConfig.INSTANCE;}public static Builder create(GuiGraphics graphics){return new Builder(graphics);}
    public static final class Builder {private final GuiGraphics graphics;private int x,y,width,height;private float cornerRadius=-1f;@Nullable private Component text;private WidgetStyle style=new WidgetStyle();private float hoverAmount,focusAmount;private Builder(GuiGraphics graphics){this.graphics=graphics;}public Builder fromWidget(AbstractWidget w){return position(w.getX(),w.getY()).size(w.getWidth(),w.getHeight()).text(w.getMessage());}public Builder position(int x,int y){this.x=x;this.y=y;return this;}public Builder size(int w,int h){width=w;height=h;return this;}public Builder dimensions(int x,int y,int w,int h){return position(x,y).size(w,h);}public Builder cornerRadius(float r){cornerRadius=r;return this;}public Builder text(@Nullable Component t){text=t;return this;}public Builder style(WidgetStyle s){style=s==null?new WidgetStyle():s;return this;}public Builder hover(float a){hoverAmount=clamp(a);return this;}public Builder focus(float a){focusAmount=clamp(a);return this;}public Builder selected(float a){return focus(a);}public void render(){float r=cornerRadius<0?Math.min(width,height)*.5f:cornerRadius;LiquidGlassRenderer.render(graphics,x,y,width,height,r,style,hoverAmount,focusAmount,text);}private static float clamp(float v){return Float.isNaN(v)?0f:Math.max(0f,Math.min(1f,v));}}
}
