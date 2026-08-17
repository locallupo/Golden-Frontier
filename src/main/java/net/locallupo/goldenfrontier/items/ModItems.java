package net.locallupo.goldenfrontier.items;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.locallupo.goldenfrontier.GoldenFrontier;
import net.locallupo.goldenfrontier.blocks.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class ModItems {

    public static Item register(ResourceKey<Item> itemKey, Function<Item.Properties, Item> itemFactory, Item.Properties settings){
        Item item = itemFactory.apply(settings.setId(itemKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    // COPPER ITEMS

    public static final Item COPPER_CHUNK = register(ModItemIds.COPPER_CHUNK, Item::new, new Item.Properties());
    public static final Item COPPER_DUST = register(ModItemIds.COPPER_DUST, Item::new, new Item.Properties());
    public static final Item COPPER_FLAKE = register(ModItemIds.COPPER_FLAKE, Item::new, new Item.Properties());

    // IRON ITEMS

    public static final Item IRON_CHUNK = register(ModItemIds.IRON_CHUNK, Item::new, new Item.Properties());
    public static final Item IRON_DUST = register(ModItemIds.IRON_DUST, Item::new, new Item.Properties());
    public static final Item IRON_FLAKE = register(ModItemIds.IRON_FLAKE, Item::new, new Item.Properties());

    // GOLD ITEMS

    public static final Item GOLD_CHUNK = register(ModItemIds.GOLD_CHUNK, Item::new, new Item.Properties());
    public static final Item GOLD_DUST = register(ModItemIds.GOLD_DUST, Item::new, new Item.Properties());
    public static final Item GOLD_FLAKE = register(ModItemIds.GOLD_FLAKE, Item::new, new Item.Properties());

    // SILVER ITEMS

    public static final Item SILVER_CHUNK = register(ModItemIds.SILVER_CHUNK, Item::new, new Item.Properties());
    public static final Item SILVER_DUST = register(ModItemIds.SILVER_DUST, Item::new, new Item.Properties());
    public static final Item SILVER_FLAKE = register(ModItemIds.SILVER_FLAkE, Item::new, new Item.Properties());
    public static final Item RAW_SILVER = register(ModItemIds.RAW_SILVER, Item::new, new Item.Properties());
    public static final Item SILVER_INGOT = register(ModItemIds.SILVER_INGOT, Item::new, new Item.Properties());
    public static final Item SILVER_NUGGET = register(ModItemIds.SILVER_NUGGET, Item::new, new Item.Properties());

    // PYRITE ITEMS

    public static final Item PYRITE_CHUNK = register(ModItemIds.PYRITE_CHUNK, Item::new, new Item.Properties());
    public static final Item PYRITE_DUST = register(ModItemIds.PYRITE_DUST, Item::new, new Item.Properties());
    public static final Item PYRITE_FLAKE = register(ModItemIds.PYRITE_FLAkE, Item::new, new Item.Properties());
    public static final Item RAW_PYRITE = register(ModItemIds.RAW_PYRITE, Item::new, new Item.Properties());
    public static final Item PYRITE_INGOT = register(ModItemIds.PYRITE_INGOT, Item::new, new Item.Properties());
    public static final Item PYRITE_NUGGET = register(ModItemIds.PYRITE_NUGGET, Item::new, new Item.Properties());

    // OTHER ITEMS

    public static final Item QUARTZ_SHARD = register(ModItemIds.QUARTZ_SHARD, Item::new, new Item.Properties());
    public static final Item WIRE = register(ModItemIds.WIRE, WireItem::new, new Item.Properties());

    // COSMETIC ITEMS

    public static final Item GAMBLER_HAT = register(ModItemIds.GAMBLER_HAT, HatItem::new, new Item.Properties());
    public static final Item RED_GAMBLER_HAT = register(ModItemIds.RED_GAMBLER_HAT, HatItem::new, new Item.Properties());
    public static final Item ORANGE_GAMBLER_HAT = register(ModItemIds.ORANGE_GAMBLER_HAT, HatItem::new, new Item.Properties());
    public static final Item YELLOW_GAMBLER_HAT = register(ModItemIds.YELLOW_GAMBLER_HAT, HatItem::new, new Item.Properties());
    public static final Item GREEN_GAMBLER_HAT = register(ModItemIds.GREEN_GAMBLER_HAT, HatItem::new, new Item.Properties());
    public static final Item CYAN_GAMBLER_HAT = register(ModItemIds.CYAN_GAMBLER_HAT, HatItem::new, new Item.Properties());
    public static final Item PURPLE_GAMBLER_HAT = register(ModItemIds.PURPLE_GAMBLER_HAT, HatItem::new, new Item.Properties());
    public static final Item MAGENTA_GAMBLER_HAT = register(ModItemIds.MAGENTA_GAMBLER_HAT, HatItem::new, new Item.Properties());
    public static final Item WHITE_GAMBLER_HAT = register(ModItemIds.WHITE_GAMBLER_HAT, HatItem::new, new Item.Properties());
    public static final Item BLACK_GAMBLER_HAT = register(ModItemIds.BLACK_GAMBLER_HAT, HatItem::new, new Item.Properties());

    public static final Item MASK = register(ModItemIds.MASK, HatItem::new, new Item.Properties());
    public static final Item RED_MASK = register(ModItemIds.RED_MASK, HatItem::new, new Item.Properties());
    public static final Item ORANGE_MASK = register(ModItemIds.ORANGE_MASK, HatItem::new, new Item.Properties());
    public static final Item YELLOW_MASK = register(ModItemIds.YELLOW_MASK, HatItem::new, new Item.Properties());
    public static final Item GREEN_MASK = register(ModItemIds.GREEN_MASK, HatItem::new, new Item.Properties());
    public static final Item CYAN_MASK = register(ModItemIds.CYAN_MASK, HatItem::new, new Item.Properties());
    public static final Item PURPLE_MASK = register(ModItemIds.PURPLE_MASK, HatItem::new, new Item.Properties());
    public static final Item MAGENTA_MASK = register(ModItemIds.MAGENTA_MASK, HatItem::new, new Item.Properties());
    public static final Item BLACK_MASK = register(ModItemIds.BLACK_MASK, HatItem::new, new Item.Properties());





    // CREATIVE TAB EVENTS (THIS GETS MESSY, TRY TO KEEP THEM ORGANISED)
    // ADD ITEMS TO MOD TAB AND ANY OTHER TABS THEY WOULD FIT WELL IN


    public static void initialize() {

        // INGREDIENTS

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> {
                    entries.accept(ModItems.COPPER_DUST);
                    entries.accept(ModItems.COPPER_FLAKE);
                    entries.accept(ModItems.COPPER_CHUNK);

                    entries.accept(ModItems.IRON_DUST);
                    entries.accept(ModItems.IRON_FLAKE);
                    entries.accept(ModItems.IRON_CHUNK);

                    entries.accept(ModItems.GOLD_DUST);
                    entries.accept(ModItems.GOLD_FLAKE);
                    entries.accept(ModItems.GOLD_CHUNK);

                    entries.accept(ModItems.SILVER_DUST);
                    entries.accept(ModItems.SILVER_FLAKE);
                    entries.accept(ModItems.SILVER_CHUNK);
                    entries.accept(ModItems.RAW_SILVER);
                    entries.accept(ModItems.SILVER_INGOT);
                    entries.accept(ModItems.SILVER_NUGGET);

                    entries.accept(ModItems.PYRITE_DUST);
                    entries.accept(ModItems.PYRITE_NUGGET);
                    entries.accept(ModItems.PYRITE_CHUNK);
                    entries.accept(ModItems.RAW_PYRITE);
                    entries.accept(ModItems.PYRITE_INGOT);
                    entries.accept(ModItems.PYRITE_NUGGET);

                    entries.accept(ModItems.QUARTZ_SHARD);
                });

        // TOOLS

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> {
                    entries.accept(ModItems.WIRE);
                });


    }
}
