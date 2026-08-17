package net.locallupo.goldenfrontier.crafting;

import net.locallupo.goldenfrontier.GoldenFrontier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ModRecipes {
    private ModRecipes() {}

    public static void initialize() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                GoldenFrontier.id("dyed_plank"), DyedPlankRecipe.SERIALIZER);
    }
}
