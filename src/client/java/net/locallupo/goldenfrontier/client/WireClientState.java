package net.locallupo.goldenfrontier.client;

import net.locallupo.goldenfrontier.wire.WireConnection;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Optional;

public final class WireClientState {
    private static List<WireConnection> connections = List.of();
    private static Optional<BlockPos> selection = Optional.empty();

    private WireClientState() {
    }

    public static void setConnections(List<WireConnection> value) {
        connections = List.copyOf(value);
    }

    public static List<WireConnection> connections() {
        return connections;
    }

    public static void setSelection(Optional<BlockPos> value) {
        selection = value.map(BlockPos::immutable);
    }

    public static Optional<BlockPos> selection() {
        return selection;
    }

    public static void clear() {
        connections = List.of();
        selection = Optional.empty();
    }
}
