package restudio.reglassneo.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import restudio.reglassneo.client.api.WidgetStyle;
import restudio.reglassneo.client.render.LiquidGlassRenderer;

/**
 * Inventory-only background injection. It runs after the vanilla inventory
 * background but before slots and labels, so the container contents remain
 * completely vanilla and are never re-rendered by the glass layer.
 */
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @Shadow protected int leftPos;
    @Shadow protected int topPos;

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void reglassneo$renderGlassBackground(GuiGraphics graphics, float partialTick,
                                                   int mouseX, int mouseY, CallbackInfo ci) {
        WidgetStyle style = WidgetStyle.create()
                .tint(0xFFFFFF, 0.13f)
                .shadow(26.0f, 0.30f, 0.0f, 4.0f)
                .shadowColor(0x101217, 0.78f)
                .refractionFactor(1.45f)
                .fresnelFactor(22.0f)
                .glareFactor(72.0f)
                .smoothing(0.045f);

        LiquidGlassRenderer.renderCapsule(
                graphics,
                leftPos - 3,
                topPos - 3,
                182,
                172,
                0.0f,
                style,
                18.0f,
                0.0f,
                0.0f
        );
    }
}
