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
     * Every AbstractWidget gets the SDF glass surface. Button is only checked to
     * suppress its vanilla gui.png background; it is not a rendering restriction.
     */
    @Overwrite
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        AbstractWidget widget = (AbstractWidget) (Object) this;
        boolean nativeButton = widget instanceof Button;
        boolean iconWidget = widget instanceof ImageButton;

        // Render non-Button contents before capturing the background so sliders,
        // text fields, Sodium controls and custom widgets remain visible through glass.
        if (!nativeButton && !iconWidget) {
            this.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }

        guiGraphics.flush();

        ReGlassConfig config = ReGlassConfig.INSTANCE;
        ReGlassAnim anim = ReGlassAnim.INSTANCE;
        float hover = widget.isHovered() ? 1.0f : 0.0f;
        float enabled = widget.active ? 1.0f : 0.55f;
        float radius = Math.min(width, height) <= 24
                ? Math.min(width, height) * 0.5f
                : Math.min(width, height) * 0.38f;
        float tintAlpha = anim.tintAlpha() * enabled;
        WidgetStyle style = WidgetStyle.create()
                .tint(config.defaultTintColor, tintAlpha)
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

        // Icon widgets are deliberately submitted after the capsule. This keeps
        // language/accessibility and other Sprite/ImageButton icons crisp and visible.
        if (iconWidget) {
            this.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        } else if (nativeButton && widget.getMessage() != null && !widget.getMessage().getString().isEmpty()) {
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
