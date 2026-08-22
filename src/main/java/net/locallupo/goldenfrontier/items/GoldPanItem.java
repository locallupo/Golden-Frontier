package net.locallupo.goldenfrontier.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class GoldPanItem extends Item {
    public GoldPanItem(Properties properties) {
        super(properties.stacksTo(1).durability(128));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        ItemStack itemStack = player.getItemInHand(hand);

        BlockHitResult hitResult = getPlayerPOVHitResult(
                level,
                player,
                ClipContext.Fluid.SOURCE_ONLY
        );

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        BlockPos pos = hitResult.getBlockPos();

        if (level.getFluidState(pos).isSource()) {
            System.out.println("PAN USED ON WATER SOURCE");

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
