package net.locallupo.goldenfrontier.items;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.locallupo.goldenfrontier.GoldenFrontier;
import net.locallupo.goldenfrontier.blocks.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroup {

    public static final ResourceKey<CreativeModeTab> GOLDEN_FRONTIER = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            GoldenFrontier.id("golden_frontier")
    );

    public static void initialize(){
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                GOLDEN_FRONTIER,
                FabricCreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.golden_frontier"))
                        .icon(() -> new ItemStack(ModBlocks.PYRITE_ORE))
                        .displayItems(((parameters, output) -> {}))
                        .build()
        );

        CreativeModeTabEvents.modifyOutputEvent(GOLDEN_FRONTIER)
                .register(entries -> {
                    entries.accept(ModBlocks.SANDSTONE_COAL_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_COAL_ORE);

                    entries.accept(ModItems.COPPER_DUST);
                    entries.accept(ModItems.COPPER_FLAKE);
                    entries.accept(ModItems.COPPER_CHUNK);
                    entries.accept(ModBlocks.SANDSTONE_COPPER_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_COPPER_ORE);

                    entries.accept(ModItems.IRON_DUST);
                    entries.accept(ModItems.IRON_FLAKE);
                    entries.accept(ModItems.IRON_CHUNK);
                    entries.accept(ModBlocks.SANDSTONE_IRON_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_IRON_ORE);

                    entries.accept(ModItems.GOLD_DUST);
                    entries.accept(ModItems.GOLD_FLAKE);
                    entries.accept(ModItems.GOLD_CHUNK);
                    entries.accept(ModBlocks.SANDSTONE_GOLD_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_GOLD_ORE);

                    entries.accept(ModItems.SILVER_DUST);
                    entries.accept(ModItems.SILVER_FLAKE);
                    entries.accept(ModItems.SILVER_CHUNK);
                    entries.accept(ModItems.RAW_SILVER);
                    entries.accept(ModItems.SILVER_INGOT);
                    entries.accept(ModItems.SILVER_NUGGET);
                    entries.accept(ModBlocks.SILVER_ORE);
                    entries.accept(ModBlocks.SANDSTONE_SILVER_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_SILVER_ORE);
                    entries.accept(ModBlocks.DEEPSLATE_SILVER_ORE);
                    entries.accept(ModBlocks.RAW_SILVER_BLOCK);
                    entries.accept(ModBlocks.SILVER_BLOCK);

                    entries.accept(ModItems.PYRITE_DUST);
                    entries.accept(ModItems.PYRITE_NUGGET);
                    entries.accept(ModItems.PYRITE_CHUNK);
                    entries.accept(ModItems.RAW_PYRITE);
                    entries.accept(ModItems.PYRITE_INGOT);
                    entries.accept(ModItems.PYRITE_NUGGET);
                    entries.accept(ModBlocks.PYRITE_ORE);
                    entries.accept(ModBlocks.SANDSTONE_PYRITE_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_PYRITE_ORE);
                    entries.accept(ModBlocks.DEEPSLATE_PYRITE_ORE);
                    entries.accept(ModBlocks.RAW_PYRITE_BLOCK);
                    entries.accept(ModBlocks.PYRITE_BLOCK);

                    entries.accept(ModItems.QUARTZ_SHARD);

                    entries.accept(ModBlocks.RED_PAINTED_PLANKS);
                    entries.accept(ModBlocks.ORANGE_PAINTED_PLANKS);
                    entries.accept(ModBlocks.YELLOW_PAINTED_PLANKS);
                    entries.accept(ModBlocks.LIME_PAINTED_PLANKS);
                    entries.accept(ModBlocks.GREEN_PAINTED_PLANKS);
                    entries.accept(ModBlocks.CYAN_PAINTED_PLANKS);
                    entries.accept(ModBlocks.LIGHT_BLUE_PAINTED_PLANKS);
                    entries.accept(ModBlocks.BLUE_PAINTED_PLANKS);
                    entries.accept(ModBlocks.PURPLE_PAINTED_PLANKS);
                    entries.accept(ModBlocks.MAGENTA_PAINTED_PLANKS);
                    entries.accept(ModBlocks.PINK_PAINTED_PLANKS);

                    entries.accept(ModItems.GOLD_PAN);

                    entries.accept(ModItems.WIRE);
                    entries.accept(ModBlocks.DETONATOR);
                    entries.accept(ModBlocks.DYNAMITE);

                    entries.accept(ModItems.GOLD_POCKET_WATCH);
                    entries.accept(ModItems.SILVER_POCKET_WATCH);

                    entries.accept(ModItems.CATTLEMAN_HAT);
                    entries.accept(ModItems.RED_CATTLEMAN_HAT);
                    entries.accept(ModItems.ORANGE_CATTLEMAN_HAT);
                    entries.accept(ModItems.YELLOW_CATTLEMAN_HAT);
                    entries.accept(ModItems.GREEN_CATTLEMAN_HAT);
                    entries.accept(ModItems.CYAN_CATTLEMAN_HAT);
                    entries.accept(ModItems.PURPLE_CATTLEMAN_HAT);
                    entries.accept(ModItems.MAGENTA_CATTLEMAN_HAT);
                    entries.accept(ModItems.WHITE_CATTLEMAN_HAT);
                    entries.accept(ModItems.BLACK_CATTLEMAN_HAT);

                    entries.accept(ModItems.GAMBLER_HAT);
                    entries.accept(ModItems.RED_GAMBLER_HAT);
                    entries.accept(ModItems.ORANGE_GAMBLER_HAT);
                    entries.accept(ModItems.YELLOW_GAMBLER_HAT);
                    entries.accept(ModItems.GREEN_GAMBLER_HAT);
                    entries.accept(ModItems.CYAN_GAMBLER_HAT);
                    entries.accept(ModItems.PURPLE_GAMBLER_HAT);
                    entries.accept(ModItems.MAGENTA_GAMBLER_HAT);
                    entries.accept(ModItems.WHITE_GAMBLER_HAT);
                    entries.accept(ModItems.BLACK_GAMBLER_HAT);

                    entries.accept(ModItems.NEVADA_HAT);
                    entries.accept(ModItems.RED_NEVADA_HAT);
                    entries.accept(ModItems.ORANGE_NEVADA_HAT);
                    entries.accept(ModItems.YELLOW_NEVADA_HAT);
                    entries.accept(ModItems.GREEN_NEVADA_HAT);
                    entries.accept(ModItems.CYAN_NEVADA_HAT);
                    entries.accept(ModItems.PURPLE_NEVADA_HAT);
                    entries.accept(ModItems.MAGENTA_NEVADA_HAT);
                    entries.accept(ModItems.WHITE_NEVADA_HAT);
                    entries.accept(ModItems.BLACK_NEVADA_HAT);
                });
    }
}
