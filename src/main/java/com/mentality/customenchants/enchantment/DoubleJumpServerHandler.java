package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.CustomEnchantsMod;
import com.mentality.customenchants.config.ModConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class DoubleJumpServerHandler {

    public static final ResourceLocation DOUBLE_JUMP_PACKET = new ResourceLocation(CustomEnchantsMod.MOD_ID, "double_jump");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(DOUBLE_JUMP_PACKET, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> handleDoubleJump(player));
        });
    }

    private static void handleDoubleJump(ServerPlayer player) {
        if (!ModConfig.get().doubleJumpEnabled) return;

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DOUBLE_JUMP, boots) <= 0) {
            return;
        }

        // 67% chance to consume 1 durability (Unbreaking is handled by hurt())
        if (player.getRandom().nextFloat() < 0.67f) {
            boots.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.FEET));
        }
    }
}
