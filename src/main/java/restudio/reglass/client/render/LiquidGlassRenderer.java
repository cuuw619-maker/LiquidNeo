package restudio.reglass.client.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import restudio.reglass.client.api.WidgetStyle;

/**
 * Immediate-mode Liquid Glass renderer for Minecraft 1.21.1.
 * It uses layered rounded geometry so the public API is usable independently
 * of the original Fabric render-state pipeline.
 */
public final class LiquidGlassRenderer {
    private LiquidGlassRenderer() {}

    public static void render(GuiGraphics graphics, int x, int y, int width, int height, float radius,
                              WidgetStyle style, float hover, float focus, Component text) {
        int r = Math.max(0, Math.min(Math.min(width, height) / 2, Math.round(radius)));
        int expand = Math.max(0, Math.round(style.getShadowExpand() * Math.max(0f, style.getShadowFactor()) * .18f));
        int sx = Math.round(style.getShadowOffsetX());
        int sy = Math.round(style.getShadowOffsetY());

        int shadowAlpha = alpha(style.getShadowColorAlpha() * style.getShadowFactor());
        if (shadowAlpha > 0 && expand > 0) {
            rounded(graphics, x-expand+sx, y-expand+sy, width+expand*2, height+expand*2, r+expand, rgba(style.getShadowColor(), shadowAlpha));
        }

        float scale = 1f + (style.getHoverScalePx() / Math.max(1f, Math.min(width, height))) * hover;
        int dw = Math.round(width * scale), dh = Math.round(height * scale);
        int dx = x - (dw-width)/2, dy = y - (dh-height)/2;
        int base = rgba(style.getTintColor(), Math.max(18, alpha(.22f + style.getTintAlpha()*.55f)));
        rounded(graphics, dx, dy, dw, dh, Math.min(r,Math.min(dw,dh)/2), base);

        int highlight = alpha(.13f + .12f * hover + .18f * focus);
        rounded(graphics, dx+1, dy+1, Math.max(1,dw-2), Math.max(1,dh-2), Math.max(0,r-1), rgba(0xFFFFFF, highlight));
        int inner = alpha(.08f + style.getSmoothing()*2f);
        rounded(graphics, dx+2, dy+2, Math.max(1,dw-4), Math.max(1,dh-4), Math.max(0,r-2), rgba(style.getTintColor(), inner));

        if (focus > 0f && style.getFocusBorderWidthPx() > 0f) {
            int border = Math.max(1, Math.round(style.getFocusBorderWidthPx()));
            int a = alpha(style.getFocusBorderIntensity() * focus);
            rounded(graphics, dx, dy, dw, border, Math.min(r,border/2), rgba(0xFFFFFF,a));
            rounded(graphics, dx, dy+dh-border, dw, border, Math.min(r,border/2), rgba(0xFFFFFF,a));
        }

        if (text != null && !text.getString().isEmpty()) {
            int color = 0xFFFFFFFF;
            int tw = graphics.guiWidth();
            int tx = dx + dw/2 - 50;
            graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, text, dx+dw/2, dy+dh/2-4, color);
        }
    }

    public static void renderCapsule(GuiGraphics graphics, int x, int y, int width, int height, float progress, WidgetStyle style) {
        progress=Math.max(0f,Math.min(1f,progress));
        int radius=Math.min(width,height)/2;
        render(graphics,x,y,width,height,radius,style,0f,0f,null);
        int innerX=x+2, innerY=y+2, innerW=Math.max(0,width-4), innerH=Math.max(0,height-4);
        int fillW=Math.round(innerW*progress);
        if(fillW>0) rounded(graphics,innerX,innerY,fillW,innerH,Math.min(radius-2,fillW/2),rgba(0xFFFFFF,190));
    }

    private static int alpha(float value) { return Math.max(0,Math.min(255,Math.round(value*255f))); }
    private static int rgba(int rgb,int a) { return (a<<24)|(rgb&0xFFFFFF); }

    private static void rounded(GuiGraphics g,int x,int y,int w,int h,int radius,int color) {
        if(w<=0||h<=0)return;
        radius=Math.min(radius,Math.min(w,h)/2);
        if(radius<=0){g.fill(x,y,x+w,y+h,color);return;}
        g.fill(x+radius,y,x+w-radius,y+h,color);
        g.fill(x,y+radius,x+w,y+h-radius,color);
        for(int i=0;i<radius;i++){
            double dy=radius-i-.5;
            int dx=(int)Math.ceil(radius-Math.sqrt(Math.max(0,radius*radius-dy*dy)));
            g.fill(x+dx,y+i,x+w-dx,y+i+1,color);
            g.fill(x+dx,y+h-i-1,x+w-dx,y+h-i,color);
        }
    }
}
