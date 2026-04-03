package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VegetationHandler {

    // Track recently replanted positions (BlockPos.asLong -> game time) to prevent accidental breaking
    private static final Map<Long, Long> recentlyPlanted = new ConcurrentHashMap<>();
    private static final int PROTECTION_TICKS = 10;

    public static void register() {
        // Cancel breaking of recently replanted seedlings
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerLevel serverLevel)) return true;
            long key = pos.asLong();
            Long plantedTime = recentlyPlanted.get(key);
            if (plantedTime != null) {
                if (serverLevel.getGameTime() - plantedTime < PROTECTION_TICKS) {
                    return false; // Cancel break — seedling was just planted
                }
                recentlyPlanted.remove(key);
            }
            return true;
        });

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

            if (chance >= 1.0f || player.getRandom().nextFloat() < chance) {
                // Replant on next server tick to ensure block break is fully processed
                BlockState finalSeedState = seedState;
                BlockPos immutablePos = pos.immutable();
                serverLevel.getServer().tell(new TickTask(
                        serverLevel.getServer().getTickCount() + 1, () -> {
                    if (serverLevel.getBlockState(immutablePos).isAir()) {
                        serverLevel.setBlockAndUpdate(immutablePos, finalSeedState);
                        recentlyPlanted.put(immutablePos.asLong(), serverLevel.getGameTime());
                    }
                }));
            }
        });
    }
}
