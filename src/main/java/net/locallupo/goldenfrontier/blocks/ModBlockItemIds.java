package net.locallupo.goldenfrontier.blocks;

import net.locallupo.goldenfrontier.GoldenFrontier;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class ModBlockItemIds {
    private static BlockItemId create(String name){
        Identifier id = Identifier.fromNamespaceAndPath(GoldenFrontier.MOD_ID, name);
        return BlockItemId.create(id, id);
    }

    // PYRITE BLOCKS

    public static final BlockItemId PYRITE_ORE = create("pyrite_ore");
    public static final BlockItemId DEEPSLATE_PYRITE_ORE = create("deepslate_pyrite_ore");
    public static final BlockItemId PYRITE_BLOCK = create("pyrite_block");
    public static final BlockItemId RAW_PYRITE_BLOCK = create("raw_pyrite_block");

    // SILVER BLOCKS

    public static final BlockItemId SILVER_ORE = create("silver_ore");
    public static final BlockItemId DEEPSLATE_SILVER_ORE = create("deepslate_silver_ore");
    public static final BlockItemId SILVER_BLOCK = create("silver_block");
    public static final BlockItemId RAW_SILVER_BLOCK = create("raw_silver_block");

    // UTILITY BLOCKS

    public static final BlockItemId DETONATOR = create("detonator");
    public static final BlockItemId DYNAMITE = create("dynamite");
}
