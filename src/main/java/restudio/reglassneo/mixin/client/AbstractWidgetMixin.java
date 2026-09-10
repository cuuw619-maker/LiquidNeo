package restudio.reglassneo.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
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

    /**
     * Replaces AbstractWidget.render so Button.renderWidget() cannot submit
     * the vanilla gui.png button sprite after our glass has been drawn.
     * Non-button widgets retain their normal renderWidget() path.
     */
    @Overwrite
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if ((Object) this instanceof Button button) {
            guiGraphics.flush();

            ReGlassConfig config = ReGlassConfig.INSTANCE;
            float hover = button.isHovered() ? 1.0f : 0.0f;
            float enabled = button.active ? 1.0f : 0.55f;
            WidgetStyle style = WidgetStyle.create()
                    .tint(config.defaultTintColor, config.defaultTintAlpha * enabled)
                    .shadow(config.defaultShadowExpand + 8.0f,
                            Math.min(1.0f, config.defaultShadowFactor + 0.10f),
                            config.defaultShadowOffsetX, config.defaultShadowOffsetY + 1.0f)
                    .shadowColor(config.defaultShadowColor, config.defaultShadowColorAlpha)
                    .refractionFactor(config.defaultRefFactor * (0.92f + 0.08f * hover))
                    .fresnelFactor(config.defaultRefFresnelFactor)
                    .glareFactor(config.defaultGlareFactor)
                    .smoothing(Math.max(0.02f, config.defaultSmoothing));

            LiquidGlassRenderer.renderCapsule(
                    guiGraphics,
                    x,
                    y,
                    width,
                    height,
                    0.0f,
                    style
            );

            guiGraphics.flush();

            if (!button.getMessage().getString().isEmpty()) {
                guiGraphics.drawCenteredString(
                        Minecraft.getInstance().font,
                        button.getMessage(),
                        x + width / 2,
                        y + (height - 8) / 2,
                        button.active ? 0xFFFFFFFF : 0xFF8D919A
                );
            }
            return;
        }

        // Do not recurse through render(). This invokes the widget-specific
        // implementation directly and preserves vanilla behavior for all
        // non-Button widgets.
        this.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    /** Implemented by AbstractWidget and overridden by concrete widgets. */
    protected abstract void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);
}
