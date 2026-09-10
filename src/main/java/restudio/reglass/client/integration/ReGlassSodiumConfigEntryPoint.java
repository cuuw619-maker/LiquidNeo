package restudio.reglass.client.integration;

import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import restudio.reglass.ReGlass;
import restudio.reglass.client.api.ReGlassConfig;
import restudio.reglass.client.config.ReGlassSettingsIO;

public final class ReGlassSodiumConfigEntryPoint implements ConfigEntryPoint {
    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        if (!ModList.get().isLoaded("reeses_sodium_options")) return;
        ReGlass.LOGGER.info("ReGlass detected Reese's Sodium Options; registering Liquid Glass settings");
        builder.registerOwnModOptions().addPage(createPage(builder));
    }

    private OptionPageBuilder createPage(ConfigBuilder builder) {
        return builder.createOptionPage().setName(Component.literal("Liquid Glass"))
                .addOptionGroup(createAppearance(builder))
                .addOptionGroup(createShadow(builder));
    }

    private OptionGroupBuilder createAppearance(ConfigBuilder builder) {
        ReGlassConfig c = ReGlassConfig.INSTANCE;
        return builder.createOptionGroup().setName(Component.literal("Appearance"))
                .addOption(builder.createIntegerOption(id("blur_radius"))
                        .setStorageHandler(ReGlassSettingsIO::saveFromMemory)
                        .setName(Component.literal("Blur Radius"))
                        .setTooltip(Component.literal("Liquid Glass blur radius"))
                        .setDefaultValue(c.defaultBlurRadius)
                        .setRange(0, 32, 1)
                        .setValueFormatter(value -> Component.literal(String.valueOf(value)))
                        .setBinding(v -> { c.defaultBlurRadius = v; }, () -> c.defaultBlurRadius))
                .addOption(builder.createIntegerOption(id("tint_alpha"))
                        .setStorageHandler(ReGlassSettingsIO::saveFromMemory)
                        .setName(Component.literal("Tint Alpha"))
                        .setTooltip(Component.literal("Glass tint opacity, percent"))
                        .setDefaultValue(Math.round(c.defaultTintAlpha * 100f))
                        .setRange(0, 100, 1)
                        .setValueFormatter(value -> Component.literal(String.valueOf(value) + "%"))
                        .setBinding(v -> { c.defaultTintAlpha = v / 100f; }, () -> Math.round(c.defaultTintAlpha * 100f)));
    }

    private OptionGroupBuilder createShadow(ConfigBuilder builder) {
        ReGlassConfig c = ReGlassConfig.INSTANCE;
        return builder.createOptionGroup().setName(Component.literal("Shadow"))
                .addOption(builder.createIntegerOption(id("shadow_factor"))
                        .setStorageHandler(ReGlassSettingsIO::saveFromMemory)
                        .setName(Component.literal("Shadow Factor"))
                        .setTooltip(Component.literal("Shadow opacity, percent"))
                        .setDefaultValue(Math.round(c.defaultShadowFactor * 100f))
                        .setRange(0, 100, 1)
                        .setValueFormatter(value -> Component.literal(String.valueOf(value) + "%"))
                        .setBinding(v -> { c.defaultShadowFactor = v / 100f; }, () -> Math.round(c.defaultShadowFactor * 100f)))
                .addOption(builder.createIntegerOption(id("shadow_expand"))
                        .setStorageHandler(ReGlassSettingsIO::saveFromMemory)
                        .setName(Component.literal("Shadow Expand"))
                        .setTooltip(Component.literal("Shadow spread"))
                        .setDefaultValue(Math.round(c.defaultShadowExpand))
                        .setRange(0, 100, 1)
                        .setValueFormatter(value -> Component.literal(String.valueOf(value)))
                        .setBinding(v -> { c.defaultShadowExpand = v; }, () -> Math.round(c.defaultShadowExpand)));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("reglass", path);
    }
}
