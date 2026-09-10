package restudio.reglass.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import restudio.reglass.client.LiquidGlassPipelines;
import restudio.reglass.client.api.WidgetStyle;
import restudio.reglass.client.render.LiquidGlassRenderer;

/** Replaces the vanilla LoadingOverlay progress bar with the Liquid Glass capsule. */
@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {
    @Shadow private float currentProgress;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void reglass$renderLiquidGlassProgress(GuiGraphics graphics, int mouseX, int mouseY,
                                                    float partialTick, CallbackInfo ci) {
        if (!LiquidGlassPipelines.ready()) {
            // Do not leave the loading screen blank if the game has not finished registering shaders yet.
            return;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        float progress = Math.max(0.0f, Math.min(1.0f, currentProgress));

        graphics.fill(0, 0, width, height, 0xFF101217);

        int barWidth = Math.min(420, Math.max(240, width - 96));
        int barHeight = 20;
        int x = (width - barWidth) / 2;
        int y = height / 2 + 42;

        WidgetStyle style = WidgetStyle.create()
                .tint(0xFFFFFF, 0.12f)
                .blurRadius(24)
                .shadow(34.0f, 0.34f, 0.0f, 4.0f)
                .shadowColor(0x000000, 0.92f)
                .refractionThickness(22.0f)
                .refractionFactor(1.55f)
                .refractionDispersion(8.0f)
                .fresnelRange(34.0f)
                .fresnelHardness(18.0f)
                .fresnelFactor(22.0f)
                .glareRange(34.0f)
                .glareHardness(18.0f)
                .glareFactor(95.0f)
                .glareAngleRad((float) (-45.0 * Math.PI / 180.0))
                .smoothing(0.05f);

        // The capsule renderer owns the entire bar: no vanilla fill/progress geometry is drawn.
        LiquidGlassRenderer.renderCapsule(graphics, x, y, barWidth, barHeight, progress, style);

        graphics.drawCenteredString(
                net.minecraft.client.Minecraft.getInstance().font,
                "ReGlass",
                width / 2, y - 30, 0xFFFFFFFF
        );
        graphics.drawCenteredString(
                net.minecraft.client.Minecraft.getInstance().font,
                Math.round(progress * 100.0f) + "%",
                width / 2, y + 30, 0xFFD9DCE4
        );

        ci.cancel();
    }
}
