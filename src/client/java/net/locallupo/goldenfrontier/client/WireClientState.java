package net.locallupo.goldenfrontier.client;

import net.locallupo.goldenfrontier.wire.WireConnection;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Optional;

public final class WireClientState {
    private static List<WireConnection> connections = List.of();
    private static Optional<BlockPos> selection = Optional.empty();
    private static Optional<Ignition> ignition = Optional.empty();

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

    public static void startIgnition(BlockPos detonator, List<WireConnection> connections) {
        ignition = Optional.of(new Ignition(detonator.immutable(), System.nanoTime(), List.copyOf(connections)));
    }

    public static Optional<Ignition> ignition() {
        return ignition;
    }

    public static void clearIgnition() {
        ignition = Optional.empty();
    }

    public static void clear() {
        connections = List.of();
        selection = Optional.empty();
        ignition = Optional.empty();
    }

    public record Ignition(BlockPos detonator, long startedAtNanos, List<WireConnection> connections) {
    }
}
