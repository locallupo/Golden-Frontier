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

    public record WireState(long revision, List<WireConnection> connections, Optional<Ignition> ignition)
            implements CustomPacketPayload {
        public static final Type<WireState> TYPE = new Type<>(
                Identifier.fromNamespaceAndPath(GoldenFrontier.MOD_ID, "wire_state")
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, WireState> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_LONG, WireState::revision,
                ByteBufCodecs.collection(ArrayList::new, WireConnection.STREAM_CODEC),
                WireState::connections,
                ByteBufCodecs.optional(Ignition.CODEC), WireState::ignition,
                WireState::new
        );

        public WireState {
            connections = List.copyOf(connections);
            ignition = ignition.map(Ignition::copy);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record Ignition(BlockPos detonator, List<WireConnection> connections) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Ignition> CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, Ignition::detonator,
                ByteBufCodecs.collection(ArrayList::new, WireConnection.STREAM_CODEC), Ignition::connections,
                Ignition::new
        );

        public Ignition {
            detonator = detonator.immutable();
            connections = List.copyOf(connections);
        }

        private Ignition copy() {
            return new Ignition(detonator, connections);
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
