package restudio.reglass.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import restudio.reglass.client.api.WidgetStyle;
import restudio.reglass.client.render.LiquidGlassRenderer;

/** Replaces the vanilla loading screen with the hardware-compatible glass progress UI. */
@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {
    @Shadow private float currentProgress;

    /**
     * @author ReGlass
     * @reason Replace vanilla loading rendering with the mobile-safe capsule renderer.
     */
    @Overwrite
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
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
                .shadow(34.0f, 0.34f, 0.0f, 4.0f)
                .shadowColor(0x000000, 0.92f)
                .smoothing(0.05f);

        LiquidGlassRenderer.renderCapsule(graphics, x, y, barWidth, barHeight, progress, style);

        graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font,
                "ReGlass", width / 2, y - 30, 0xFFFFFFFF);
        graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font,
                Math.round(progress * 100.0f) + "%", width / 2, y + 30, 0xFFD9DCE4);
    }
}
