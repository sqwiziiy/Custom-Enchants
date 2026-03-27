package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MagnetHandler {

    public static void collectNearby(ServerLevel level, ServerPlayer player, BlockPos pos) {
        int radius = ModConfig.get().magnetRadius;
        level.getServer().execute(() -> {
            if (!player.isAlive()) return;
            AABB area = new AABB(pos).inflate(radius);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
            for (ItemEntity itemEntity : items) {
                if (!itemEntity.isAlive()) continue;
                ItemStack stack = itemEntity.getItem().copy();
                boolean added = player.getInventory().add(stack);
                if (stack.isEmpty()) {
                    itemEntity.discard();
                } else if (added) {
                    itemEntity.setItem(stack);
                }
            }
        });
    }

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (!(world instanceof ServerLevel serverLevel)) return;
            if (!ModConfig.get().magnetEnabled) return;

            ItemStack tool = player.getMainHandItem();
            if (tool.isEmpty()) return;
            if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MAGNET, tool) <= 0) return;

            collectNearby(serverLevel, serverPlayer, pos);
        });
    }
}
