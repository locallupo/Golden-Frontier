package net.locallupo.goldenfrontier.items;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

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




    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.COPPER_CHUNK));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.COPPER_DUST));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.COPPER_FLAKE));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.IRON_CHUNK));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.IRON_DUST));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.IRON_FLAKE));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.GOLD_CHUNK));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.GOLD_DUST));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.GOLD_FLAKE));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.SILVER_CHUNK));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.SILVER_DUST));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.SILVER_FLAKE));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.RAW_SILVER));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.SILVER_INGOT));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.SILVER_NUGGET));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.PYRITE_CHUNK));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.PYRITE_DUST));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.PYRITE_FLAKE));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.RAW_PYRITE));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.PYRITE_INGOT));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.PYRITE_NUGGET));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((creativeTab) -> creativeTab.accept(ModItems.QUARTZ_SHARD));
    }
}
