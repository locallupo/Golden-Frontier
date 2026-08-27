package net.locallupo.goldenfrontier.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.locallupo.goldenfrontier.GoldenFrontier;

public class GoldPanItem extends Item {
    private static final ResourceKey<LootTable> GOLD_PAN_LOOT_TABLE = ResourceKey.create(
            Registries.LOOT_TABLE,
            GoldenFrontier.id("gameplay/gold_pan")
    );

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
            // Swing on both sides so the hand animation is visible immediately.
            player.swing(hand, true);

            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            ServerLevel serverLevel = (ServerLevel) level;
            level.playSound(null, pos, SoundEvents.BRUSH_GRAVEL, SoundSource.PLAYERS, 0.9F, 1.0F);
            serverLevel.sendParticles(
                    ParticleTypes.SPLASH,
                    pos.getX() + 0.5D,
                    pos.getY() + 0.9D,
                    pos.getZ() + 0.5D,
                    8,
                    0.25D,
                    0.08D,
                    0.25D,
                    0.15D
            );

            LootParams lootParams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withParameter(LootContextParams.TOOL, itemStack)
                    .create(LootContextParamSets.FISHING);

            LootTable lootTable = serverLevel.getServer()
                    .reloadableRegistries()
                    .getLootTable(GOLD_PAN_LOOT_TABLE);

            lootTable.getRandomItems(lootParams).forEach(reward -> {
                if (!player.getInventory().add(reward)) {
                    player.drop(reward, false);
                }
            });

            itemStack.hurtAndBreak(1, player, hand);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
