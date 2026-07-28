package net.locallupo.goldenfrontier.items;

import net.locallupo.goldenfrontier.GoldenFrontier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {

    public static ResourceKey<Item> create(String name){
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(GoldenFrontier.MOD_ID, name));
    }

    // COPPER ITEMS

    public static final ResourceKey<Item> COPPER_CHUNK = create("copper_chunk");
    public static final ResourceKey<Item> COPPER_DUST = create("copper_dust");
    public static final ResourceKey<Item> COPPER_FLAKE = create("copper_flake");

    // IRON ITEMS

    public static final ResourceKey<Item> IRON_CHUNK = create("iron_chunk");
    public static final ResourceKey<Item> IRON_DUST = create("iron_dust");
    public static final ResourceKey<Item> IRON_FLAKE = create("iron_flake");

    // GOLD ITEMS

    public static final ResourceKey<Item> GOLD_CHUNK = create("gold_chunk");
    public static final ResourceKey<Item> GOLD_DUST = create("gold_dust");
    public static final ResourceKey<Item> GOLD_FLAKE = create("gold_flake");

    // SILVER ITEMS

    public static final ResourceKey<Item> SILVER_CHUNK = create("silver_chunk");
    public static final ResourceKey<Item> SILVER_DUST = create("silver_dust");
    public static final ResourceKey<Item> SILVER_FLAkE = create("silver_flake");
    public static final ResourceKey<Item> RAW_SILVER = create("raw_silver");
    public static final ResourceKey<Item> SILVER_INGOT = create("silver_ingot");
    public static final ResourceKey<Item> SILVER_NUGGET = create("silver_nugget");

    // PYRITE ITEMS

    public static final ResourceKey<Item> PYRITE_CHUNK = create("pyrite_chunk");
    public static final ResourceKey<Item> PYRITE_DUST = create("pyrite_dust");
    public static final ResourceKey<Item> PYRITE_FLAkE = create("pyrite_flake");
    public static final ResourceKey<Item> RAW_PYRITE = create("raw_pyrite");
    public static final ResourceKey<Item> PYRITE_INGOT = create("pyrite_ingot");
    public static final ResourceKey<Item> PYRITE_NUGGET = create("pyrite_nugget");

    // OTHER ITEMS

    public static final ResourceKey<Item> QUARTZ_SHARD = create("quartz_shard");
}
