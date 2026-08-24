package net.locallupo.goldenfrontier.wire;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/** Applies the wire rule that only its two attached endpoint blocks are passable. */
public final class WireEndpointCollisionFilter {
    private WireEndpointCollisionFilter() {
    }

    public interface Raycaster {
        Optional<Hit> firstHit(Vec3 start, Vec3 end);
    }

    public record Hit(Vec3 location, BlockPos block) {
    }

    public static boolean isClear(Raycaster raycaster, Vec3 start, Vec3 end,
                                  BlockPos firstEndpoint, BlockPos secondEndpoint) {
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        if (length < 0.04) {
            return true;
        }
        Vec3 direction = delta.scale(1.0 / length);
        Vec3 cursor = start;
        for (int hitCount = 0; hitCount < 8; hitCount++) {
            Optional<Hit> hit = raycaster.firstHit(cursor, end);
            if (hit.isEmpty()) {
                return true;
            }
            if (!hit.get().block().equals(firstEndpoint) && !hit.get().block().equals(secondEndpoint)) {
                return false;
            }
            cursor = movePastBlock(hit.get().location(), direction, hit.get().block());
            // The endpoint collision shape can extend past the requested
            // surface point. Once we have moved beyond the segment end, the
            // remaining ray would point backwards into the ground.
            if (end.subtract(cursor).dot(direction) <= 0.0) {
                return true;
            }
        }
        return false;
    }

    private static Vec3 movePastBlock(Vec3 point, Vec3 direction, BlockPos block) {
        double distance = Double.POSITIVE_INFINITY;
        if (direction.x > 0.0) distance = Math.min(distance, (block.getX() + 1.0 - point.x) / direction.x);
        if (direction.x < 0.0) distance = Math.min(distance, (block.getX() - point.x) / direction.x);
        if (direction.y > 0.0) distance = Math.min(distance, (block.getY() + 1.0 - point.y) / direction.y);
        if (direction.y < 0.0) distance = Math.min(distance, (block.getY() - point.y) / direction.y);
        if (direction.z > 0.0) distance = Math.min(distance, (block.getZ() + 1.0 - point.z) / direction.z);
        if (direction.z < 0.0) distance = Math.min(distance, (block.getZ() - point.z) / direction.z);
        return point.add(direction.scale(Math.max(0.0, distance) + 0.001));
    }
}
