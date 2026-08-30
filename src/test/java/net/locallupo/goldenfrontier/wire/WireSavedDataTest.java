package net.locallupo.goldenfrontier.wire;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WireSavedDataTest {
    @Test
    void ignitionOnlySelectsConnectionsTowardNextDynamite() {
        BlockPos previous = new BlockPos(0, 1, 0);
        BlockPos current = new BlockPos(1, 1, 0);
        BlockPos next = new BlockPos(2, 1, 0);
        WireConnection incoming = WireConnection.between(previous, current);
        WireConnection outgoing = WireConnection.between(current, next);

        WireSavedData data = new WireSavedData(List.of(incoming, outgoing));

        assertEquals(List.of(outgoing), data.connectionsToward(current, List.of(next)));
    }
}
