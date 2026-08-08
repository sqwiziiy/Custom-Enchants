package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.net.DoubleJumpApprovedPayload;
import com.mentality.customenchants.net.DoubleJumpPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class DoubleJumpHandler {

    private static boolean wasOnGround = true;
    private static boolean canDoubleJump = false;
    private static boolean jumpKeyWasPressed = false;
    private static LocalPlayer trackedPlayer;
    private static long lastApprovalSequence = Long.MIN_VALUE;

    public static void register() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> reset());
        ClientPlayNetworking.registerGlobalReceiver(DoubleJumpApprovedPayload.TYPE, (payload, context) ->
                context.client().execute(() -> applyServerApprovedVelocity(context.client().player, payload)));

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
                // Preserve the local sprint state explicitly. The server can briefly observe
                // sprint=false during airborne packet handling even while the owner is sprinting.
                ClientPlayNetworking.send(new DoubleJumpPayload(player.isSprinting()));
            }

            if (!player.onGround()) {
                wasOnGround = false;
            }

            jumpKeyWasPressed = jumpKeyDown;
        });
    }

    /** Applies only server-approved movement; current client X/Z prediction is preserved. */
    static void applyServerApprovedVelocity(LocalPlayer player, DoubleJumpApprovedPayload payload) {
        if (player == null || payload == null || !Double.isFinite(payload.verticalVelocity())) return;
        if (payload.sequence() <= lastApprovalSequence) return;
        if (!Double.isFinite(payload.horizontalImpulseX()) || !Double.isFinite(payload.horizontalImpulseZ())) return;

        lastApprovalSequence = payload.sequence();
        Vec3 current = player.getDeltaMovement();
        player.setDeltaMovement(
                current.x + payload.horizontalImpulseX(),
                Math.max(current.y, payload.verticalVelocity()),
                current.z + payload.horizontalImpulseZ());
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
        lastApprovalSequence = Long.MIN_VALUE;
    }
}
