package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.CustomEnchantsMod;
import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.net.SecondWindPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SecondWindHandler {

    /** Stable id for the Second Wind knockback-resistance attribute modifier. */
    private static final Identifier KNOCKBACK_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(CustomEnchantsMod.MOD_ID, "second_wind_knockback");

    // Cooldown tracking: player UUID -> game time when effect was last triggered
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    public static void register() {
        PayloadTypeRegistry.playS2C().register(SecondWindPayload.TYPE, SecondWindPayload.CODEC);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!ModConfig.get().secondWindEnabled) return;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.isDeadOrDying() || player.isSpectator() || player.isCreative()) continue;

                // Count how many armor pieces have Second Wind
                int pieces = 0;
                for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                    ItemStack armor = player.getItemBySlot(slot);
                    if (!armor.isEmpty() && EnchantmentAccess.getLevel(armor, ModEnchantments.SECOND_WIND) > 0) {
                        pieces++;
                    }
                }
                if (pieces <= 0) continue;

                // Trigger when health drops to 2 HP (1 heart) or below
                if (player.getHealth() > 2.0f) continue;

                // Check cooldown
                long currentTime = server.getTickCount();
                int cooldownTicks = ModConfig.get().secondWindCooldown * 20;
                Long lastTriggered = cooldowns.get(player.getUUID());
                if (lastTriggered != null && (currentTime - lastTriggered) < cooldownTicks) continue;

                // Trigger Second Wind — scale durations by piece count
                cooldowns.put(player.getUUID(), currentTime);

                // Speed: 1→2s, 2→3s, 3→3s, 4→4s
                int speedTicks = calculateSpeedTicks(ModConfig.get().secondWindSpeedDuration, pieces);
                // Resistance: 1→1s, 2→1s, 3+→2s
                int resistanceTicks = (pieces >= 3) ? 40 : 20;

                // Speed II
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, speedTicks, 1, false, true, true));
                // Resistance I
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, resistanceTicks, 0, false, true, true));

                // Knockback resistance 100% for speed duration
                var knockbackAttr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE);
                if (knockbackAttr != null) {
                    var modifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                            KNOCKBACK_MODIFIER_ID,
                            1.0,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
                    );
                    knockbackAttr.removeModifier(KNOCKBACK_MODIFIER_ID);
                    knockbackAttr.addTransientModifier(modifier);

                    server.execute(() -> scheduleKnockbackRemoval(player, modifier, speedTicks));
                }

                // Send visual effect payload to client
                ServerPlayNetworking.send(player, SecondWindPayload.INSTANCE);
            }
        });
    }

    private static void scheduleKnockbackRemoval(ServerPlayer player, net.minecraft.world.entity.ai.attributes.AttributeModifier modifier, int ticks) {
        // Use a simple tick counter approach
        net.minecraft.server.MinecraftServer server = player.level().getServer();
        server.schedule(new net.minecraft.server.TickTask(
                server.getTickCount() + ticks, () -> {
            var knockbackAttr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE);
            if (knockbackAttr != null) {
                knockbackAttr.removeModifier(modifier);
            }
        }));
    }

    public static int calculateSpeedTicks(int baseDurationSeconds, int pieces) {
        double multiplier = switch (Math.max(1, Math.min(4, pieces))) {
            case 1 -> 0.4d;
            case 2, 3 -> 0.6d;
            default -> 0.8d;
        };
        long ticks = Math.round(baseDurationSeconds * 20.0d * multiplier);
        return (int) Math.max(1L, Math.min(Integer.MAX_VALUE, ticks));
    }

    public static void clear(UUID playerId) {
        cooldowns.remove(playerId);
    }

    public static void clearAll() {
        cooldowns.clear();
    }
}
