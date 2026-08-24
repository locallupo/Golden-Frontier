package net.locallupo.goldenfrontier.blocks;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.locallupo.goldenfrontier.components.ModComponents;
import net.locallupo.goldenfrontier.components.OriginalPlankComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class PaintedPlanksBlock extends Block implements EntityBlock {
    private static final Map<DyeColor, Block> BY_COLOR = new EnumMap<>(DyeColor.class);
    private static final Map<Block, DyeColor> COLORS = new HashMap<>();

    public PaintedPlanksBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(net.minecraft.core.BlockPos pos, BlockState state) {
        return new ColoredPlankBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, net.minecraft.core.BlockPos pos, BlockState state,
                            LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos) instanceof ColoredPlankBlockEntity entity) {
            OriginalPlankComponent original = stack.get(ModComponents.ORIGINAL_PLANK);
            entity.setOriginal(original != null ? original : component(Blocks.OAK_PLANKS.defaultBlockState()));
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, net.minecraft.core.BlockPos pos,
                              BlockState state, BlockEntity blockEntity, ItemStack tool) {
        if (blockEntity instanceof ColoredPlankBlockEntity entity) {
            ItemStack drop = new ItemStack(state.getBlock().asItem());
            if (entity.original() != null) drop.set(ModComponents.ORIGINAL_PLANK, entity.original());
            Block.popResource(level, pos, drop);
        }
    }

    public static void initialize() {
        add(DyeColor.RED, ModBlocks.RED_PAINTED_PLANKS);
        add(DyeColor.ORANGE, ModBlocks.ORANGE_PAINTED_PLANKS);
        add(DyeColor.YELLOW, ModBlocks.YELLOW_PAINTED_PLANKS);
        add(DyeColor.LIME, ModBlocks.LIME_PAINTED_PLANKS);
        add(DyeColor.GREEN, ModBlocks.GREEN_PAINTED_PLANKS);
        add(DyeColor.CYAN, ModBlocks.CYAN_PAINTED_PLANKS);
        add(DyeColor.LIGHT_BLUE, ModBlocks.LIGHT_BLUE_PAINTED_PLANKS);
        add(DyeColor.BLUE, ModBlocks.BLUE_PAINTED_PLANKS);
        add(DyeColor.PURPLE, ModBlocks.PURPLE_PAINTED_PLANKS);
        add(DyeColor.MAGENTA, ModBlocks.MAGENTA_PAINTED_PLANKS);
        add(DyeColor.PINK, ModBlocks.PINK_PAINTED_PLANKS);

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.isSpectator()) return InteractionResult.PASS;

            BlockPosAndState target = new BlockPosAndState(hit.getBlockPos(), level.getBlockState(hit.getBlockPos()));
            ItemStack held = player.getItemInHand(hand);
            DyeColor dye = dyeColor(held);

            if (COLORS.containsKey(target.state.getBlock())) {
                if (held.is(ItemTags.AXES)) {
                    if (!level.isClientSide() && level.getBlockEntity(target.pos) instanceof ColoredPlankBlockEntity entity) {
                        BlockState original = restore(entity.original());
                        if (original != null) {
                            level.setBlock(target.pos, original, 3);
                            damageTool(player, held);
                            level.playSound(null, target.pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }

                if (dye != null && dye != COLORS.get(target.state.getBlock())) {
                    recolor(level, target.pos, target.state, dye, held, player);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }

            if (isSupportedSourcePlank(target.state) && dye != null) {
                if (!level.isClientSide()) {
                    placeColored(level, target.pos, target.state, dye, held, player);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
    }

    private static void add(DyeColor color, Block block) {
        BY_COLOR.put(color, block);
        COLORS.put(block, color);
    }

    private static boolean isSupportedSourcePlank(BlockState state) {
        return state.is(BlockTags.PLANKS) && !state.is(Blocks.BAMBOO_PLANKS);
    }

    public static Block block(DyeColor color) {
        return BY_COLOR.get(color);
    }

    public static DyeColor color(Block block) {
        return COLORS.get(block);
    }

    public static OriginalPlankComponent component(BlockState state) {
        Map<String, String> properties = new HashMap<>();
        state.getValues().forEach(value -> properties.put(value.property().getName(), value.valueName()));
        Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return new OriginalPlankComponent(id, properties);
    }

    public static BlockState restore(OriginalPlankComponent component) {
        if (component == null) return null;
        Block block = BuiltInRegistries.BLOCK.getOptional(component.block()).orElse(null);
        if (block == null) return null;
        BlockState state = block.defaultBlockState();
        for (Map.Entry<String, String> entry : component.properties().entrySet()) {
            Property<?> property = state.getBlock().getStateDefinition().getProperty(entry.getKey());
            if (property != null) state = setProperty(state, property, entry.getValue());
        }
        return state;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static BlockState setProperty(BlockState state, Property property, String value) {
        java.util.Optional<?> parsed = property.getValue(value);
        return parsed.isPresent() ? (BlockState) state.setValue(property, (Comparable) parsed.get()) : state;
    }

    private static void placeColored(Level level, net.minecraft.core.BlockPos pos, BlockState original,
                                     DyeColor dye, ItemStack held, net.minecraft.world.entity.player.Player player) {
        Block block = block(dye);
        level.setBlock(pos, block.defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof ColoredPlankBlockEntity entity) {
            entity.setOriginal(component(original));
        }
        finishDye(level, pos, held, player);
    }

    private static void recolor(Level level, net.minecraft.core.BlockPos pos, BlockState state,
                                DyeColor dye, ItemStack held, net.minecraft.world.entity.player.Player player) {
        OriginalPlankComponent original = level.getBlockEntity(pos) instanceof ColoredPlankBlockEntity entity
                ? entity.original() : null;
        level.setBlock(pos, block(dye).defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof ColoredPlankBlockEntity entity) entity.setOriginal(original);
        finishDye(level, pos, held, player);
    }

    private static void finishDye(Level level, net.minecraft.core.BlockPos pos, ItemStack held,
                                  net.minecraft.world.entity.player.Player player) {
        if (!player.isCreative()) held.shrink(1);
        level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static void damageTool(net.minecraft.world.entity.player.Player player, ItemStack stack) {
        if (!player.isCreative()) stack.hurtWithoutBreaking(1, player);
    }

    private static DyeColor dyeColor(ItemStack stack) {
        for (DyeColor color : DyeColor.values()) if (stack.is(Items.DYE.pick(color))) return color;
        return null;
    }

    private record BlockPosAndState(net.minecraft.core.BlockPos pos, BlockState state) {}
}
