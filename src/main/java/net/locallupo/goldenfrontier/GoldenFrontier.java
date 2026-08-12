package net.locallupo.goldenfrontier;

import net.fabricmc.api.ModInitializer;

import net.locallupo.goldenfrontier.blocks.ModBlocks;
import net.locallupo.goldenfrontier.items.ModItemGroup;
import net.locallupo.goldenfrontier.items.ModItems;
import net.locallupo.goldenfrontier.wire.WireNetworking;
import net.locallupo.goldenfrontier.world.OreGeneration;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoldenFrontier implements ModInitializer {
	public static final String MOD_ID = "golden-frontier";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Vote Golden Frontier for the 2026 CurseForge Modjam!");
		WireNetworking.initialize();
		ModItemGroup.initialize();
		ModItems.initialize();
		ModBlocks.initialize();
		OreGeneration.initialize();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
