package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.CustomEnchantsMod;
import com.mentality.customenchants.config.ModConfig;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SecondWindHandler {

    public static final ResourceLocation SECOND_WIND_PACKET = new ResourceLocation(CustomEnchantsMod.MOD_ID, "second_wind");

    // Cooldown tracking: player UUID -> game time when effect was last triggered
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    public static void register() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!ModConfig.get().secondWindEnabled) return;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.isDeadOrDying() || player.isSpectator() || player.isCreative()) continue;

                // Count how many armor pieces have Second Wind
                int pieces = 0;
                for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                    ItemStack armor = player.getItemBySlot(slot);
                    if (!armor.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SECOND_WIND, armor) > 0) {
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
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, speedTicks, 1, false, true, true));
                // Resistance I
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, resistanceTicks, 0, false, true, true));

                // Knockback resistance 100% for speed duration
                var knockbackAttr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE);
                if (knockbackAttr != null) {
                    var modifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                            java.util.UUID.fromString("b3f7c466-7c2a-4c3f-9e1a-1d2f3a4b5c6d"),
                            "Second Wind knockback resistance",
                            1.0,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION
                    );
                    knockbackAttr.removeModifier(modifier);
                    knockbackAttr.addTransientModifier(modifier);

                    server.execute(() -> scheduleKnockbackRemoval(player, modifier, speedTicks));
                }

                // Send visual effect packet to client
                ServerPlayNetworking.send(player, SECOND_WIND_PACKET, PacketByteBufs.empty());
            }
        });
    }

    private static void scheduleKnockbackRemoval(ServerPlayer player, net.minecraft.world.entity.ai.attributes.AttributeModifier modifier, int ticks) {
        // Use a simple tick counter approach
        player.getServer().tell(new net.minecraft.server.TickTask(
                player.getServer().getTickCount() + ticks, () -> {
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
