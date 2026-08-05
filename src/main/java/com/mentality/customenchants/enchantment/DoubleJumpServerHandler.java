package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.CustomEnchantsMod;
import com.mentality.customenchants.net.DoubleJumpPayload;
import com.mentality.customenchants.net.DoubleJumpApprovedPayload;
import com.mentality.customenchants.state.DoubleJumpServerValidator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DoubleJumpServerHandler {

    /** Vanilla's normal jump take-off velocity; this is applied while airborne. */
    static final double DOUBLE_JUMP_Y_VELOCITY = 0.42D;
    /** Exact horizontal magnitude used by LivingEntity.jumpFromGround in Minecraft 1.21.1. */
    static final float VANILLA_SPRINT_JUMP_IMPULSE = 0.2F;
    private static final boolean TRACE = Boolean.getBoolean("customenchants.debug.double_jump");

    private static final Map<UUID, DoubleJumpServerValidator.State> states = new HashMap<>();
    private static final Map<UUID, ResourceKey<Level>> dimensions = new HashMap<>();
    private static final Map<UUID, Long> sequences = new HashMap<>();

    public static void register() {
        PayloadTypeRegistry.playC2S().register(DoubleJumpPayload.TYPE, DoubleJumpPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(DoubleJumpApprovedPayload.TYPE, DoubleJumpApprovedPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> handleDoubleJump(context.player()));
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ResourceKey<Level> previousDimension = dimensions.put(player.getUUID(), player.level().dimension());
                if (previousDimension != null && !previousDimension.equals(player.level().dimension())) {
                    states.remove(player.getUUID());
                }
                if (player.onGround()) {
                    states.computeIfPresent(player.getUUID(), (id, state) -> DoubleJumpServerValidator.resetAfterLanding(state));
                }
            }
        });
    }

    private static void handleDoubleJump(ServerPlayer player) {
        trace("received player={} pos={} velocity={} ground={} water={} swimming={} lava={} flying={} passenger={}",
                player.getUUID(), player.position(), player.getDeltaMovement(), player.onGround(), player.isInWater(),
                player.isSwimming(), player.isInLava(), player.isFallFlying(), player.isPassenger());
        if (!ModConfig.get().doubleJumpEnabled || !eligible(player)) {
            trace("rejected player={} reason=ineligible", player.getUUID());
            return;
        }

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty() || EnchantmentAccess.getLevel(boots, ModEnchantments.DOUBLE_JUMP) <= 0) {
            trace("rejected player={} reason=no-enchantment", player.getUUID());
            return;
        }

        UUID playerId = player.getUUID();
        DoubleJumpServerValidator.State previous = states.getOrDefault(playerId, DoubleJumpServerValidator.State.initial());
        DoubleJumpServerValidator.Decision decision = DoubleJumpServerValidator.accept(
                previous, player.getServer().getTickCount(), true);
        if (!decision.accepted()) {
            trace("rejected player={} reason=airtime-already-used", playerId);
            return;
        }
        states.put(playerId, decision.state());

        Vec3 before = player.getDeltaMovement();
        Vec3 sprintImpulse = applyAirborneJumpVelocity(player);
        Vec3 after = player.getDeltaMovement();
        // Do not send a full motion vector: its server-side X/Z may lag behind the owner's
        // predicted sprint movement. The client receives only this accepted Y component and
        // retains its current horizontal momentum while server reconciliation stays authoritative.
        long sequence = sequences.merge(playerId, 1L, Long::sum);
        ServerPlayNetworking.send(player, new DoubleJumpApprovedPayload(sequence, after.y,
                sprintImpulse.x, sprintImpulse.z));
        player.serverLevel().sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.1D,
                player.getZ(), 12, 0.3D, 0.05D, 0.3D, 0.0D);
        trace("accepted player={} sequence={} sprint={} yaw={} velocityBefore={} sprintImpulse={} velocityAfter={} horizontalBefore={} horizontalAfter={} impulse={} hurtMarked={} yApproval={}",
                playerId, sequence, player.isSprinting(), player.getYRot(), before, sprintImpulse, after, horizontalSpeed(before), horizontalSpeed(after),
                player.hasImpulse, player.hurtMarked, after.y);

        // 67% chance to consume 1 durability (Unbreaking is handled by hurtAndBreak)
        if (player.getRandom().nextFloat() < 0.67f) {
            boots.hurtAndBreak(1, player, EquipmentSlot.FEET);
        }
    }

    /** Applies an authoritative jump impulse without relying on the grounded-only vanilla helper. */
    static Vec3 applyAirborneJumpVelocity(ServerPlayer player) {
        Vec3 velocity = player.getDeltaMovement();
        Vec3 sprintImpulse = sprintJumpImpulse(player.getYRot(), player.isSprinting());
        player.setDeltaMovement(velocity.x + sprintImpulse.x, Math.max(velocity.y, DOUBLE_JUMP_Y_VELOCITY),
                velocity.z + sprintImpulse.z);
        player.hasImpulse = true;
        player.hurtMarked = true;
        player.resetFallDistance();
        return sprintImpulse;
    }

    /** Exact 1.21.1 LivingEntity.jumpFromGround sprint branch, isolated from its grounded-only Y logic. */
    static Vec3 sprintJumpImpulse(float yawDegrees, boolean sprinting) {
        if (!sprinting) return Vec3.ZERO;
        float yawRadians = yawDegrees * ((float) Math.PI / 180.0F);
        return new Vec3(-Mth.sin(yawRadians) * VANILLA_SPRINT_JUMP_IMPULSE, 0.0D,
                Mth.cos(yawRadians) * VANILLA_SPRINT_JUMP_IMPULSE);
    }

    private static boolean eligible(ServerPlayer player) {
        return player.isAlive() && !player.isRemoved() && !player.isCreative() && !player.isSpectator()
                && !player.isPassenger() && !player.isVehicle() && !player.onGround()
                && !player.isInWater() && !player.isSwimming() && !player.isUnderWater()
                && !player.isInLava() && !player.isFallFlying()
                && Double.isFinite(player.getX()) && Double.isFinite(player.getY()) && Double.isFinite(player.getZ());
    }

    private static void trace(String message, Object... args) {
        if (TRACE) CustomEnchantsMod.LOGGER.info("[Double Jump trace] " + message, args);
    }

    private static double horizontalSpeed(Vec3 velocity) {
        return Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
    }

    public static void clear(UUID playerId) {
        states.remove(playerId);
        dimensions.remove(playerId);
        sequences.remove(playerId);
    }

    public static void clearAll() {
        states.clear();
        dimensions.clear();
        sequences.clear();
    }
}
