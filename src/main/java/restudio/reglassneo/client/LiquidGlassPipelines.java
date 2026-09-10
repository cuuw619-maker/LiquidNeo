package restudio.reglassneo.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import restudio.reglassneo.ReGlass;
import java.io.IOException;

public final class LiquidGlassPipelines {
    private static ShaderInstance glassShader;
    private LiquidGlassPipelines(){}
    public static void registerShaders(RegisterShadersEvent event){try{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.fromNamespaceAndPath(ReGlass.MOD_ID,"liquid_glass"),DefaultVertexFormat.POSITION_TEX),shader->glassShader=shader);ReGlass.LOGGER.info("ReGlassNeo mobile SDF glass shader registered");}catch(IOException e){throw new IllegalStateException("Unable to load ReGlassNeo liquid glass shader",e);}}
    public static ShaderInstance glassShader(){return glassShader;}
    public static boolean ready(){return glassShader!=null;}
}
