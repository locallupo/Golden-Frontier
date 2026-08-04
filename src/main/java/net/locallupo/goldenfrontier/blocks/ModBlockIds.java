package net.locallupo.goldenfrontier.blocks;

import net.locallupo.goldenfrontier.GoldenFrontier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class ModBlockIds {
    private static ResourceKey<Block> create(String name){
        Identifier id = Identifier.fromNamespaceAndPath(GoldenFrontier.MOD_ID, name);
        return ResourceKey.create(Registries.BLOCK, id);
    }

    // PYRITE BLOCKS

    public static final ResourceKey<Block> PYRITE_ORE = create("pyrite_ore");
    public static final ResourceKey<Block> DEEPSLATE_PYRITE_ORE = create("deepslate_pyrite_ore");
    public static final ResourceKey<Block> PYRITE_BLOCK = create("pyrite_block");
    public static final ResourceKey<Block> RAW_PYRITE_BLOCK = create("raw_pyrite_block");

    // SILVER BLOCKS

    public static final ResourceKey<Block> SILVER_ORE = create("silver_ore");
    public static final ResourceKey<Block> DEEPSLATE_SILVER_ORE = create("deepslate_silver_ore");
    public static final ResourceKey<Block> SILVER_BLOCK = create("silver_block");
    public static final ResourceKey<Block> RAW_SILVER_BLOCK = create("raw_silver_block");
}

