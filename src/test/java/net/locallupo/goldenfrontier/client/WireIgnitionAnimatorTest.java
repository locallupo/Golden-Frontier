package net.locallupo.goldenfrontier.client;

import net.locallupo.goldenfrontier.wire.WireConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WireIgnitionAnimatorTest {
    private static final WireConnection CONNECTION = WireConnection.between(
            new BlockPos(0, 1, 0), new BlockPos(3, 1, 0));
    private static final List<Vec3> PATH = List.of(new Vec3(0, 0, 0), new Vec3(3, 0, 0));

    @AfterEach
    void clearState() {
        WireClientState.clearIgnition();
    }

    @Test
    void progressUsesTheDetonatorSideAndClock() {
        WireClientState.startIgnition(CONNECTION.first(), List.of(CONNECTION));
        long started = WireClientState.ignition().orElseThrow().startedAtNanos();
        assertTrue(WireIgnitionAnimator.progress(CONNECTION, PATH, started - 1L).orElseThrow().progress() <= 0.0);
        assertTrue(WireIgnitionAnimator.progress(CONNECTION, PATH, started + 100_000_000L).orElseThrow().progress() > 0.0);
        assertTrue(WireIgnitionAnimator.progress(CONNECTION, PATH, started + 1_000_000_000L).isEmpty());

        WireClientState.startIgnition(CONNECTION.second(), List.of(CONNECTION));
        assertTrue(!WireIgnitionAnimator.progress(CONNECTION, PATH,
                WireClientState.ignition().orElseThrow().startedAtNanos() + 100_000_000L)
                .orElseThrow().fromFirst());
    }

    @Test
    void slicesAPathAtDistanceBoundaries() {
        assertEquals(List.of(new Vec3(1, 0, 0), new Vec3(3, 0, 0)),
                WireIgnitionAnimator.slice(PATH, 1.0, 3.0));
        assertEquals(3.0, WireIgnitionAnimator.length(PATH));
    }
}
