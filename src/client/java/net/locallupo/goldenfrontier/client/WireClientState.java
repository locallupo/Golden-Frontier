package net.locallupo.goldenfrontier.client;

import net.locallupo.goldenfrontier.wire.WireConnection;
import net.locallupo.goldenfrontier.wire.WirePayloads;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class WireClientState {
    private static List<WireConnection> connections = List.of();
    private static final Set<WireConnection> hiddenConnections = new HashSet<>();
    private static long revision = -1L;
    private static Optional<BlockPos> selection = Optional.empty();
    private static Optional<Ignition> ignition = Optional.empty();

    private WireClientState() {
    }

    public static void setConnections(List<WireConnection> value) {
        connections = List.copyOf(value);
        hiddenConnections.retainAll(connections);
    }

    public static void applyState(WirePayloads.WireState state) {
        if (state.revision() <= revision) return;
        revision = state.revision();
        setConnections(state.connections());
        state.ignition().ifPresent(event -> startIgnition(event.detonator(), event.connections().stream()
                .filter(state.connections()::contains)
                .toList()));
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
        hiddenConnections.addAll(connections);
        ignition = Optional.of(new Ignition(detonator.immutable(), System.nanoTime(), List.copyOf(connections)));
    }

    static boolean isHidden(WireConnection connection) {
        return hiddenConnections.contains(connection);
    }

    public static Optional<Ignition> ignition() {
        return ignition;
    }

    public static void clearIgnition() {
        ignition = Optional.empty();
    }

    public static void clear() {
        connections = List.of();
        hiddenConnections.clear();
        revision = -1L;
        selection = Optional.empty();
        ignition = Optional.empty();
    }

    public record Ignition(BlockPos detonator, long startedAtNanos, List<WireConnection> connections) {
    }
}
