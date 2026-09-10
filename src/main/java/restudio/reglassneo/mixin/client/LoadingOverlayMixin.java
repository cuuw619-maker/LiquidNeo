package restudio.reglassneo.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import restudio.reglassneo.client.api.WidgetStyle;
import restudio.reglassneo.client.render.LiquidGlassRenderer;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {
    @Shadow private float currentProgress;
    private float reglassneo$animatedProgress;

    /** Fully replaces Mojang's rectangular progress bar with a volumetric SDF capsule. */
    @Overwrite
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        float target = Math.max(0.0f, Math.min(1.0f, currentProgress));
        if (reglassneo$animatedProgress <= 0.0f) {
            reglassneo$animatedProgress = target;
        } else {
            float smoothing = 1.0f - (float) Math.exp(-Math.max(0.0f, partialTick + 0.05f) * 8.0f);
            reglassneo$animatedProgress += (target - reglassneo$animatedProgress) * smoothing;
        }

        // Keep Mojang's loading-screen backdrop, but never submit its white bar.
        graphics.fill(0, 0, width, height, 0xFFE23845);

        int barWidth = Math.min(520, Math.max(260, width - 80));
        int barHeight = Math.max(20, Math.min(28, height / 22));
        int x = (width - barWidth) / 2;
        int y = height / 2 + 42;

        WidgetStyle style = WidgetStyle.create()
                .tint(0xFFFFFF, 0.18f)
                .shadow(36.0f, 0.42f, 0.0f, 6.0f)
                .shadowColor(0x120A0D, 0.92f)
                .refractionFactor(1.75f)
                .fresnelFactor(26.0f)
                .glareFactor(96.0f)
                .smoothing(0.04f);

        LiquidGlassRenderer.renderCapsule(
                graphics,
                x, y, barWidth, barHeight,
                reglassneo$animatedProgress, style, barHeight * 0.5f, 0.0f, 0.0f
        );

        graphics.flush();
        graphics.drawCenteredString(
                Minecraft.getInstance().font,
                "ReGlassNeo",
                width / 2,
                y - 32,
                0xFFFFFFFF
        );
        graphics.drawCenteredString(
                Minecraft.getInstance().font,
                Math.round(reglassneo$animatedProgress * 100.0f) + "%",
                width / 2,
                y + 34,
                0xFFF2F3F7
        );
    }
}
