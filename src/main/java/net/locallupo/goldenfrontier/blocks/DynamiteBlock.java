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

public final class DynamiteBlock extends Block {
    public static final MapCodec<DynamiteBlock> CODEC = simpleCodec(DynamiteBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(4.5, 0.0, 4.5, 11.5, 10.0, 11.5)
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
        WireSavedData data = WireSavedData.get(level);
        // Capture both lists before the explosion changes the endpoint states.
        var next = data.adjacentDynamite(level, pos);
        // Only the next-hop segments are newly burning. The segment from the
        // previous dynamite/detonator has already been consumed and must not
        // be restarted by the next ignition event.
        var ignitionConnections = data.connectionsToward(pos, next);
        level.destroyBlock(pos, false);
        level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                4.0F, Level.ExplosionInteraction.BLOCK);
        // Publish the ignition and the post-explosion wire snapshot together.
        // This prevents the client from pairing the ignition animation with a
        // stale connection list and briefly drawing the full route.
        data.pruneInvalid(level);
        WireNetworking.broadcastIgnition(level, pos, ignitionConnections);
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
