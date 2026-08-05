package net.locallupo.goldenfrontier.blocks;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class ModBlocks {
    private static Block register(BlockItemId id, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties){
        Block block = register(id.block(), blockFactory, properties);

        BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()));
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
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
    );

    public static final Block DYNAMITE = register(
            ModBlockItemIds.DYNAMITE,
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
    );






    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.SANDSTONE_COAL_ORE.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.SANDSTONE_COPPER_ORE.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.SANDSTONE_IRON_ORE.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.SANDSTONE_GOLD_ORE.asItem());
        });


        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.PYRITE_ORE.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.DEEPSLATE_PYRITE_ORE.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.PYRITE_BLOCK.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.RAW_PYRITE_BLOCK.asItem());
        });


        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.SILVER_ORE.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.DEEPSLATE_SILVER_ORE.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.SILVER_BLOCK.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.RAW_SILVER_BLOCK.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.DETONATOR.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.DYNAMITE.asItem());
        });

    }
}
