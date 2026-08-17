package net.locallupo.goldenfrontier.components;

import net.locallupo.goldenfrontier.GoldenFrontier;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ModComponents {
    private ModComponents() {}

    public static final DataComponentType<OriginalPlankComponent> ORIGINAL_PLANK =
            DataComponentType.<OriginalPlankComponent>builder()
                    .persistent(OriginalPlankComponent.CODEC)
                    .build();

    public static void initialize() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                GoldenFrontier.id("original_plank"), ORIGINAL_PLANK);
    }
}
