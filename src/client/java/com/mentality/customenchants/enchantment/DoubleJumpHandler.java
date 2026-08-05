package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public class DoubleJumpHandler {

    private static boolean wasOnGround = true;
    private static boolean canDoubleJump = false;
    private static boolean jumpKeyWasPressed = false;
    private static LocalPlayer trackedPlayer;
    private static long lastApprovalSequence = Long.MIN_VALUE;

    public static void register() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> reset());
        ClientPlayNetworking.registerGlobalReceiver(DoubleJumpServerHandler.DOUBLE_JUMP_APPROVED_PACKET,
                (client, handler, buf, responseSender) -> {
                    long sequence = buf.readLong();
                    double y = buf.readDouble();
                    double x = buf.readDouble();
                    double z = buf.readDouble();
                    client.execute(() -> applyServerApproval(client.player, sequence, y, x, z));
                });
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
                ClientPlayNetworking.send(DoubleJumpServerHandler.DOUBLE_JUMP_PACKET, PacketByteBufs.empty());
            }

            if (!player.onGround()) {
                wasOnGround = false;
            }

            jumpKeyWasPressed = jumpKeyDown;
        });
    }

    /** Merge only a fresh, server-approved jump impulse with predicted client movement. */
    static void applyServerApproval(LocalPlayer player, long sequence, double verticalVelocity,
                                    double horizontalImpulseX, double horizontalImpulseZ) {
        if (player == null || sequence <= lastApprovalSequence
                || !Double.isFinite(verticalVelocity) || !Double.isFinite(horizontalImpulseX)
                || !Double.isFinite(horizontalImpulseZ)) return;
        lastApprovalSequence = sequence;
        Vec3 current = player.getDeltaMovement();
        player.setDeltaMovement(current.x + horizontalImpulseX, Math.max(current.y, verticalVelocity),
                current.z + horizontalImpulseZ);
    }

    private static boolean hasDoubleJumpEnchant(LocalPlayer player) {
        ItemStack boots = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET);
        return !boots.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DOUBLE_JUMP, boots) > 0;
    }

    private static void reset() {
        wasOnGround = true;
        canDoubleJump = false;
        jumpKeyWasPressed = false;
        trackedPlayer = null;
        lastApprovalSequence = Long.MIN_VALUE;
    }
}
