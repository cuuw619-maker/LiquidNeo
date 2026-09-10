package restudio.reglass.client;

import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import restudio.reglass.ReGlass;

/**
 * Legacy shader integration kept as an API compatibility surface.
 * The active renderer is deliberately shader-free for mobile OpenGL compatibility.
 */
public final class LiquidGlassPipelines {
    private LiquidGlassPipelines() {}

    public static void registerShaders(RegisterShadersEvent event) {
        ReGlass.LOGGER.info("ReGlass mobile-safe hardware renderer active; custom core shaders disabled");
    }

    public static boolean ready() {
        return true;
    }
}
