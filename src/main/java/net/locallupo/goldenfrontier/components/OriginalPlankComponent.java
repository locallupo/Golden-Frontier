package net.locallupo.goldenfrontier.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Map;

public record OriginalPlankComponent(Identifier block, Map<String, String> properties) {
    public static final Codec<OriginalPlankComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("block").forGetter(OriginalPlankComponent::block),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("properties", Map.of())
                    .forGetter(OriginalPlankComponent::properties)
    ).apply(instance, OriginalPlankComponent::new));
}
