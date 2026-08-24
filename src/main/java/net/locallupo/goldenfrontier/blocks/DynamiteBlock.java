package net.locallupo.goldenfrontier.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A small dynamite bundle, not a full opaque cube. */
public final class DynamiteBlock extends Block {
    public static final MapCodec<DynamiteBlock> CODEC = simpleCodec(DynamiteBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(4.5, 0.0, 4.5, 11.5, 10.0, 11.5),
            Block.box(7.0, 10.0, 7.0, 9.0, 16.0, 9.0)
    );

    public DynamiteBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
