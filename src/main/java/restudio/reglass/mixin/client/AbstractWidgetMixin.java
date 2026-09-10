package restudio.reglass.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.WidgetStyle;
import restudio.reglass.client.render.LiquidGlassRenderer;

/** Replaces vanilla button rendering with the hardware-compatible Liquid Glass renderer. */
@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {
    /**
     * @author ReGlass
     * @reason Replace the vanilla widget render pass for buttons.
     */
    @Overwrite
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!((Object) this instanceof Button button)) return;

        ReGlassConfig config = ReGlassConfig.INSTANCE;
        float hover = button.isHovered() ? 1.0f : 0.0f;
        float enabled = button.active ? 1.0f : 0.55f;
        float pulse = 0.92f + hover * 0.08f;

        WidgetStyle style = WidgetStyle.create()
                .tint(config.defaultTintColor, config.defaultTintAlpha * enabled)
                .shadow(config.defaultShadowExpand + 8.0f,
                        Math.min(1.0f, config.defaultShadowFactor + 0.10f),
                        config.defaultShadowOffsetX,
                        config.defaultShadowOffsetY + 1.0f)
                .shadowColor(config.defaultShadowColor, config.defaultShadowColorAlpha)
                .refractionFactor(config.defaultRefFactor * pulse)
                .fresnelFactor(config.defaultRefFresnelFactor * pulse)
                .glareFactor(config.defaultGlareFactor * pulse)
                .smoothing(Math.max(0.02f, config.defaultSmoothing));

        LiquidGlassRenderer.renderCapsule(guiGraphics,
                button.getX(), button.getY(), button.getWidth(), button.getHeight(), 0.0f, style);

        if (!button.getMessage().getString().isEmpty()) {
            int textColor = button.active ? 0xFFFFFFFF : 0xFF8D919A;
            guiGraphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font,
                    button.getMessage(), button.getX() + button.getWidth() / 2,
                    button.getY() + (button.getHeight() - 8) / 2, textColor);
        }
    }
}
