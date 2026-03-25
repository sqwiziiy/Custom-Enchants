package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class DoubleJumpHandler {

    private static boolean wasOnGround = true;
    private static boolean canDoubleJump = false;
    private static boolean jumpKeyWasPressed = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ModConfig.get().doubleJumpEnabled) return;

            LocalPlayer player = client.player;
            if (player == null || player.isCreative() || player.isSpectator()) return;

            if (!hasDoubleJumpEnchant(player)) {
                canDoubleJump = false;
                wasOnGround = player.onGround();
                jumpKeyWasPressed = client.options.keyJump.isDown();
                return;
            }

            boolean jumpKeyDown = client.options.keyJump.isDown();

            if (player.onGround()) {
                canDoubleJump = true;
                wasOnGround = true;
            } else if (!wasOnGround && canDoubleJump && jumpKeyDown && !jumpKeyWasPressed
                    && !player.isInWater() && !player.isInLava()) {
                player.jumpFromGround();
                canDoubleJump = false;
            }

            if (!player.onGround()) {
                wasOnGround = false;
            }

            jumpKeyWasPressed = jumpKeyDown;
        });
    }

    private static boolean hasDoubleJumpEnchant(LocalPlayer player) {
        ItemStack boots = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET);
        return !boots.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DOUBLE_JUMP, boots) > 0;
    }
}
