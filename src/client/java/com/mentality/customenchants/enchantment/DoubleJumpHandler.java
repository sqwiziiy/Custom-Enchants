package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.net.DoubleJumpPayload;
import com.mentality.customenchants.net.DoubleJumpApprovedPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
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
                context.client().execute(() -> applyServerApprovedVerticalVelocity(context.client().player, payload)));
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
                boolean sprinting = player.isSprinting();
                float yawDegrees = player.getYRot();
                applyLocalPrediction(player, sprinting, yawDegrees);
                ClientPlayNetworking.send(new DoubleJumpPayload(sprinting, yawDegrees));
            }

            if (!player.onGround()) {
                wasOnGround = false;
            }

            jumpKeyWasPressed = jumpKeyDown;
        });
    }

    /** Approval is an acknowledgement: the owner already applied this impulse on its input tick. */
    static void applyServerApprovedVerticalVelocity(LocalPlayer player, DoubleJumpApprovedPayload payload) {
        if (player == null || payload == null || !Double.isFinite(payload.verticalVelocity())) return;
        if (payload.sequence() <= lastApprovalSequence) return;
        if (!Double.isFinite(payload.horizontalImpulseX()) || !Double.isFinite(payload.horizontalImpulseZ())) return;
        lastApprovalSequence = payload.sequence();
        // Never add a second impulse after RTT. Vanilla's authoritative correction remains available
        // through the normal movement synchronization path if the server rejected or diverged.
    }

    static void applyLocalPrediction(LocalPlayer player, boolean sprinting, float yawDegrees) {
        if (player == null || !Float.isFinite(yawDegrees)) return;
        Vec3 current = player.getDeltaMovement();
        float yawRadians = yawDegrees * ((float) Math.PI / 180.0F);
        double impulseX = sprinting ? -Mth.sin(yawRadians) * DoubleJumpServerHandler.VANILLA_SPRINT_JUMP_IMPULSE : 0.0D;
        double impulseZ = sprinting ? Mth.cos(yawRadians) * DoubleJumpServerHandler.VANILLA_SPRINT_JUMP_IMPULSE : 0.0D;
        player.setDeltaMovement(current.x + impulseX, Math.max(current.y, DoubleJumpServerHandler.DOUBLE_JUMP_Y_VELOCITY), current.z + impulseZ);
        player.resetFallDistance();
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
