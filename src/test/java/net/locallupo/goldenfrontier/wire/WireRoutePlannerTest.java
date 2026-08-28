package net.locallupo.goldenfrontier.wire;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WireRoutePlannerTest {

    @Test
    void raycastMissIsNotTreatedAsABlockCollision() {
        assertFalse(WireRaycastResultFilter.isBlockCollision(
                BlockHitResult.miss(new Vec3(1.0, 2.0, 3.0), Direction.UP, new BlockPos(1, 2, 3))));
        assertTrue(WireRaycastResultFilter.isBlockCollision(
                new BlockHitResult(new Vec3(1.0, 2.0, 3.0), Direction.UP, new BlockPos(1, 2, 3), false)));
    }

    @Test
    void routesAroundADetourWiderThanTheOldSixteenBlockMargin() {
        TestTerrain terrain = TestTerrain.floor(-5, 45, -24, 24, 0);
        // Going around the wall should be cheaper than going over it.
        for (int z = -17; z <= 17; z++) {
            for (int y = 1; y <= 40; y++) terrain.solid(20, y, z);
        }

        List<Vec3> route = WireRoutePlanner.findRoute(terrain, new BlockPos(0, 1, 0), new BlockPos(40, 1, 0));

        assertFalse(route.isEmpty());
        assertTrue(route.stream().anyMatch(point -> Math.abs(point.z) >= 18.5), "route must go around the wall");
    }

    @Test
    void allowsAClearMultiBlockDrop() {
        TestTerrain terrain = new TestTerrain();
        terrain.solid(0, 5, 0).solid(1, 0, 0);

        List<Vec3> route = WireRoutePlanner.findRoute(terrain, new BlockPos(0, 6, 0), new BlockPos(1, 1, 0));

        assertFalse(route.isEmpty());
        assertTrue(route.stream().anyMatch(point -> Math.abs(point.y - 6.08) < 0.01));
        assertTrue(route.stream().anyMatch(point -> Math.abs(point.y - 1.08) < 0.01));
        assertTrue(route.stream().anyMatch(point -> Math.abs(point.x - 1.025) < 0.01 && Math.abs(point.y - 6.08) < 0.01));
        assertTrue(route.stream().anyMatch(point -> Math.abs(point.x - 1.025) < 0.01 && Math.abs(point.y - 1.08) < 0.01));
    }

    @Test
    void bridgesUpToTwoUnsupportedCellsButNotThree() {
        TestTerrain twoCellGap = new TestTerrain();
        twoCellGap.solid(0, 0, 0).solid(3, 0, 0);
        assertFalse(WireRoutePlanner.findRoute(twoCellGap, new BlockPos(0, 1, 0), new BlockPos(3, 1, 0)).isEmpty());

        TestTerrain threeCellGap = new TestTerrain();
        threeCellGap.solid(0, 0, 0).solid(4, 0, 0);
        assertTrue(WireRoutePlanner.findRoute(threeCellGap, new BlockPos(0, 1, 0), new BlockPos(4, 1, 0)).isEmpty());
    }

    @Test
    void adjacentEndpointsAreTransparentButAnOrdinaryBlockIsNot() {
        BlockPos first = new BlockPos(0, 1, 0);
        BlockPos second = new BlockPos(1, 1, 0);
        Vec3 start = new Vec3(.5, 1.08, .5);
        Vec3 end = new Vec3(1.5, 1.08, .5);

        assertTrue(WireEndpointCollisionFilter.isClear(new Hits(
                new WireEndpointCollisionFilter.Hit(new Vec3(.60, 1.08, .5), first),
                new WireEndpointCollisionFilter.Hit(new Vec3(.90, 1.08, .5), second)
        ), start, end, first, second));

        assertFalse(WireEndpointCollisionFilter.isClear(new Hits(
                new WireEndpointCollisionFilter.Hit(new Vec3(.60, 1.08, .5), first),
                new WireEndpointCollisionFilter.Hit(new Vec3(.90, 1.08, .5), new BlockPos(2, 1, 0))
        ), start, end, first, second));
    }

    @Test
    void endpointHitPastTheSurfacePointDoesNotRaycastBackIntoTheFloor() {
        BlockPos endpoint = new BlockPos(0, 1, 0);
        Vec3 start = new Vec3(.5, 1.5, .5);
        Vec3 groundSurface = new Vec3(.5, 1.08, .5);

        assertTrue(WireEndpointCollisionFilter.isClear(new Hits(
                // The ray exits below the wire's clearance point.
                new WireEndpointCollisionFilter.Hit(new Vec3(.5, 1.0, .5), endpoint)
        ), start, groundSurface, endpoint, new BlockPos(2, 1, 0)));
    }

    @Test
    void endpointAttachmentDoesNotDependOnItsCollisionShape() {
        TestTerrain terrain = TestTerrain.floor(-1, 2, -1, 1, 0).rejectEndpointAttachmentRaycasts();

        List<Vec3> route = WireRoutePlanner.findRoute(terrain, new BlockPos(0, 1, 0), new BlockPos(1, 1, 0));

        assertFalse(route.isEmpty());
    }

    @Test
    void caveFloorsCannotBeUsedAsFreeEndpointSurfaces() {
        TestTerrain terrain = TestTerrain.floor(-1, 4, -1, 1, 0);
        for (int x = -1; x <= 4; x++) for (int z = -1; z <= 1; z++) terrain.solid(x, -20, z);

        List<Vec3> route = WireRoutePlanner.findRoute(terrain, new BlockPos(0, 1, 0), new BlockPos(3, 1, 0));

        assertFalse(route.isEmpty());
        assertTrue(route.stream().allMatch(point -> point.y > 0.0), "route must remain on the surface, not a cave floor");
    }

    @Test
    void caveEndpointsUseTheirLocalCaveFloorInsteadOfASeparateSurfaceLayer() {
        TestTerrain terrain = TestTerrain.floor(-1, 4, -1, 1, -5)
                .solid(0, 20, 0).solid(3, 20, 0);

        List<Vec3> route = WireRoutePlanner.findRoute(terrain, new BlockPos(0, -4, 0), new BlockPos(3, -4, 0));

        assertFalse(route.isEmpty());
        assertTrue(route.stream().anyMatch(point -> point.y < 0.0), "route should use the cave floor");
    }

    @Test
    void exitsAnEndpointBlockInsteadOfGivingUpOnRepeatedInsideHits() {
        BlockPos endpoint = new BlockPos(0, 1, 0);
        Vec3 start = new Vec3(.5, 1.08, .5);
        Vec3 end = new Vec3(2.5, 1.08, .5);

        assertTrue(WireEndpointCollisionFilter.isClear((from, ignoredEnd) ->
                from.x < 1.0
                        ? Optional.of(new WireEndpointCollisionFilter.Hit(from, endpoint))
                        : Optional.empty(), start, end, endpoint, new BlockPos(3, 1, 0)));
    }

    private static final class Hits implements WireEndpointCollisionFilter.Raycaster {
        private final List<WireEndpointCollisionFilter.Hit> hits;
        private int index;

        private Hits(WireEndpointCollisionFilter.Hit... hits) {
            this.hits = List.of(hits);
        }

        @Override
        public Optional<WireEndpointCollisionFilter.Hit> firstHit(Vec3 start, Vec3 end) {
            return index < hits.size() ? Optional.of(hits.get(index++)) : Optional.empty();
        }
    }

    private static final class TestTerrain implements WireRoutePlanner.Terrain {
        private final Set<BlockPos> blocks = new HashSet<>();
        private boolean rejectEndpointAttachmentRaycasts;

        static TestTerrain floor(int minX, int maxX, int minZ, int maxZ, int y) {
            TestTerrain terrain = new TestTerrain();
            for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) terrain.solid(x, y, z);
            return terrain;
        }

        TestTerrain solid(int x, int y, int z) {
            blocks.add(new BlockPos(x, y, z));
            return this;
        }

        TestTerrain rejectEndpointAttachmentRaycasts() {
            rejectEndpointAttachmentRaycasts = true;
            return this;
        }

        @Override public int minY() { return -64; }
        @Override public int maxY() { return 320; }

        @Override
        public List<WireRoutePlanner.Surface> surfacesAt(int x, int z, BlockPos first, BlockPos second) {
            return blocks.stream()
                    .filter(block -> block.getX() == x && block.getZ() == z)
                    .filter(block -> !block.equals(first) && !block.equals(second))
                    .filter(block -> !blocks.contains(block.above()))
                    .sorted(java.util.Comparator.comparingInt(BlockPos::getY))
                    .map(block -> new WireRoutePlanner.Surface(new Vec3(x + .5, block.getY() + 1.08, z + .5), block))
                    .toList();
        }

        @Override
        public boolean lineClear(Vec3 start, Vec3 end, BlockPos first, BlockPos second) {
            if (rejectEndpointAttachmentRaycasts
                    && (BlockPos.containing(start).equals(first) || BlockPos.containing(end).equals(first)
                    || BlockPos.containing(start).equals(second) || BlockPos.containing(end).equals(second))) {
                return false;
            }
            // Keep the test terrain independent of Minecraft's collision engine.
            int samples = Math.max(1, (int) Math.ceil(start.distanceTo(end) * 32));
            for (int i = 1; i < samples; i++) {
                Vec3 point = start.lerp(end, i / (double) samples);
                BlockPos block = BlockPos.containing(point);
                if (!block.equals(first) && !block.equals(second) && blocks.contains(block)) return false;
            }
            return true;
        }
    }
}
