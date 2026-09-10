package restudio.reglassneo.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import restudio.reglassneo.client.api.ReGlassConfig;
import restudio.reglassneo.client.api.WidgetStyle;
import restudio.reglassneo.client.render.LiquidGlassRenderer;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {
    @Shadow private int x;
    @Shadow private int y;
    @Shadow protected int width;
    @Shadow protected int height;

    @Overwrite
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean button = (Object) this instanceof Button;
        boolean imageButton = (Object) this instanceof ImageButton;

        AbstractWidget widget = (AbstractWidget) (Object) this;
        if (!button) {
            // Draw the native contents first, then frost the entire widget. This
            // keeps sliders, checkboxes, text fields and Sodium widgets visible
            // while giving every inherited AbstractWidget the glass surface.
            this.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }

        guiGraphics.flush();

        ReGlassConfig config = ReGlassConfig.INSTANCE;
        float hover = widget.isHovered() ? 1.0f : 0.0f;
        float enabled = widget.active ? 1.0f : 0.55f;
        float radius = Math.min(width, height) <= 24
                ? Math.min(width, height) * 0.5f
                : Math.min(width, height) * 0.38f;
        WidgetStyle style = WidgetStyle.create()
                .tint(config.defaultTintColor, config.defaultTintAlpha * enabled)
                .shadow(config.defaultShadowExpand + (height <= 24 ? 6.0f : 8.0f),
                        Math.min(1.0f, config.defaultShadowFactor + 0.10f),
                        config.defaultShadowOffsetX, config.defaultShadowOffsetY + 1.0f)
                .shadowColor(config.defaultShadowColor, config.defaultShadowColorAlpha)
                .refractionFactor(config.defaultRefFactor * (0.92f + 0.08f * hover))
                .fresnelFactor(config.defaultRefFresnelFactor)
                .glareFactor(config.defaultGlareFactor)
                .smoothing(Math.max(0.02f, config.defaultSmoothing));

        LiquidGlassRenderer.renderCapsule(
                guiGraphics, x, y, width, height, 0.0f, style, radius, hover, 0.0f
        );
        guiGraphics.flush();

        if (button) {
            if (imageButton) {
                // ImageButton.renderWidget() is the native Sprite/icon path. It
                // runs only after the glass, so the icon remains crisp without
                // bringing back a normal Button's gui.png background.
                this.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            } else if (widget.getMessage() != null && !widget.getMessage().getString().isEmpty()) {
                guiGraphics.drawCenteredString(
                        Minecraft.getInstance().font,
                        widget.getMessage(),
                        x + width / 2,
                        y + (height - 8) / 2,
                        widget.active ? 0xFFFFFFFF : 0xFF8D919A
                );
            }
        }
    }

    protected abstract void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);
}
