package restudio.reglass.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.neoforged.fml.loading.FMLPaths;
import restudio.reglass.ReGlass;
import restudio.reglass.client.api.ReGlassConfig;

public final class ReGlassSettingsIO {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private ReGlassSettingsIO() {}

    private static Path configPath() { return FMLPaths.CONFIGDIR.get().resolve("reglass.json"); }

    public static void loadIntoMemory() {
        try {
            Path path = configPath();
            if (!Files.exists(path)) { saveFromMemory(); return; }
            try (Reader reader = Files.newBufferedReader(path)) {
                Data data = GSON.fromJson(reader, Data.class);
                if (data != null) apply(data);
            }
        } catch (Exception exception) {
            ReGlass.LOGGER.error("Failed to load ReGlass configuration", exception);
        }
    }

    public static void saveFromMemory() {
        try {
            Path path = configPath();
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) { GSON.toJson(snapshot(), writer); }
        } catch (Exception exception) {
            ReGlass.LOGGER.error("Failed to save ReGlass configuration", exception);
        }
    }

    private static Data snapshot() {
        ReGlassConfig c = ReGlassConfig.INSTANCE;
        Data d = new Data();
        d.enableRedesign=c.features.enableRedesign; d.buttons=c.features.buttons; d.sliders=c.features.sliders; d.hotbar=c.features.hotbar;
        d.cancelScreenDarkening=c.features.cancelScreenDarkening; d.pixelatedGrid=c.features.pixelatedGrid;
        d.tintColor=c.defaultTintColor; d.tintAlpha=c.defaultTintAlpha; d.smoothing=c.defaultSmoothing; d.blurRadius=c.defaultBlurRadius;
        d.shadowExpand=c.defaultShadowExpand; d.shadowFactor=c.defaultShadowFactor; d.shadowOffsetX=c.defaultShadowOffsetX; d.shadowOffsetY=c.defaultShadowOffsetY;
        d.shadowColor=c.defaultShadowColor; d.shadowColorAlpha=c.defaultShadowColorAlpha;
        d.refThickness=c.defaultRefThickness; d.refFactor=c.defaultRefFactor; d.refDispersion=c.defaultRefDispersion; d.refFresnelRange=c.defaultRefFresnelRange;
        d.refFresnelHardness=c.defaultRefFresnelHardness; d.refFresnelFactor=c.defaultRefFresnelFactor;
        d.glareRange=c.defaultGlareRange; d.glareHardness=c.defaultGlareHardness; d.glareConvergence=c.defaultGlareConvergence;
        d.glareOppositeFactor=c.defaultGlareOppositeFactor; d.glareFactor=c.defaultGlareFactor; d.glareAngleRad=c.defaultGlareAngleRad;
        d.pixelatedGridSize=c.pixelatedGridSize; d.hoverScalePx=c.hoverScalePx; d.focusScalePx=c.focusScalePx; d.focusBorderWidthPx=c.focusBorderWidthPx;
        d.focusBorderIntensity=c.focusBorderIntensity; d.focusBorderSpeed=c.focusBorderSpeed;
        return d;
    }

    public static void apply(Data d) {
        ReGlassConfig c=ReGlassConfig.INSTANCE;
        c.features.enableRedesign=d.enableRedesign; c.features.buttons=d.buttons; c.features.sliders=d.sliders; c.features.hotbar=d.hotbar;
        c.features.cancelScreenDarkening=d.cancelScreenDarkening; c.features.pixelatedGrid=d.pixelatedGrid;
        c.defaultTintColor=d.tintColor; c.defaultTintAlpha=d.tintAlpha; c.defaultSmoothing=d.smoothing; c.defaultBlurRadius=d.blurRadius;
        c.defaultShadowExpand=d.shadowExpand; c.defaultShadowFactor=d.shadowFactor; c.defaultShadowOffsetX=d.shadowOffsetX; c.defaultShadowOffsetY=d.shadowOffsetY;
        c.defaultShadowColor=d.shadowColor; c.defaultShadowColorAlpha=d.shadowColorAlpha;
        c.defaultRefThickness=d.refThickness; c.defaultRefFactor=d.refFactor; c.defaultRefDispersion=d.refDispersion; c.defaultRefFresnelRange=d.refFresnelRange;
        c.defaultRefFresnelHardness=d.refFresnelHardness; c.defaultRefFresnelFactor=d.refFresnelFactor;
        c.defaultGlareRange=d.glareRange; c.defaultGlareHardness=d.glareHardness; c.defaultGlareConvergence=d.glareConvergence;
        c.defaultGlareOppositeFactor=d.glareOppositeFactor; c.defaultGlareFactor=d.glareFactor; c.defaultGlareAngleRad=d.glareAngleRad;
        c.pixelatedGridSize=d.pixelatedGridSize; c.hoverScalePx=d.hoverScalePx; c.focusScalePx=d.focusScalePx; c.focusBorderWidthPx=d.focusBorderWidthPx;
        c.focusBorderIntensity=d.focusBorderIntensity; c.focusBorderSpeed=d.focusBorderSpeed;
    }

    public static final class Data {
        public boolean enableRedesign=true, buttons=true, sliders=true, hotbar=true, cancelScreenDarkening=true, pixelatedGrid=false;
        public int tintColor=0x000000, blurRadius=12, shadowColor=0x000000;
        public float tintAlpha=0f, smoothing=.003f, shadowExpand=30f, shadowFactor=.25f, shadowOffsetX=0f, shadowOffsetY=2f, shadowColorAlpha=1f;
        public float refThickness=20f, refFactor=1.4f, refDispersion=7f, refFresnelRange=30f, refFresnelHardness=20f, refFresnelFactor=20f;
        public float glareRange=30f, glareHardness=20f, glareConvergence=50f, glareOppositeFactor=80f, glareFactor=90f, glareAngleRad=(float)(-45*Math.PI/180);
        public float pixelatedGridSize=8f, hoverScalePx=1.5f, focusScalePx=2.5f, focusBorderWidthPx=2f, focusBorderIntensity=.75f, focusBorderSpeed=1.6f;
    }
}
