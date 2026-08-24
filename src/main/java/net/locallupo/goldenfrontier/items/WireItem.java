package net.locallupo.goldenfrontier.items;

import net.locallupo.goldenfrontier.blocks.ModBlocks;
import net.locallupo.goldenfrontier.GoldenFrontier;
import net.locallupo.goldenfrontier.wire.WireNetworking;
import net.locallupo.goldenfrontier.wire.WireSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WireItem extends Item {
    private static final Map<UUID, PendingSelection> PENDING = new HashMap<>();

    public WireItem(Properties properties) {
        super(properties);
    }

    public static void clearSelection(UUID playerId) {
        PENDING.remove(playerId);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();

        if (!isEndpoint(level, clickedPos)) {
            GoldenFrontier.LOGGER.info("Wire click ignored at {}: {} is not a detonator or dynamite", clickedPos,
                    level.getBlockState(clickedPos).getBlock());
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        UUID playerId = serverPlayer.getUUID();
        PendingSelection pending = PENDING.get(playerId);
        GoldenFrontier.LOGGER.info("Wire click: player={}, endpoint={} at {}, pending={}", serverPlayer.getName().getString(),
                level.getBlockState(clickedPos).getBlock(), clickedPos, pending == null ? "none" : pending.position());
        if (serverPlayer.isShiftKeyDown()) {
            PENDING.remove(playerId);
            ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
            boolean removed = WireSavedData.get(serverLevel).removeAllAt(clickedPos);
            WireNetworking.sendSelection(serverPlayer, java.util.Optional.empty());
            if (removed) {
                WireNetworking.broadcast(serverLevel);
                return InteractionResult.SUCCESS_SERVER;
            }
            return InteractionResult.PASS;
        }

        if (pending == null || !pending.dimension().equals(level.dimension())) {
            PENDING.put(playerId, new PendingSelection(clickedPos.immutable(), level.dimension()));
            WireNetworking.sendSelection(serverPlayer, java.util.Optional.of(clickedPos.immutable()));
            GoldenFrontier.LOGGER.info("Wire selection started at {}", clickedPos);
            return InteractionResult.SUCCESS_SERVER;
        }

        if (pending.position().equals(clickedPos)) {
            PENDING.remove(playerId);
            WireNetworking.sendSelection(serverPlayer, java.util.Optional.empty());
            GoldenFrontier.LOGGER.info("Wire selection cancelled at {}", clickedPos);
            return InteractionResult.SUCCESS_SERVER;
        }

        ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
        WireSavedData data = WireSavedData.get(serverLevel);
        boolean added = data.add(pending.position(), clickedPos);
        if (added) {
            if (!serverPlayer.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
            WireNetworking.broadcast(serverLevel);
        }
        GoldenFrontier.LOGGER.info("Wire connection {} -> {}: added={}, totalConnections={}",
                pending.position(), clickedPos, added, data.connections().size());

        PENDING.remove(playerId);
        WireNetworking.sendSelection(serverPlayer, java.util.Optional.empty());
        return InteractionResult.SUCCESS_SERVER;
    }

    private static boolean isEndpoint(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.DETONATOR)
                || level.getBlockState(pos).is(ModBlocks.DYNAMITE);
    }

    private record PendingSelection(BlockPos position, ResourceKey<Level> dimension) {
    }
}
