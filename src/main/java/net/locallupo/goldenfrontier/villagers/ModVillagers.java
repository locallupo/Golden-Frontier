package net.locallupo.goldenfrontier.villagers;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.locallupo.goldenfrontier.GoldenFrontier;
import net.locallupo.goldenfrontier.blocks.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.Blocks;

public final class ModVillagers {
    public static final PoiType PROSPECTOR_POI = PoiHelper.register(
            GoldenFrontier.id("prospector"),
            1,
            1,
            Blocks.FURNACE
    );

    public static final ResourceKey<VillagerProfession> PROSPECTOR_KEY =
            ResourceKey.create(Registries.VILLAGER_PROFESSION, GoldenFrontier.id("prospector"));

    public static final VillagerProfession PROSPECTOR = Registry.register(
            BuiltInRegistries.VILLAGER_PROFESSION,
            PROSPECTOR_KEY,
            new VillagerProfession(
                    Component.translatable("entity.minecraft.villager.golden-frontier.prospector"),
                    holder -> holder.value() == PROSPECTOR_POI,
                    holder -> holder.value() == PROSPECTOR_POI,
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_MASON,
                    tradeSets()
            )
    );

    private ModVillagers() {
    }

    private static Int2ObjectOpenHashMap<ResourceKey<TradeSet>> tradeSets() {
        Int2ObjectOpenHashMap<ResourceKey<TradeSet>> trades = new Int2ObjectOpenHashMap<>();
        for (int level = 1; level <= 5; level++) {
            trades.put(level, ResourceKey.create(
                    Registries.TRADE_SET,
                    GoldenFrontier.id("prospector/level_" + level)
            ));
        }
        return trades;
    }

    public static void initialize() {
        // Class loading performs the POI and profession registration.
    }
}
