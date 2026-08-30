package net.locallupo.goldenfrontier.client;

import net.locallupo.goldenfrontier.wire.WireConnection;
import net.locallupo.goldenfrontier.wire.WirePayloads;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WireIgnitionAnimatorTest {
    private static final WireConnection CONNECTION = WireConnection.between(
            new BlockPos(0, 1, 0), new BlockPos(3, 1, 0));
    private static final List<Vec3> PATH = List.of(new Vec3(0, 0, 0), new Vec3(3, 0, 0));

    @AfterEach
    void clearState() {
        WireClientState.clear();
    }

    @Test
    void progressUsesTheDetonatorSideAndClock() {
        WireClientState.startIgnition(CONNECTION.first(), List.of(CONNECTION));
        long started = WireClientState.ignition().orElseThrow().startedAtNanos();
        assertTrue(WireClientState.isHidden(CONNECTION));
        assertTrue(WireIgnitionAnimator.progress(CONNECTION, PATH, started - 1L).orElseThrow().progress() <= 0.0);
        assertTrue(WireIgnitionAnimator.progress(CONNECTION, PATH, started + 100_000_000L).orElseThrow().progress() > 0.0);
        assertEquals(1.0, WireIgnitionAnimator.progress(CONNECTION, PATH,
                started + 1_000_000_000L).orElseThrow().progress());
        assertTrue(WireIgnitionAnimator.expired(started + 1_000_000_000L));

        WireClientState.startIgnition(CONNECTION.second(), List.of(CONNECTION));
        assertTrue(!WireIgnitionAnimator.progress(CONNECTION, PATH,
                WireClientState.ignition().orElseThrow().startedAtNanos() + 100_000_000L)
                .orElseThrow().fromFirst());

        WireClientState.setConnections(List.of());
        assertTrue(!WireClientState.isHidden(CONNECTION));
    }

    @Test
    void slicesAPathAtDistanceBoundaries() {
        assertEquals(List.of(new Vec3(1, 0, 0), new Vec3(3, 0, 0)),
                WireIgnitionAnimator.slice(PATH, 1.0, 3.0));
        assertEquals(3.0, WireIgnitionAnimator.length(PATH));
    }

    @Test
    void newerAuthoritativeStateCannotBeReplacedByAnOlderSnapshot() {
        WireClientState.applyState(new WirePayloads.WireState(4, List.of(CONNECTION),
                Optional.of(new WirePayloads.Ignition(CONNECTION.first(), List.of(CONNECTION)))));
        assertEquals(List.of(CONNECTION), WireClientState.connections());
        assertTrue(WireClientState.isHidden(CONNECTION));

        WireClientState.applyState(new WirePayloads.WireState(5, List.of(), Optional.empty()));
        assertTrue(WireClientState.connections().isEmpty());
        assertTrue(!WireClientState.isHidden(CONNECTION));

        WireClientState.applyState(new WirePayloads.WireState(4, List.of(CONNECTION), Optional.empty()));
        assertTrue(WireClientState.connections().isEmpty());
    }

    @Test
    void ignitionCannotRenderAConnectionRemovedByTheAuthoritativeSnapshot() {
        WireConnection incoming = WireConnection.between(new BlockPos(-3, 1, 0), CONNECTION.first());
        WireClientState.applyState(new WirePayloads.WireState(6, List.of(CONNECTION),
                Optional.of(new WirePayloads.Ignition(CONNECTION.first(), List.of(incoming, CONNECTION)))));

        assertEquals(List.of(CONNECTION), WireClientState.ignition().orElseThrow().connections());
    }
}
