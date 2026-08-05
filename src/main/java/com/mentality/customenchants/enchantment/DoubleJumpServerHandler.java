package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.CustomEnchantsMod;
import com.mentality.customenchants.net.DoubleJumpPayload;
import com.mentality.customenchants.state.DoubleJumpServerValidator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DoubleJumpServerHandler {

    /** Vanilla's normal jump take-off velocity; this is applied while airborne. */
    static final double DOUBLE_JUMP_Y_VELOCITY = 0.42D;
    private static final boolean TRACE = Boolean.getBoolean("customenchants.debug.double_jump");

    private static final Map<UUID, DoubleJumpServerValidator.State> states = new HashMap<>();
    private static final Map<UUID, ResourceKey<Level>> dimensions = new HashMap<>();

    public static void register() {
        PayloadTypeRegistry.playC2S().register(DoubleJumpPayload.TYPE, DoubleJumpPayload.CODEC);
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
        applyAirborneJumpVelocity(player);
        Vec3 after = player.getDeltaMovement();
        // A player's client is not a normal entity tracker recipient for its own motion.
        // Send the authoritative velocity explicitly so client prediction cannot erase it.
        player.connection.send(new ClientboundSetEntityMotionPacket(player));
        player.serverLevel().sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.1D,
                player.getZ(), 12, 0.3D, 0.05D, 0.3D, 0.0D);
        trace("accepted player={} velocityBefore={} velocityAfter={} impulse={} hurtMarked={} particles=true",
                playerId, before, after, player.hasImpulse, player.hurtMarked);

        // 67% chance to consume 1 durability (Unbreaking is handled by hurtAndBreak)
        if (player.getRandom().nextFloat() < 0.67f) {
            boots.hurtAndBreak(1, player, EquipmentSlot.FEET);
        }
    }

    /** Applies an authoritative jump impulse without relying on the grounded-only vanilla helper. */
    static void applyAirborneJumpVelocity(ServerPlayer player) {
        Vec3 velocity = player.getDeltaMovement();
        player.setDeltaMovement(velocity.x, Math.max(velocity.y, DOUBLE_JUMP_Y_VELOCITY), velocity.z);
        player.hasImpulse = true;
        player.hurtMarked = true;
        player.resetFallDistance();
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

    public static void clear(UUID playerId) {
        states.remove(playerId);
        dimensions.remove(playerId);
    }

    public static void clearAll() {
        states.clear();
        dimensions.clear();
    }
}
