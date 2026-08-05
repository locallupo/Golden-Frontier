package net.locallupo.goldenfrontier.wire;

import net.locallupo.goldenfrontier.GoldenFrontier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class WirePayloads {
    private WirePayloads() {
    }

    public record Connections(List<WireConnection> connections) implements CustomPacketPayload {
        public static final Type<Connections> TYPE = new Type<>(
                Identifier.fromNamespaceAndPath(GoldenFrontier.MOD_ID, "wire_connections")
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Connections> CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ArrayList::new, WireConnection.STREAM_CODEC),
                Connections::connections,
                Connections::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record Selection(Optional<BlockPos> position) implements CustomPacketPayload {
        public static final Type<Selection> TYPE = new Type<>(
                Identifier.fromNamespaceAndPath(GoldenFrontier.MOD_ID, "wire_selection")
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Selection> CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
                Selection::position,
                Selection::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
