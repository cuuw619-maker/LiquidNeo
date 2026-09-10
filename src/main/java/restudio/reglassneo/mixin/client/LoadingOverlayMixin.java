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

    /**
     * Replaces the vanilla loading bar with the same SDF/frosted-glass capsule
     * used by widgets. The vanilla white progress rectangle is never submitted.
     */
    @Overwrite
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        float progress = Math.max(0.0f, Math.min(1.0f, currentProgress));

        graphics.fill(0, 0, width, height, 0xFFE23845);

        int barWidth = Math.min(420, Math.max(240, width - 96));
        int barHeight = Math.max(18, Math.min(24, height / 24));
        int x = (width - barWidth) / 2;
        int y = height / 2 + 42;

        WidgetStyle style = WidgetStyle.create()
                .tint(0xFFFFFF, 0.16f)
                .shadow(34.0f, 0.38f, 0.0f, 5.0f)
                .shadowColor(0x120A0D, 0.90f)
                .refractionFactor(1.65f)
                .fresnelFactor(24.0f)
                .glareFactor(90.0f)
                .smoothing(0.045f);

        LiquidGlassRenderer.renderCapsule(
                graphics,
                x, y, barWidth, barHeight,
                progress, style, barHeight * 0.5f, 0.0f, 0.0f
        );

        graphics.flush();
        graphics.drawCenteredString(
                Minecraft.getInstance().font,
                "ReGlassNeo",
                width / 2,
                y - 30,
                0xFFFFFFFF
        );
        graphics.drawCenteredString(
                Minecraft.getInstance().font,
                Math.round(progress * 100.0f) + "%",
                width / 2,
                y + 30,
                0xFFF2F3F7
        );
    }
}
