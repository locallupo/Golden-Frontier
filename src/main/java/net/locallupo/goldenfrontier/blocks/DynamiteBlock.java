package net.locallupo.goldenfrontier.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.locallupo.goldenfrontier.wire.WireNetworking;
import net.locallupo.goldenfrontier.wire.WireSavedData;
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

    public static void ignite(ServerLevel level, BlockPos pos) {
        if (level.getBlockState(pos).is(ModBlocks.DYNAMITE)) level.scheduleTick(pos, ModBlocks.DYNAMITE, 10);
    }

    public static void detonate(ServerLevel level, BlockPos pos) {
        if (level.getBlockState(pos).getBlock() instanceof DynamiteBlock dynamite) {
            dynamite.explodeAndPropagate(level, pos);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.is(this)) return;
        explodeAndPropagate(level, pos);
    }

    private void explodeAndPropagate(ServerLevel level, BlockPos pos) {
        // Capture the next hop before this bundle is removed by the blast.
        var next = WireSavedData.get(level).adjacentDynamite(level, pos);
        WireNetworking.broadcastIgnition(level, pos);
        level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                4.0F, Level.ExplosionInteraction.BLOCK);
        for (BlockPos dynamite : next) ignite(level, dynamite);
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
