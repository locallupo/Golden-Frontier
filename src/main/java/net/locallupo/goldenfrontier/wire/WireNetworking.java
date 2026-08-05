package net.locallupo.goldenfrontier.wire;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.locallupo.goldenfrontier.items.WireItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WireNetworking {
    private static final Map<UUID, ResourceKey<Level>> PLAYER_DIMENSIONS = new HashMap<>();

    private WireNetworking() {
    }

    public static void initialize() {
        PayloadTypeRegistry.clientboundPlay().register(WirePayloads.Connections.TYPE, WirePayloads.Connections.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(WirePayloads.Selection.TYPE, WirePayloads.Selection.CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> syncPlayer(handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PLAYER_DIMENSIONS.remove(handler.getPlayer().getUUID());
            WireItem.clearSelection(handler.getPlayer().getUUID());
        });

        ServerTickEvents.END_SERVER_TICK.register(WireNetworking::tickServer);
        ServerTickEvents.END_LEVEL_TICK.register(WireNetworking::tickLevel);
    }

    public static void syncPlayer(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        ServerPlayNetworking.send(player, new WirePayloads.Connections(WireSavedData.get(level).connections()));
    }

    public static void broadcast(ServerLevel level) {
        WirePayloads.Connections payload = new WirePayloads.Connections(WireSavedData.get(level).connections());
        for (ServerPlayer player : PlayerLookup.level(level)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendSelection(ServerPlayer player, java.util.Optional<net.minecraft.core.BlockPos> position) {
        ServerPlayNetworking.send(player, new WirePayloads.Selection(position));
    }

    private static void tickLevel(ServerLevel level) {
        if (WireSavedData.get(level).pruneInvalid(level)) {
            broadcast(level);
        }
    }

    private static void tickServer(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ResourceKey<Level> dimension = ((ServerLevel) player.level()).dimension();
            ResourceKey<Level> previous = PLAYER_DIMENSIONS.put(player.getUUID(), dimension);
            if (previous != null && previous != dimension) {
                syncPlayer(player);
            }
        }
    }
}
