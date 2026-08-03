package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.net.DoubleJumpPayload;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DoubleJumpServerHandler {

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
        if (!ModConfig.get().doubleJumpEnabled || !eligible(player)) return;

        UUID playerId = player.getUUID();
        DoubleJumpServerValidator.State previous = states.getOrDefault(playerId, DoubleJumpServerValidator.State.initial());
        DoubleJumpServerValidator.Decision decision = DoubleJumpServerValidator.accept(
                previous, player.getServer().getTickCount(), true);
        if (!decision.accepted()) return;
        states.put(playerId, decision.state());

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty() || EnchantmentAccess.getLevel(boots, ModEnchantments.DOUBLE_JUMP) <= 0) {
            return;
        }

        // 67% chance to consume 1 durability (Unbreaking is handled by hurtAndBreak)
        if (player.getRandom().nextFloat() < 0.67f) {
            boots.hurtAndBreak(1, player, EquipmentSlot.FEET);
        }
    }

    private static boolean eligible(ServerPlayer player) {
        return player.isAlive() && !player.isRemoved() && !player.isCreative() && !player.isSpectator()
                && !player.isPassenger() && !player.isVehicle() && !player.onGround()
                && !player.isInWater() && !player.isInLava()
                && Double.isFinite(player.getX()) && Double.isFinite(player.getY()) && Double.isFinite(player.getZ());
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
