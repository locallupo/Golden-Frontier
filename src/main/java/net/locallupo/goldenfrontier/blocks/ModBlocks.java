package net.locallupo.goldenfrontier.blocks;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.locallupo.goldenfrontier.GoldenFrontier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class ModBlocks {
    private static Block register(BlockItemId id, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties){
        Block block = register(id.block(), blockFactory, properties);

        BlockItem blockItem = block instanceof PaintedPlanksBlock
                ? new PaintedPlankBlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()))
                : new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()));
        Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);

        return block;
    }

    private static Block register(ResourceKey<Block> id, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties){
        Block block = blockFactory.apply(properties.setId(id));

        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    // VANILLA BLOCKS

    public static final Block SANDSTONE_COAL_ORE = register(
            ModBlockItemIds.SANDSTONE_COAL_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)
    );

    public static final Block SANDSTONE_COPPER_ORE = register(
            ModBlockItemIds.SANDSTONE_COPPER_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)
    );

    public static final Block SANDSTONE_IRON_ORE = register(
            ModBlockItemIds.SANDSTONE_IRON_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)
    );

    public static final Block SANDSTONE_GOLD_ORE = register(
            ModBlockItemIds.SANDSTONE_GOLD_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)
    );

    public static final Block RED_SANDSTONE_COAL_ORE = register(
            ModBlockItemIds.RED_SANDSTONE_COAL_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE)
    );

    public static final Block RED_SANDSTONE_COPPER_ORE = register(
            ModBlockItemIds.RED_SANDSTONE_COPPER_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE)
    );

    public static final Block RED_SANDSTONE_IRON_ORE = register(
            ModBlockItemIds.RED_SANDSTONE_IRON_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE)
    );

    public static final Block RED_SANDSTONE_GOLD_ORE = register(
            ModBlockItemIds.RED_SANDSTONE_GOLD_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE)
    );


    // PYRITE BLOCKS

    public static final Block PYRITE_ORE = register(
            ModBlockItemIds.PYRITE_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_ORE)
    );

    public static final Block DEEPSLATE_PYRITE_ORE = register(
            ModBlockItemIds.DEEPSLATE_PYRITE_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_GOLD_ORE)
    );

    public static final Block SANDSTONE_PYRITE_ORE = register(
            ModBlockItemIds.SANDSTONE_PYRITE_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)
    );

    public static final Block RED_SANDSTONE_PYRITE_ORE = register(
            ModBlockItemIds.RED_SANDSTONE_PYRITE_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE)
    );

    public static final Block PYRITE_BLOCK = register(
            ModBlockItemIds.PYRITE_BLOCK,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK)
    );

    public static final Block RAW_PYRITE_BLOCK = register(
            ModBlockItemIds.RAW_PYRITE_BLOCK,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_GOLD_BLOCK)
    );



    // SILVER BLOCKS

    public static final Block SILVER_ORE = register(
            ModBlockItemIds.SILVER_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)
    );

    public static final Block DEEPSLATE_SILVER_ORE = register(
            ModBlockItemIds.DEEPSLATE_SILVER_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE)
    );

    public static final Block SANDSTONE_SILVER_ORE = register(
            ModBlockItemIds.SANDSTONE_SILVER_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)
    );

    public static final Block RED_SANDSTONE_SILVER_ORE = register(
            ModBlockItemIds.RED_SANDSTONE_SILVER_ORE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE)
    );

    public static final Block SILVER_BLOCK = register(
            ModBlockItemIds.SILVER_BLOCK,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final Block RAW_SILVER_BLOCK = register(
            ModBlockItemIds.RAW_SILVER_BLOCK,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK)
    );


    // UTILITY BLOCKS

    public static final Block DETONATOR = register(
            ModBlockItemIds.DETONATOR,
            DetonatorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS)
    );

    public static final Block DYNAMITE = register(
            ModBlockItemIds.DYNAMITE,
            DynamiteBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.TNT).noOcclusion()
    );

    // DECORATIVE BLOCKS

    public static final Block RED_PAINTED_PLANKS = register(
            ModBlockItemIds.RED_PAINTED_PLANKS,
            PaintedPlanksBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final Block ORANGE_PAINTED_PLANKS = register(
            ModBlockItemIds.ORANGE_PAINTED_PLANKS,
            PaintedPlanksBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final Block YELLOW_PAINTED_PLANKS = register(
            ModBlockItemIds.YELLOW_PAINTED_PLANKS,
            PaintedPlanksBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final Block LIME_PAINTED_PLANKS = register(
            ModBlockItemIds.LIME_PAINTED_PLANKS,
            PaintedPlanksBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final Block GREEN_PAINTED_PLANKS = register(
            ModBlockItemIds.GREEN_PAINTED_PLANKS,
            PaintedPlanksBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final Block CYAN_PAINTED_PLANKS = register(
            ModBlockItemIds.CYAN_PAINTED_PLANKS,
            PaintedPlanksBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final Block LIGHT_BLUE_PAINTED_PLANKS = register(
            ModBlockItemIds.LIGHT_BLUE_PAINTED_PLANKS,
            PaintedPlanksBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final Block BLUE_PAINTED_PLANKS = register(
            ModBlockItemIds.BLUE_PAINTED_PLANKS,
            PaintedPlanksBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final Block PURPLE_PAINTED_PLANKS = register(
            ModBlockItemIds.PURPLE_PAINTED_PLANKS,
            PaintedPlanksBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final Block MAGENTA_PAINTED_PLANKS = register(
            ModBlockItemIds.MAGENTA_PAINTED_PLANKS,
            PaintedPlanksBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final Block PINK_PAINTED_PLANKS = register(
            ModBlockItemIds.PINK_PAINTED_PLANKS,
            PaintedPlanksBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final net.minecraft.world.level.block.entity.BlockEntityType<ColoredPlankBlockEntity> COLORED_PLANK_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            GoldenFrontier.id("colored_plank"),
            new net.minecraft.world.level.block.entity.BlockEntityType<>(ColoredPlankBlockEntity::new, java.util.Set.of(
                    RED_PAINTED_PLANKS, ORANGE_PAINTED_PLANKS, YELLOW_PAINTED_PLANKS, LIME_PAINTED_PLANKS,
                    GREEN_PAINTED_PLANKS, CYAN_PAINTED_PLANKS, LIGHT_BLUE_PAINTED_PLANKS, BLUE_PAINTED_PLANKS,
                    PURPLE_PAINTED_PLANKS, MAGENTA_PAINTED_PLANKS, PINK_PAINTED_PLANKS))
    );





    public static void initialize() {

        // NATURAL BLOCKS

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
                .register(entries -> {
                    entries.accept(ModBlocks.SANDSTONE_COAL_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_COAL_ORE);
                    entries.accept(ModBlocks.SANDSTONE_COPPER_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_COPPER_ORE);
                    entries.accept(ModBlocks.SANDSTONE_IRON_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_IRON_ORE);
                    entries.accept(ModBlocks.SANDSTONE_GOLD_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_GOLD_ORE);

                    entries.accept(ModBlocks.SILVER_ORE);
                    entries.accept(ModBlocks.SANDSTONE_SILVER_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_SILVER_ORE);
                    entries.accept(ModBlocks.DEEPSLATE_SILVER_ORE);
                    entries.accept(ModBlocks.RAW_SILVER_BLOCK);

                    entries.accept(ModBlocks.PYRITE_ORE);
                    entries.accept(ModBlocks.SANDSTONE_PYRITE_ORE);
                    entries.accept(ModBlocks.RED_SANDSTONE_PYRITE_ORE);
                    entries.accept(ModBlocks.DEEPSLATE_PYRITE_ORE);
                    entries.accept(ModBlocks.RAW_PYRITE_BLOCK);
                });


        // BUILDING BLOCKS

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
                .register(entries -> {
                    entries.accept(ModBlocks.SILVER_BLOCK);

                    entries.accept(ModBlocks.PYRITE_BLOCK);

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
                });



        // FUNCTIONAL BLOCKS

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register(entries -> {
                    entries.accept(ModBlocks.DETONATOR);

                    entries.accept(ModBlocks.DYNAMITE);
                });



        // COLOURED BLOCKS

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COLORED_BLOCKS)
                .register(entries -> {
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
                });

    }
}
