package restudio.reglassneo.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import restudio.reglassneo.client.api.ReGlassConfig;
import restudio.reglassneo.client.api.WidgetStyle;
import restudio.reglassneo.client.render.LiquidGlassRenderer;
import restudio.reglassneo.client.runtime.ReGlassAnim;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {
    @Shadow private int x;
    @Shadow private int y;
    @Shadow protected int width;
    @Shadow protected int height;

    @Shadow
    protected abstract void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);

    /**
     * Replaces only the widget surface stage. Content remains vanilla: custom
     * widgets are rendered before the translucent glass, while buttons render
     * their text through AbstractButton.renderString after the surface.
     */
    @Overwrite
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        AbstractWidget widget = (AbstractWidget) (Object) this;
        boolean button = widget instanceof AbstractButton;
        boolean iconWidget = widget instanceof ImageButton;

        if (!button) {
            this.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
        guiGraphics.flush();

        ReGlassConfig config = ReGlassConfig.INSTANCE;
        ReGlassAnim anim = ReGlassAnim.INSTANCE;
        float hover = widget.isHovered() ? 1.0f : 0.0f;
        float enabled = widget.active ? 1.0f : 0.55f;
        float min = Math.min(width, height);
        float radius = min <= 24 ? min * 0.5f : min * 0.38f;

        WidgetStyle style = WidgetStyle.create()
                .tint(config.defaultTintColor, anim.tintAlpha() * enabled)
                .shadow(config.defaultShadowExpand + (height <= 24 ? 6.0f : 8.0f),
                        Math.min(1.0f, config.defaultShadowFactor + 0.10f),
                        config.defaultShadowOffsetX, config.defaultShadowOffsetY + 1.0f)
                .shadowColor(config.defaultShadowColor, config.defaultShadowColorAlpha)
                .refractionFactor(config.defaultRefFactor * (0.92f + 0.08f * hover))
                .fresnelFactor(config.defaultRefFresnelFactor)
                .glareFactor(config.defaultGlareFactor)
                .smoothing(Math.max(0.02f, config.defaultSmoothing));

        LiquidGlassRenderer.renderCapsule(guiGraphics, x, y, width, height,
                0.0f, style, radius, hover, 0.0f);
        guiGraphics.flush();

        if (iconWidget) {
            // ImageButton.renderWidget is icon-only in 1.21.1, so the sprite is
            // intentionally submitted after glass and never gets covered by it.
            this.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        } else if (button) {
            ((AbstractButton) widget).renderString(
                    guiGraphics,
                    Minecraft.getInstance().font,
                    widget.active ? 0xFFFFFFFF : 0xFF8D919A
            );
        }
    }
}
