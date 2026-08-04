package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.net.DoubleJumpPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class DoubleJumpHandler {

    private static boolean wasOnGround = true;
    private static boolean canDoubleJump = false;
    private static boolean jumpKeyWasPressed = false;
    private static LocalPlayer trackedPlayer;

    public static void register() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> reset());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ModConfig.get().doubleJumpEnabled) return;

            LocalPlayer player = client.player;
            if (player == null) {
                reset();
                return;
            }
            if (player != trackedPlayer) {
                reset();
                trackedPlayer = player;
            }
            if (player.isCreative() || player.isSpectator()) return;

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
                    && !player.isInWater() && !player.isSwimming() && !player.isUnderWater()
                    && !player.isInLava() && !player.isFallFlying()) {
                canDoubleJump = false;
                ClientPlayNetworking.send(DoubleJumpPayload.INSTANCE);
            }

            if (!player.onGround()) {
                wasOnGround = false;
            }

            jumpKeyWasPressed = jumpKeyDown;
        });
    }

    private static boolean hasDoubleJumpEnchant(LocalPlayer player) {
        ItemStack boots = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET);
        return !boots.isEmpty() && EnchantmentAccess.getLevel(boots, ModEnchantments.DOUBLE_JUMP) > 0;
    }

    private static void reset() {
        wasOnGround = true;
        canDoubleJump = false;
        jumpKeyWasPressed = false;
        trackedPlayer = null;
    }
}
