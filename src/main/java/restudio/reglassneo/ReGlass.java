package restudio.reglassneo;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("reglassneo")
public class ReGlass {
    public static final String MOD_ID="reglassneo";
    public static final Logger LOGGER=LoggerFactory.getLogger(MOD_ID);
    public ReGlass(IEventBus modEventBus){LOGGER.info("Initializing ReGlassNeo {} on {}",MOD_ID,FMLEnvironment.dist);if(FMLEnvironment.dist==Dist.CLIENT)restudio.reglassneo.client.ReGlassClient.register(modEventBus);}
}
