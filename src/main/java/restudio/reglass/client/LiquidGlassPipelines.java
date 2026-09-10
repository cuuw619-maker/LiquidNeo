package restudio.reglass.client;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

public final class LiquidGlassPipelines {
    private static ShaderInstance copyShader;
    private static ShaderInstance blurShader;
    private static ShaderInstance glassShader;

    private LiquidGlassPipelines() {
    }

    public static void registerShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(ReGlass.MOD_ID, "copy"), DefaultVertexFormat.POSITION_TEX),
                    shader -> copyShader = shader);
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(ReGlass.MOD_ID, "blur"), DefaultVertexFormat.POSITION_TEX),
                    shader -> blurShader = shader);
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(ReGlass.MOD_ID, "liquid_glass"), DefaultVertexFormat.POSITION_TEX),
                    shader -> glassShader = shader);
            ReGlass.LOGGER.info("Liquid Glass shaders registered for Minecraft 1.21.1");
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load ReGlass core shaders", exception);
        }
    }

    public static ShaderInstance copyShader() {
        return copyShader;
    }

    public static ShaderInstance blurShader() {
        return blurShader;
    }

    public static ShaderInstance glassShader() {
        return glassShader;
    }

    public static boolean ready() {
        return copyShader != null && blurShader != null && glassShader != null;
    }
}
