package net.locallupo.goldenfrontier.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.locallupo.goldenfrontier.GoldenFrontier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class OreGeneration {
    private OreGeneration() {
    }

    private static ResourceKey<PlacedFeature> placedFeature(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                Identifier.fromNamespaceAndPath(GoldenFrontier.MOD_ID, name));
    }

    public static void initialize() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("pyrite_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("silver_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("sandstone_pyrite_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("sandstone_silver_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("sandstone_coal_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("sandstone_copper_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("sandstone_iron_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("sandstone_gold_ore")
        );




        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("red_sandstone_pyrite_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("red_sandstone_silver_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("red_sandstone_coal_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("red_sandstone_copper_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("red_sandstone_iron_ore")
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeature("red_sandstone_gold_ore")
        );
    }
}
