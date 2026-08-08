package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.net.DoubleJumpApprovedPayload;
import com.mentality.customenchants.net.DoubleJumpPayload;
import com.mentality.customenchants.state.DoubleJumpServerValidator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DoubleJumpServerHandler {

    /** Vanilla-style airborne second-jump vertical velocity. */
    static final double DOUBLE_JUMP_Y_VELOCITY = 0.42D;
    /** Vanilla sprint-jump horizontal impulse magnitude. */
    static final float VANILLA_SPRINT_JUMP_IMPULSE = 0.2F;

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
        if (!ModConfig.get().doubleJumpEnabled || !eligible(player)) return;

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty() || EnchantmentAccess.getLevel(boots, ModEnchantments.DOUBLE_JUMP) <= 0) {
            return;
        }

        UUID playerId = player.getUUID();
        DoubleJumpServerValidator.State previous = states.getOrDefault(playerId, DoubleJumpServerValidator.State.initial());
        DoubleJumpServerValidator.Decision decision = DoubleJumpServerValidator.accept(
                previous, player.level().getServer().getTickCount(), true);
        if (!decision.accepted()) return;
        states.put(playerId, decision.state());

        Vec3 sprintImpulse = applyAirborneJumpVelocity(player);
        Vec3 after = player.getDeltaMovement();

        // Only send the authoritative vertical component plus the sprint impulse delta.
        // Sending a stale full X/Z vector can overwrite the owner's newer client prediction.
        long sequence = sequences.merge(playerId, 1L, Long::sum);
        ServerPlayNetworking.send(player, new DoubleJumpApprovedPayload(
                sequence, after.y, sprintImpulse.x, sprintImpulse.z));

        ((ServerLevel) player.level()).sendParticles(ParticleTypes.CLOUD,
                player.getX(), player.getY() + 0.1D, player.getZ(),
                12, 0.3D, 0.05D, 0.3D, 0.0D);

        // 67% chance to consume 1 durability (Unbreaking is handled by hurtAndBreak)
        if (player.getRandom().nextFloat() < 0.67f) {
            boots.hurtAndBreak(1, player, EquipmentSlot.FEET);
        }
    }

    /** Applies an airborne jump impulse directly instead of using grounded-only jumpFromGround(). */
    static Vec3 applyAirborneJumpVelocity(ServerPlayer player) {
        Vec3 velocity = player.getDeltaMovement();
        Vec3 sprintImpulse = sprintJumpImpulse(player.getYRot(), player.isSprinting());
        player.setDeltaMovement(
                velocity.x + sprintImpulse.x,
                Math.max(velocity.y, DOUBLE_JUMP_Y_VELOCITY),
                velocity.z + sprintImpulse.z);
        player.hurtMarked = true;
        player.resetFallDistance();
        return sprintImpulse;
    }

    /** Vanilla-style sprint-jump forward impulse, independent of grounded jump logic. */
    static Vec3 sprintJumpImpulse(float yawDegrees, boolean sprinting) {
        if (!sprinting) return Vec3.ZERO;
        float yawRadians = yawDegrees * ((float) Math.PI / 180.0F);
        return new Vec3(
                -Mth.sin(yawRadians) * VANILLA_SPRINT_JUMP_IMPULSE,
                0.0D,
                Mth.cos(yawRadians) * VANILLA_SPRINT_JUMP_IMPULSE);
    }

    private static boolean eligible(ServerPlayer player) {
        return player.isAlive() && !player.isRemoved() && !player.isCreative() && !player.isSpectator()
                && !player.isPassenger() && !player.isVehicle() && !player.onGround()
                && !player.isInWater() && !player.isSwimming() && !player.isUnderWater()
                && !player.isInLava() && !player.isFallFlying()
                && Double.isFinite(player.getX()) && Double.isFinite(player.getY()) && Double.isFinite(player.getZ());
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
