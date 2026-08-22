package net.locallupo.goldenfrontier.blocks;

import net.locallupo.goldenfrontier.GoldenFrontier;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class ModBlockItemIds {
    private static BlockItemId create(String name){
        Identifier id = Identifier.fromNamespaceAndPath(GoldenFrontier.MOD_ID, name);
        return BlockItemId.create(id, id);
    }

    // VANILLA BLOCK TYPES
    public static final BlockItemId SANDSTONE_COAL_ORE = create("sandstone_coal_ore");
    public static final BlockItemId SANDSTONE_COPPER_ORE = create("sandstone_copper_ore");
    public static final BlockItemId SANDSTONE_IRON_ORE = create("sandstone_iron_ore");
    public static final BlockItemId SANDSTONE_GOLD_ORE = create("sandstone_gold_ore");

    public static final BlockItemId RED_SANDSTONE_COAL_ORE = create("red_sandstone_coal_ore");
    public static final BlockItemId RED_SANDSTONE_COPPER_ORE = create("red_sandstone_copper_ore");
    public static final BlockItemId RED_SANDSTONE_IRON_ORE = create("red_sandstone_iron_ore");
    public static final BlockItemId RED_SANDSTONE_GOLD_ORE = create("red_sandstone_gold_ore");


    // PYRITE BLOCKS

    public static final BlockItemId PYRITE_ORE = create("pyrite_ore");
    public static final BlockItemId DEEPSLATE_PYRITE_ORE = create("deepslate_pyrite_ore");
    public static final BlockItemId SANDSTONE_PYRITE_ORE = create("sandstone_pyrite_ore");
    public static final BlockItemId RED_SANDSTONE_PYRITE_ORE = create("red_sandstone_pyrite_ore");
    public static final BlockItemId PYRITE_BLOCK = create("pyrite_block");
    public static final BlockItemId RAW_PYRITE_BLOCK = create("raw_pyrite_block");

    // SILVER BLOCKS

    public static final BlockItemId SILVER_ORE = create("silver_ore");
    public static final BlockItemId SANDSTONE_SILVER_ORE = create("sandstone_silver_ore");
    public static final BlockItemId RED_SANDSTONE_SILVER_ORE = create("red_sandstone_silver_ore");
    public static final BlockItemId DEEPSLATE_SILVER_ORE = create("deepslate_silver_ore");
    public static final BlockItemId SILVER_BLOCK = create("silver_block");
    public static final BlockItemId RAW_SILVER_BLOCK = create("raw_silver_block");

    // UTILITY BLOCKS

    public static final BlockItemId DETONATOR = create("detonator");
    public static final BlockItemId DYNAMITE = create("dynamite");

    // DECORATIVE BLOCKS

    public static final BlockItemId RED_PAINTED_PLANKS = create("red_painted_planks");
    public static final BlockItemId ORANGE_PAINTED_PLANKS = create("orange_painted_planks");
    public static final BlockItemId YELLOW_PAINTED_PLANKS = create("yellow_painted_planks");
    public static final BlockItemId LIME_PAINTED_PLANKS = create("lime_painted_planks");
    public static final BlockItemId GREEN_PAINTED_PLANKS = create("green_painted_planks");
    public static final BlockItemId CYAN_PAINTED_PLANKS = create("cyan_painted_planks");
    public static final BlockItemId LIGHT_BLUE_PAINTED_PLANKS = create("light_blue_painted_planks");
    public static final BlockItemId BLUE_PAINTED_PLANKS = create("blue_painted_planks");
    public static final BlockItemId PURPLE_PAINTED_PLANKS = create("purple_painted_planks");
    public static final BlockItemId MAGENTA_PAINTED_PLANKS = create("magenta_painted_planks");
    public static final BlockItemId PINK_PAINTED_PLANKS = create("pink_painted_planks");

}
