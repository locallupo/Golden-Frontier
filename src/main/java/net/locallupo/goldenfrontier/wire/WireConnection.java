package net.locallupo.goldenfrontier.wire;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record WireConnection(BlockPos first, BlockPos second) {
    public static final Codec<WireConnection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("first").forGetter(WireConnection::first),
            BlockPos.CODEC.fieldOf("second").forGetter(WireConnection::second)
    ).apply(instance, WireConnection::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WireConnection> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, WireConnection::first,
            BlockPos.STREAM_CODEC, WireConnection::second,
            WireConnection::new
    );

    public WireConnection {
        first = first.immutable();
        second = second.immutable();
    }

    public static WireConnection between(BlockPos first, BlockPos second) {
        return first.asLong() <= second.asLong()
                ? new WireConnection(first, second)
                : new WireConnection(second, first);
    }

    public boolean contains(BlockPos pos) {
        return first.equals(pos) || second.equals(pos);
    }
}
