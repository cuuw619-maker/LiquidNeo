package restudio.reglass.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.WidgetStyle;
import restudio.reglass.client.render.LiquidGlassRenderer;

/**
 * Replaces vanilla Button rendering at the widget boundary.
 * Input, focus and narration remain vanilla; only the visual pass is replaced.
 */
@Mixin(Button.class)
public abstract class ButtonMixin {
    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void reglass$renderLiquidGlass(GuiGraphics graphics, int mouseX, int mouseY,
                                           float partialTick, CallbackInfo ci) {
        Button button = (Button) (Object) this;
        ReGlassConfig config = ReGlassConfig.INSTANCE;

        if (!config.features.enableRedesign || !config.features.buttons) {
            return;
        }

        float hover = button.isHovered() ? 1.0f : 0.0f;
        float enabled = button.active ? 1.0f : 0.55f;
        float pulse = 0.92f + hover * 0.08f;

        WidgetStyle style = WidgetStyle.create()
                .tint(config.defaultTintColor, config.defaultTintAlpha * enabled)
                .blurRadius(Math.max(12, config.defaultBlurRadius + 8))
                .shadow(config.defaultShadowExpand + 8.0f,
                        Math.min(1.0f, config.defaultShadowFactor + 0.10f),
                        config.defaultShadowOffsetX,
                        config.defaultShadowOffsetY + 1.0f)
                .shadowColor(config.defaultShadowColor, config.defaultShadowColorAlpha)
                .refractionThickness(config.defaultRefThickness)
                .refractionFactor(config.defaultRefFactor * pulse)
                .refractionDispersion(config.defaultRefDispersion)
                .fresnelRange(config.defaultRefFresnelRange)
                .fresnelHardness(config.defaultRefFresnelHardness)
                .fresnelFactor(config.defaultRefFresnelFactor * pulse)
                .glareRange(config.defaultGlareRange)
                .glareHardness(config.defaultGlareHardness)
                .glareFactor(config.defaultGlareFactor * pulse)
                .glareAngleRad(config.defaultGlareAngleRad)
                .smoothing(Math.max(0.02f, config.defaultSmoothing));

        LiquidGlassRenderer.renderCapsule(
                graphics,
                button.getX(),
                button.getY(),
                button.getWidth(),
                button.getHeight(),
                0.0f,
                style
        );

        Component message = button.getMessage();
        if (!message.getString().isEmpty()) {
            int textColor = button.active ? 0xFFFFFFFF : 0xFF8D919A;
            graphics.drawCenteredString(
                    net.minecraft.client.Minecraft.getInstance().font,
                    message,
                    button.getX() + button.getWidth() / 2,
                    button.getY() + (button.getHeight() - 8) / 2,
                    textColor
            );
        }

        ci.cancel();
    }
}
