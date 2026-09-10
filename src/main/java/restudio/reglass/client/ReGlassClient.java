package restudio.reglass.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import restudio.reglass.ReGlass;
import restudio.reglass.client.config.ReGlassSettingsIO;

public final class ReGlassClient {
    private static final String KEY_CATEGORY = "key.categories.reglass";

    private static KeyMapping playgroundKey;
    private static KeyMapping configKey;

    public static Minecraft minecraftClient;

    private ReGlassClient() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ReGlassClient::onClientSetup);
        modEventBus.addListener(ReGlassClient::registerKeyMappings);
        NeoForge.EVENT_BUS.addListener(ReGlassClient::onClientTick);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            minecraftClient = Minecraft.getInstance();
            ReGlassSettingsIO.loadIntoMemory();
            ReGlass.LOGGER.info("ReGlass client initialized");
        });
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        playgroundKey = new KeyMapping(
                "key.reglass.playground",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KEY_CATEGORY
        );
        configKey = new KeyMapping(
                "key.reglass.config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KEY_CATEGORY
        );

        event.register(playgroundKey);
        event.register(configKey);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();

        if (configKey != null && configKey.consumeClick()) {
            // The NeoForge screen implementation will be connected here when
            // ReGlassConfigScreen is ported from Fabric to Minecraft 1.21.1.
            ReGlass.LOGGER.debug("ReGlass config key pressed");
        }

        if (playgroundKey != null && playgroundKey.consumeClick()) {
            // The playground screen will be connected here when the widget/UI
            // layer is ported. Keeping the input hook here mirrors the original
            // Fabric client tick behavior without introducing Fabric APIs.
            ReGlass.LOGGER.debug("ReGlass playground key pressed");
        }
    }
}
