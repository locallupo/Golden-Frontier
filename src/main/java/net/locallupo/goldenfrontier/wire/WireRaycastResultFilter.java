package net.locallupo.goldenfrontier.wire;

import net.minecraft.world.phys.HitResult;

/** Distinguishes an actual block impact from Minecraft's BlockHitResult miss. */
public final class WireRaycastResultFilter {
    private WireRaycastResultFilter() {
    }

    public static boolean isBlockCollision(HitResult result) {
        return result.getType() == HitResult.Type.BLOCK;
    }
}
