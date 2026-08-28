package net.locallupo.goldenfrontier.client;

import net.locallupo.goldenfrontier.wire.WireEndpointCollisionFilter;
import net.locallupo.goldenfrontier.wire.WireRaycastResultFilter;
import net.locallupo.goldenfrontier.wire.WireRoutePlanner;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class ClientWireTerrain implements WireRoutePlanner.Terrain {
    private final ClientLevel level;
    private final Map<ColumnKey, List<WireRoutePlanner.Surface>> surfaces = new HashMap<>();

    ClientWireTerrain(ClientLevel level) {
        this.level = level;
    }

    @Override
    public int minY() {
        return level.dimensionType().minY();
    }

    @Override
    public int maxY() {
        return minY() + level.dimensionType().height() - 1;
    }

    @Override
    public List<WireRoutePlanner.Surface> surfacesAt(int x, int z, BlockPos first, BlockPos second) {
        // This gets called a lot while looking for a route. Keep the column result around
        // for this pass; WireRouteService throws the whole thing away when the world changes.
        ColumnKey key = new ColumnKey(x, z);
        List<WireRoutePlanner.Surface> cached = surfaces.get(key);
        if (cached != null) return cached;

        List<WireRoutePlanner.Surface> result = new ArrayList<>();
        for (int y = minY(); y <= maxY(); y++) {
            surfaceAt(x, z, y, first, second).ifPresent(surface ->
                    result.add(new WireRoutePlanner.Surface(surface.position(), surface.supportBlock())));
        }
        List<WireRoutePlanner.Surface> immutable = List.copyOf(result);
        surfaces.put(key, immutable);
        return immutable;
    }

    @Override
    public boolean lineClear(Vec3 start, Vec3 end, BlockPos first, BlockPos second) {
        return WireEndpointCollisionFilter.isClear((from, to) -> {
            HitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, CollisionContext.empty()));
            if (!WireRaycastResultFilter.isBlockCollision(hit) || !(hit instanceof BlockHitResult blockHit)) {
                return Optional.empty();
            }
            return Optional.of(new WireEndpointCollisionFilter.Hit(blockHit.getLocation(), blockHit.getBlockPos()));
        }, start, end, first, second);
    }

    private Optional<SurfacePoint> surfaceAt(int x, int z, int y, BlockPos first, BlockPos second) {
        BlockPos support = new BlockPos(x, y, z);
        if (support.equals(first) || support.equals(second)) return Optional.empty();

        VoxelShape shape = level.getBlockState(support).getCollisionShape(level, support);
        if (shape.isEmpty()) return Optional.empty();

        double surfaceY = y + shape.max(Direction.Axis.Y) + WireRoutePlanner.CLEARANCE - 0.025;
        BlockPos space = new BlockPos(x, (int) Math.floor(surfaceY), z);
        if (!space.equals(first) && !space.equals(second)) {
            VoxelShape spaceShape = level.getBlockState(space).getCollisionShape(level, space);
            if (!spaceShape.isEmpty()
                    && space.getY() + spaceShape.max(Direction.Axis.Y) > surfaceY - WireRoutePlanner.CLEARANCE + 0.025) {
                return Optional.empty();
            }
        }
        return Optional.of(new SurfacePoint(new Vec3(x + 0.5, surfaceY, z + 0.5), support));
    }

    private record SurfacePoint(Vec3 position, BlockPos supportBlock) {}
    private record ColumnKey(int x, int z) {}
}
