package restudio.reglassneo.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import restudio.reglassneo.ReGlass;
import restudio.reglassneo.client.config.ReGlassSettingsIO;
import restudio.reglassneo.client.render.LiquidGlassRenderer;
import restudio.reglassneo.client.runtime.ReGlassAnim;
import restudio.reglassneo.client.screen.PlaygroundScreen;
import restudio.reglassneo.client.screen.config.ReGlassConfigScreen;

public final class ReGlassClient {
    private static final String KEY_CATEGORY="key.categories.reglassneo";
    private static KeyMapping playgroundKey,configKey;
    public static Minecraft minecraftClient;
    private ReGlassClient(){}
    public static void register(IEventBus bus){bus.addListener(ReGlassClient::onClientSetup);bus.addListener(ReGlassClient::registerKeyMappings);bus.addListener(ReGlassClient::registerShaders);NeoForge.EVENT_BUS.addListener(ReGlassClient::onClientTick);}
    private static void onClientSetup(FMLClientSetupEvent event){event.enqueueWork(()->{minecraftClient=Minecraft.getInstance();ReGlassSettingsIO.loadIntoMemory();ReGlassAnim.INSTANCE.update(restudio.reglassneo.client.api.ReGlassConfig.INSTANCE,0.0);ReGlass.LOGGER.info("ReGlassNeo client initialized on {}",FMLEnvironment.dist);});}
    private static void registerKeyMappings(RegisterKeyMappingsEvent event){playgroundKey=new KeyMapping("key.reglassneo.playground",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_H,KEY_CATEGORY);configKey=new KeyMapping("key.reglassneo.config",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_G,KEY_CATEGORY);event.register(playgroundKey);event.register(configKey);}
    private static void registerShaders(RegisterShadersEvent event){LiquidGlassRenderer.registerShaders(event);}
    private static void onClientTick(ClientTickEvent.Post event){Minecraft client=Minecraft.getInstance();ReGlassAnim.INSTANCE.update(restudio.reglassneo.client.api.ReGlassConfig.INSTANCE,1.0/Math.max(20.0,client.getFps()));LiquidGlassRenderer.beginFrame();if(configKey!=null&&configKey.consumeClick())client.setScreen(new ReGlassConfigScreen(client.screen));if(playgroundKey!=null&&playgroundKey.consumeClick())client.setScreen(new PlaygroundScreen());}
}
