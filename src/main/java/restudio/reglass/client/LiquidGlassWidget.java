package restudio.reglass.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import restudio.reglass.client.api.ReGlassApi;
import restudio.reglass.client.api.WidgetStyle;

public class LiquidGlassWidget extends AbstractWidget {
    private float cornerRadiusPx;
    private boolean moveable;
    private boolean dragging;
    private int dragOffsetX, dragOffsetY;
    public WidgetStyle style;

    public LiquidGlassWidget(int x, int y, int width, int height, WidgetStyle style) {
        super(x, y, width, height, Component.empty());
        cornerRadiusPx = Math.min(width, height) * 0.5f;
        this.style = style == null ? new WidgetStyle() : style;
    }

    public LiquidGlassWidget setCornerRadiusPx(float radius) {
        cornerRadiusPx = Math.max(0, radius);
        return this;
    }

    public LiquidGlassWidget setMoveable(boolean value) {
        moveable = value;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        ReGlassApi.create(graphics)
                .fromWidget(this)
                .cornerRadius(cornerRadiusPx)
                .style(style)
                .hover(isHovered() ? 1f : 0f)
                .focus(isFocused() ? 1f : 0f)
                .render();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (moveable && button == 0 && isMouseOver(mouseX, mouseY)) {
            dragging = true;
            dragOffsetX = (int) mouseX - getX();
            dragOffsetY = (int) mouseY - getY();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (dragging && button == 0) {
            setX((int) mouseX - dragOffsetX);
            setY((int) mouseY - dragOffsetY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
