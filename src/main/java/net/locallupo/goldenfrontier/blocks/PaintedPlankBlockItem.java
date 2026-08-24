package net.locallupo.goldenfrontier.blocks;

import net.locallupo.goldenfrontier.components.ModComponents;
import net.locallupo.goldenfrontier.components.OriginalPlankComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/** Displays the saved source wood in the name of a painted plank stack. */
public final class PaintedPlankBlockItem extends BlockItem {
    public PaintedPlankBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        OriginalPlankComponent original = stack.get(ModComponents.ORIGINAL_PLANK);
        if (original == null) return super.getName(stack);

        Block source = BuiltInRegistries.BLOCK.getOptional(original.block()).orElse(null);
        if (source == null) return super.getName(stack);
        return super.getName(stack).copy()
                .append(Component.literal(" ("))
                .append(source.getName())
                .append(Component.literal(")"));
    }
}
