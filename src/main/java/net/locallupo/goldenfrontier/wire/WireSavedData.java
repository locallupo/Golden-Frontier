package net.locallupo.goldenfrontier.wire;

import com.mojang.serialization.Codec;
import net.locallupo.goldenfrontier.GoldenFrontier;
import net.locallupo.goldenfrontier.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.Deque;

public class WireSavedData extends SavedData {
    private static final SavedDataType<WireSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(GoldenFrontier.MOD_ID, "wire_connections"),
            WireSavedData::new,
            WireConnection.CODEC.listOf().xmap(WireSavedData::new, data -> List.copyOf(data.connections)),
            null
    );

    private final Set<WireConnection> connections = new LinkedHashSet<>();

    public WireSavedData() {
    }

    public WireSavedData(List<WireConnection> connections) {
        this.connections.addAll(connections);
    }

    public static WireSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public List<WireConnection> connections() {
        return List.copyOf(connections);
    }

    public boolean add(BlockPos first, BlockPos second) {
        boolean added = connections.add(WireConnection.between(first, second));
        if (added) {
            setDirty();
        }
        return added;
    }

    public boolean removeAllAt(BlockPos pos) {
        boolean changed = connections.removeIf(connection -> connection.contains(pos));
        if (changed) {
            setDirty();
        }
        return changed;
    }

    /** Returns dynamite reachable from a detonator through the connected wire network. */
    public List<BlockPos> connectedDynamite(ServerLevel level, BlockPos detonator) {
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> pending = new ArrayDeque<>();
        List<BlockPos> dynamite = new ArrayList<>();
        pending.add(detonator);

        while (!pending.isEmpty()) {
            BlockPos current = pending.removeFirst();
            if (!visited.add(current)) {
                continue;
            }

            if (!current.equals(detonator) && level.getBlockState(current).is(ModBlocks.DYNAMITE)) {
                dynamite.add(current.immutable());
            }

            for (WireConnection connection : connections) {
                if (connection.first().equals(current)) {
                    pending.addLast(connection.second());
                } else if (connection.second().equals(current)) {
                    pending.addLast(connection.first());
                }
            }
        }

        return dynamite;
    }

    public boolean pruneInvalid(ServerLevel level) {
        boolean changed = connections.removeIf(connection ->
                !isEndpoint(level, connection.first()) || !isEndpoint(level, connection.second()));
        if (changed) {
            setDirty();
        }
        return changed;
    }

    private static boolean isEndpoint(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.DETONATOR)
                || level.getBlockState(pos).is(ModBlocks.DYNAMITE);
    }
}
