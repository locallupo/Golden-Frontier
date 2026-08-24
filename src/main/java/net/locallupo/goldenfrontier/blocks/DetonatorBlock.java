package net.locallupo.goldenfrontier.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.locallupo.goldenfrontier.wire.WireSavedData;
import net.locallupo.goldenfrontier.items.ModItems;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.math.OctahedralGroup;
import net.minecraft.util.RandomSource;

public class DetonatorBlock extends HorizontalDirectionalBlock {
    public static final BooleanProperty PRESSED = BooleanProperty.create("pressed");
    private static final int DETONATION_DELAY_TICKS = 10;

    // Needed for directional blocks
    public static final MapCodec<DetonatorBlock> CODEC =
            simpleCodec(DetonatorBlock::new);

    @Override
    protected MapCodec<? extends DetonatorBlock> codec() {
        return CODEC;
    }


    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(4, 0, 4, 12, 9, 12),          // Body
            Block.box(5, 14, 7.5, 11, 15, 8.5),    // Handle top
            Block.box(7.5, 8, 7.5, 8.5, 14, 8.5),  // Handle stem
            Block.box(5, 9, 5, 6, 10, 6),          // Left terminal
            Block.box(10, 9, 5, 11, 10, 6)         // Right terminal
    );

    private static final VoxelShape PRESSED_SHAPE = Shapes.or(
            Block.box(4, 0, 4, 12, 9, 12),          // Body
            Block.box(5, 10, 7.5, 11, 11, 8.5),     // Lowered handle top
            Block.box(7.5, 4, 7.5, 8.5, 10, 8.5),   // Lowered handle stem
            Block.box(5, 9, 5, 6, 10, 6),
            Block.box(10, 9, 5, 11, 10, 6)
    );


    private static final VoxelShape SHAPE_NORTH = SHAPE;

    private static final VoxelShape SHAPE_EAST =
            Shapes.rotate(SHAPE, OctahedralGroup.ROT_90_Y_NEG);

    private static final VoxelShape SHAPE_SOUTH =
            Shapes.rotate(SHAPE, OctahedralGroup.ROT_180_FACE_XZ);

    private static final VoxelShape SHAPE_WEST =
            Shapes.rotate(SHAPE, OctahedralGroup.ROT_90_Y_POS);

    private static final VoxelShape PRESSED_SHAPE_EAST =
            Shapes.rotate(PRESSED_SHAPE, OctahedralGroup.ROT_90_Y_NEG);
    private static final VoxelShape PRESSED_SHAPE_SOUTH =
            Shapes.rotate(PRESSED_SHAPE, OctahedralGroup.ROT_180_FACE_XZ);
    private static final VoxelShape PRESSED_SHAPE_WEST =
            Shapes.rotate(PRESSED_SHAPE, OctahedralGroup.ROT_90_Y_POS);


    public DetonatorBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(PRESSED, false)
        );
    }


    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(FACING, PRESSED);
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(
                        FACING,
                        context.getHorizontalDirection().getOpposite()
                );
    }


    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter world,
            BlockPos pos,
            CollisionContext context
    ) {
        return rotateShape(state);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        if (state.getValue(PRESSED)) {
            return InteractionResult.SUCCESS_SERVER;
        }
        serverLevel.setBlock(pos, state.setValue(PRESSED, true), Block.UPDATE_CLIENTS);
        serverLevel.scheduleTick(pos, this, DETONATION_DELAY_TICKS);
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        if (!state.is(this) || !state.getValue(PRESSED)) {
            return;
        }
        for (BlockPos dynamite : WireSavedData.get(serverLevel).connectedDynamite(serverLevel, pos)) {
            serverLevel.explode(
                    null,
                    dynamite.getX() + 0.5D,
                    dynamite.getY() + 0.5D,
                    dynamite.getZ() + 0.5D,
                    4.0F,
                    Level.ExplosionInteraction.BLOCK
            );
        }
        if (serverLevel.getBlockState(pos).is(this)) {
            serverLevel.setBlock(pos, state.setValue(PRESSED, false), Block.UPDATE_CLIENTS);
        }
    }

    /**
     * A detonator has an empty-hand action, but wire must reach WireItem.useOn
     * so it can be selected as a connection endpoint rather than consumed here.
     */
    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (itemStack.is(ModItems.WIRE)) {
            return InteractionResult.PASS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }


    private static VoxelShape rotateShape(BlockState state) {
        if (state.getValue(PRESSED)) {
            return switch (state.getValue(FACING)) {
                case EAST -> PRESSED_SHAPE_EAST;
                case SOUTH -> PRESSED_SHAPE_SOUTH;
                case WEST -> PRESSED_SHAPE_WEST;
                default -> PRESSED_SHAPE;
            };
        }
        return switch (state.getValue(FACING)) {
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }
}
