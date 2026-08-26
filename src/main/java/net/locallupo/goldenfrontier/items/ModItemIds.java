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

    public static final ResourceKey<Item> GOLD_POCKET_WATCH = create("gold_pocket_watch");

    // SILVER ITEMS

    public static final ResourceKey<Item> SILVER_CHUNK = create("silver_chunk");
    public static final ResourceKey<Item> SILVER_DUST = create("silver_dust");
    public static final ResourceKey<Item> SILVER_FLAkE = create("silver_flake");
    public static final ResourceKey<Item> RAW_SILVER = create("raw_silver");
    public static final ResourceKey<Item> SILVER_INGOT = create("silver_ingot");
    public static final ResourceKey<Item> SILVER_NUGGET = create("silver_nugget");

    public static final ResourceKey<Item> SILVER_POCKET_WATCH = create("silver_pocket_watch");

    // PYRITE ITEMS

    public static final ResourceKey<Item> PYRITE_CHUNK = create("pyrite_chunk");
    public static final ResourceKey<Item> PYRITE_DUST = create("pyrite_dust");
    public static final ResourceKey<Item> PYRITE_FLAKE = create("pyrite_flake");
    public static final ResourceKey<Item> RAW_PYRITE = create("raw_pyrite");
    public static final ResourceKey<Item> PYRITE_INGOT = create("pyrite_ingot");
    public static final ResourceKey<Item> PYRITE_NUGGET = create("pyrite_nugget");

    // OTHER ITEMS

    public static final ResourceKey<Item> QUARTZ_SHARD = create("quartz_shard");

    public static final ResourceKey<Item> WIRE = create("wire");

    public static final ResourceKey<Item> GOLD_PAN = create("gold_pan");

    // COSMETIC ITEMS

    public static final ResourceKey<Item> GAMBLER_HAT = create("gambler_hat");
    public static final ResourceKey<Item> RED_GAMBLER_HAT = create("red_gambler_hat");
    public static final ResourceKey<Item> ORANGE_GAMBLER_HAT = create("orange_gambler_hat");
    public static final ResourceKey<Item> YELLOW_GAMBLER_HAT = create("yellow_gambler_hat");
    public static final ResourceKey<Item> GREEN_GAMBLER_HAT = create("green_gambler_hat");
    public static final ResourceKey<Item> CYAN_GAMBLER_HAT = create("cyan_gambler_hat");
    public static final ResourceKey<Item> PURPLE_GAMBLER_HAT = create("purple_gambler_hat");
    public static final ResourceKey<Item> MAGENTA_GAMBLER_HAT = create("magenta_gambler_hat");
    public static final ResourceKey<Item> WHITE_GAMBLER_HAT = create("white_gambler_hat");
    public static final ResourceKey<Item> BLACK_GAMBLER_HAT = create("black_gambler_hat");

    public static final ResourceKey<Item> CATTLEMAN_HAT = create("cattleman_hat");
    public static final ResourceKey<Item> RED_CATTLEMAN_HAT = create("red_cattleman_hat");
    public static final ResourceKey<Item> ORANGE_CATTLEMAN_HAT = create("orange_cattleman_hat");
    public static final ResourceKey<Item> YELLOW_CATTLEMAN_HAT = create("yellow_cattleman_hat");
    public static final ResourceKey<Item> GREEN_CATTLEMAN_HAT = create("green_cattleman_hat");
    public static final ResourceKey<Item> CYAN_CATTLEMAN_HAT = create("cyan_cattleman_hat");
    public static final ResourceKey<Item> PURPLE_CATTLEMAN_HAT = create("purple_cattleman_hat");
    public static final ResourceKey<Item> MAGENTA_CATTLEMAN_HAT = create("magenta_cattleman_hat");
    public static final ResourceKey<Item> WHITE_CATTLEMAN_HAT = create("white_cattleman_hat");
    public static final ResourceKey<Item> BLACK_CATTLEMAN_HAT = create("black_cattleman_hat");

    public static final ResourceKey<Item> NEVADA_HAT = create("nevada_hat");
    public static final ResourceKey<Item> RED_NEVADA_HAT = create("red_nevada_hat");
    public static final ResourceKey<Item> ORANGE_NEVADA_HAT = create("orange_nevada_hat");
    public static final ResourceKey<Item> YELLOW_NEVADA_HAT = create("yellow_nevada_hat");
    public static final ResourceKey<Item> GREEN_NEVADA_HAT = create("green_nevada_hat");
    public static final ResourceKey<Item> CYAN_NEVADA_HAT = create("cyan_nevada_hat");
    public static final ResourceKey<Item> PURPLE_NEVADA_HAT = create("purple_nevada_hat");
    public static final ResourceKey<Item> MAGENTA_NEVADA_HAT = create("magenta_nevada_hat");
    public static final ResourceKey<Item> WHITE_NEVADA_HAT = create("white_nevada_hat");
    public static final ResourceKey<Item> BLACK_NEVADA_HAT = create("black_nevada_hat");

}
