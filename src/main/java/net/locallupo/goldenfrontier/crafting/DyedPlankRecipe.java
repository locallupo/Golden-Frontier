package net.locallupo.goldenfrontier.crafting;

import com.mojang.serialization.Codec;
import net.locallupo.goldenfrontier.blocks.PaintedPlanksBlock;
import net.locallupo.goldenfrontier.components.ModComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class DyedPlankRecipe implements CraftingRecipe {
    public static final RecipeSerializer<DyedPlankRecipe> SERIALIZER = new RecipeSerializer<>(
            Codec.BOOL.fieldOf("surrounded").xmap(DyedPlankRecipe::new, recipe -> recipe.surrounded),
            StreamCodec.of(
                    (net.minecraft.network.RegistryFriendlyByteBuf buffer, DyedPlankRecipe recipe) -> buffer.writeBoolean(recipe.surrounded),
                    buffer -> new DyedPlankRecipe(buffer.readBoolean())));

    private final boolean surrounded;

    public DyedPlankRecipe() {
        this(false);
    }

    public DyedPlankRecipe(boolean surrounded) {
        this.surrounded = surrounded;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (surrounded) {
            if (input.width() != 3 || input.height() != 3 || dyeColor(input.getItem(1, 1)) == null) return false;
            BlockItem source = null;
            for (int i = 0; i < input.size(); i++) {
                if (i == 4) continue;
                if (!isSourcePlank(input.getItem(i))) return false;
                BlockItem current = (BlockItem) input.getItem(i).getItem();
                if (source == null) source = current;
                else if (source.getBlock() != current.getBlock()) return false;
            }
            return true;
        }

        int planks = 0;
        int dyes = 0;
        for (ItemStack stack : input.items()) {
            if (isSourcePlank(stack)) planks++;
            else if (dyeColor(stack) != null) dyes++;
            else if (!stack.isEmpty()) return false;
        }
        return planks == 1 && dyes == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack source = input.items().stream().filter(DyedPlankRecipe::isSourcePlank)
                .findFirst().orElse(ItemStack.EMPTY);
        DyeColor color = input.items().stream().map(DyedPlankRecipe::dyeColor)
                .filter(Objects::nonNull).findFirst().orElse(DyeColor.WHITE);
        if (!(source.getItem() instanceof BlockItem sourceItem)) return ItemStack.EMPTY;

        ItemStack result = new ItemStack(PaintedPlanksBlock.block(color).asItem(), surrounded ? 8 : 1);
        result.set(ModComponents.ORIGINAL_PLANK, PaintedPlanksBlock.component(sourceItem.getBlock().defaultBlockState()));
        return result;
    }

    private static boolean isSourcePlank(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock().defaultBlockState().is(BlockTags.PLANKS)
                && blockItem.getBlock() != Blocks.BAMBOO_PLANKS
                && PaintedPlanksBlock.color(blockItem.getBlock()) == null;
    }

    private static DyeColor dyeColor(ItemStack stack) {
        if (stack.isEmpty()) return null;
        for (DyeColor color : DyeColor.values()) if (stack.is(Items.DYE.pick(color))) return color;
        return null;
    }

    @Override public boolean isSpecial() { return true; }
    @Override public boolean showNotification() { return false; }
    @Override public String group() { return "painted_planks"; }
    @Override public RecipeSerializer<? extends CraftingRecipe> getSerializer() { return SERIALIZER; }
    @Override public net.minecraft.world.item.crafting.CraftingBookCategory category() { return net.minecraft.world.item.crafting.CraftingBookCategory.BUILDING; }
    @Override public RecipeType<net.minecraft.world.item.crafting.CraftingRecipe> getType() { return RecipeType.CRAFTING; }
    @Override public net.minecraft.world.item.crafting.PlacementInfo placementInfo() { return net.minecraft.world.item.crafting.PlacementInfo.NOT_PLACEABLE; }
}
