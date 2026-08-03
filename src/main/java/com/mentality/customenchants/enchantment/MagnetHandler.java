package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.magnet.MagnetPickupPolicy;
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
            if (!player.isAlive() || level != player.level()) return;
            AABB area = new AABB(pos).inflate(radius);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
            for (ItemEntity itemEntity : items) {
                if (!MagnetPickupPolicy.eligible(itemEntity, player, radius)) continue;
                itemEntity.playerTouch(player);
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
            if (EnchantmentAccess.getLevel(tool, ModEnchantments.MAGNET) <= 0) return;

            collectNearby(serverLevel, serverPlayer, pos);
        });
    }
}
