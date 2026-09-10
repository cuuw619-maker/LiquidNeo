package restudio.reglassneo.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import restudio.reglassneo.client.api.ReGlassConfig;
import restudio.reglassneo.client.api.WidgetStyle;
import restudio.reglassneo.client.render.LiquidGlassRenderer;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {
    @Overwrite
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!((Object) this instanceof Button button)) return;

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

        // Use the widget's own coordinates directly so the shader receives the exact
        // geometry Minecraft is rendering, without an intermediate offset or cache.
        LiquidGlassRenderer.renderCapsule(
                graphics,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                -1.0f,
                style
        );

        if (!button.getMessage().getString().isEmpty()) {
            graphics.drawCenteredString(
                    Minecraft.getInstance().font,
                    button.getMessage(),
                    this.getX() + this.getWidth() / 2,
                    this.getY() + (this.getHeight() - 8) / 2,
                    button.active ? 0xFFFFFFFF : 0xFF8D919A
            );
        }
    }
}
