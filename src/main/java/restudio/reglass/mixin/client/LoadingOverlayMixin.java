package restudio.reglass.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import restudio.reglass.client.api.WidgetStyle;
import restudio.reglass.client.render.LiquidGlassRenderer;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {
    @Shadow private float currentProgress;

    @Inject(method="render", at=@At("HEAD"), cancellable=true)
    private void reglass$renderLiquidGlassProgress(GuiGraphics graphics,int mouseX,int mouseY,float partialTick,CallbackInfo ci){
        int width=graphics.guiWidth(), height=graphics.guiHeight();
        graphics.fill(0,0,width,height,0xFF101217);

        int barWidth=Math.min(360,Math.max(220,width-80));
        int barHeight=18;
        int x=(width-barWidth)/2;
        int y=height/2+42;
        float progress=Math.max(0f,Math.min(1f,currentProgress));

        WidgetStyle style=WidgetStyle.create()
                .tint(0xFFFFFF,.10f)
                .blurRadius(8)
                .shadow(26f,.28f,0f,3f)
                .shadowColor(0x000000,.9f)
                .smoothing(.05f);
        LiquidGlassRenderer.renderCapsule(graphics,x,y,barWidth,barHeight,progress,style);
        graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font,
                "ReGlass",width/2,y-28,0xFFFFFFFF);
        graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font,
                Math.round(progress*100f)+"%",width/2,y+28,0xFFD9DCE4);
        ci.cancel();
    }
}
