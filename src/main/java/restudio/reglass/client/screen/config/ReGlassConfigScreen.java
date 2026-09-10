package restudio.reglass.client.screen.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.api.WidgetStyle;
import restudio.reglass.client.config.ReGlassSettingsIO;
import restudio.reglass.client.render.LiquidGlassRenderer;

public final class ReGlassConfigScreen extends Screen {
    private final Screen parent;
    private final ReGlassConfig config=ReGlassConfig.INSTANCE;
    public ReGlassConfigScreen(Screen parent){super(Component.literal("ReGlass Configuration"));this.parent=parent;}

    @Override protected void init(){
        int cx=width/2, y=48;
        addRenderableWidget(Button.builder(label("Liquid Glass",config.features.enableRedesign),b->{config.features.enableRedesign=!config.features.enableRedesign;b.setMessage(label("Liquid Glass",config.features.enableRedesign));}).bounds(cx-155,y,150,20).build());
        addRenderableWidget(Button.builder(label("Buttons",config.features.buttons),b->{config.features.buttons=!config.features.buttons;b.setMessage(label("Buttons",config.features.buttons));}).bounds(cx+5,y,150,20).build()); y+=28;
        addRenderableWidget(slider(cx-155,y,310,"Blur Radius",0,32,config.defaultBlurRadius,v->config.defaultBlurRadius=Math.round(v))); y+=28;
        addRenderableWidget(slider(cx-155,y,310,"Tint Alpha",0,1,config.defaultTintAlpha,v->config.defaultTintAlpha=v)); y+=28;
        addRenderableWidget(slider(cx-155,y,310,"Shadow Factor",0,1,config.defaultShadowFactor,v->config.defaultShadowFactor=v)); y+=28;
        addRenderableWidget(slider(cx-155,y,310,"Shadow Expand",0,100,config.defaultShadowExpand,v->config.defaultShadowExpand=v)); y+=28;
        addRenderableWidget(slider(cx-155,y,310,"Refraction Factor",1,2.5f,config.defaultRefFactor,v->config.defaultRefFactor=v)); y+=28;
        addRenderableWidget(slider(cx-155,y,310,"Glare Factor",0,100,config.defaultGlareFactor,v->config.defaultGlareFactor=v)); y+=32;
        addRenderableWidget(Button.builder(Component.literal("Reset"),b->{ReGlassSettingsIO.apply(new ReGlassSettingsIO.Data());rebuildWidgets();}).bounds(cx-155,height-35,100,20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"),b->close()).bounds(cx+55,height-35,100,20).build());
    }

    private void rebuildWidgets(){clearWidgets();init();}
    private Button.OnPress unused(){return b->{};}
    private AbstractSliderButton slider(int x,int y,int w,String name,float min,float max,float value,java.util.function.Consumer<Float> setter){
        return new AbstractSliderButton(x,y,w,20,(Component.literal(name)),(value-min)/(max-min)){
            @Override protected void updateMessage(){setMessage(Component.literal(name+": "+format(min+value*(max-min))));}
            @Override protected void applyValue(){setter.accept(min+(float)value*(max-min));}
        };
    }
    private static String format(float v){return String.format(java.util.Locale.ROOT,"%.2f",v);}
    private static Component label(String n,boolean v){return Component.literal(n+": "+(v?"ON":"OFF"));}

    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float delta){
        renderBackground(graphics,mouseX,mouseY,delta);
        super.render(graphics,mouseX,mouseY,delta);
        graphics.drawCenteredString(font,title,width/2,18,0xFFFFFFFF);
        WidgetStyle preview=WidgetStyle.create().tint(0xFFFFFF,config.defaultTintAlpha).blurRadius(config.defaultBlurRadius)
                .shadow(config.defaultShadowExpand,config.defaultShadowFactor,config.defaultShadowOffsetX,config.defaultShadowOffsetY)
                .shadowColor(config.defaultShadowColor,config.defaultShadowColorAlpha);
        LiquidGlassRenderer.render(graphics,width/2-70,height/2+65,140,54,27,preview,.4f,.2f,Component.literal("Preview"));
    }
    @Override public void onClose(){close();}
    @Override public void close(){ReGlassSettingsIO.saveFromMemory();if(minecraft!=null)minecraft.setScreen(parent);}
}
