package restudio.reglass.client.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import restudio.reglass.client.render.LiquidGlassRenderer;

public final class ReGlassApi {
    public static final WidgetStyle inactiveStyle = new WidgetStyle().tint(0x000000, 0.30f);
    private ReGlassApi() {}

    public static ReGlassConfig getGlobalConfig() { return ReGlassConfig.INSTANCE; }
    public static Builder create(GuiGraphics graphics) { return new Builder(graphics); }

    public static final class Builder {
        private final GuiGraphics graphics;
        private int x, y, width, height;
        private float cornerRadius = -1f;
        @Nullable private Component text;
        private WidgetStyle style = new WidgetStyle();
        private float hoverAmount;
        private float focusAmount;

        private Builder(GuiGraphics graphics) { this.graphics = graphics; }
        public Builder fromWidget(AbstractWidget widget) { return position(widget.getX(),widget.getY()).size(widget.getWidth(),widget.getHeight()).text(widget.getMessage()); }
        public Builder position(int x,int y) { this.x=x; this.y=y; return this; }
        public Builder size(int width,int height) { this.width=width; this.height=height; return this; }
        public Builder dimensions(int x,int y,int width,int height) { return position(x,y).size(width,height); }
        public Builder cornerRadius(float radius) { cornerRadius=radius; return this; }
        public Builder text(@Nullable Component text) { this.text=text; return this; }
        public Builder style(WidgetStyle style) { this.style=style == null ? new WidgetStyle() : style; return this; }
        public Builder hover(float amount) { hoverAmount=clamp(amount); return this; }
        public Builder focus(float amount) { focusAmount=clamp(amount); return this; }
        public Builder selected(float amount) { return focus(amount); }
        public void render() {
            float radius=cornerRadius < 0 ? Math.min(width,height)*.5f : cornerRadius;
            LiquidGlassRenderer.render(graphics,x,y,width,height,radius,style,hoverAmount,focusAmount,text);
        }
        private static float clamp(float v) { return Float.isNaN(v) ? 0f : Math.max(0f,Math.min(1f,v)); }
    }
}
