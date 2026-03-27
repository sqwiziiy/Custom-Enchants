package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;

public class VegetationHandler {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (!(world instanceof ServerLevel serverLevel)) return;
            if (!ModConfig.get().vegetationEnabled) return;

            ItemStack tool = player.getMainHandItem();
            if (tool.isEmpty()) return;
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.VEGETATION, tool);
            if (level <= 0) return;

            Block block = state.getBlock();

            // Check if this is a mature crop or nether wart
            boolean isMatureCrop = false;
            BlockState seedState = null;

            if (block instanceof CropBlock cropBlock) {
                if (cropBlock.isMaxAge(state)) {
                    isMatureCrop = true;
                    seedState = cropBlock.getStateForAge(0);
                }
            } else if (block instanceof NetherWartBlock) {
                int age = state.getValue(NetherWartBlock.AGE);
                if (age >= 3) {
                    isMatureCrop = true;
                    seedState = block.defaultBlockState();
                }
            }

            if (!isMatureCrop || seedState == null) return;

            float chance = switch (level) {
                case 1 -> ModConfig.get().vegetationChanceL1 / 100f;
                case 2 -> ModConfig.get().vegetationChanceL2 / 100f;
                case 3 -> ModConfig.get().vegetationChanceL3 / 100f;
                default -> ModConfig.get().vegetationChanceL1 / 100f;
            };

            if (player.getRandom().nextFloat() < chance) {
                // Replant on next tick to avoid conflict with block break
                BlockState finalSeedState = seedState;
                serverLevel.getServer().execute(() -> {
                    if (serverLevel.getBlockState(pos).isAir()) {
                        serverLevel.setBlockAndUpdate(pos, finalSeedState);
                    }
                });
            }
        });
    }
}
