package restudio.reglass.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import restudio.reglass.client.LiquidGlassWidget;
import restudio.reglass.client.api.WidgetStyle;

public final class PlaygroundScreen extends Screen {
    private boolean showBackground=true;
    public PlaygroundScreen(){super(Component.literal("ReGlass Playground"));}

    @Override protected void init(){
        WidgetStyle style=WidgetStyle.create().tint(0xFFFFFF,.12f).blurRadius(8).shadow(30f,.25f,0f,3f).shadowColor(0x000000,.8f).smoothing(.05f);
        addRenderableWidget(new LiquidGlassWidget(width/2-75,height/2-25,150,50,style).setMoveable(true));
        addRenderableWidget(Button.builder(Component.literal("Toggle background"),b->showBackground=!showBackground).bounds(10,10,130,20).build());
    }
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float delta){
        if(showBackground)renderBackground(graphics,mouseX,mouseY,delta);
        else graphics.fill(0,0,width,height,0xFF101318);
        graphics.drawCenteredString(font,Component.literal("ReGlass Playground"),width/2,20,0xFFFFFFFF);
        super.render(graphics,mouseX,mouseY,delta);
    }
}
