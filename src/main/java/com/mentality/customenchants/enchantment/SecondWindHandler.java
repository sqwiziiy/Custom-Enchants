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

                ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
                if (chestplate.isEmpty()) continue;
                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SECOND_WIND, chestplate);
                if (level <= 0) continue;

                // Trigger when health drops to 2 HP (1 heart) or below
                if (player.getHealth() > 2.0f) continue;

                // Check cooldown
                long currentTime = player.level().getGameTime();
                int cooldownTicks = ModConfig.get().secondWindCooldown * 20;
                Long lastTriggered = cooldowns.get(player.getUUID());
                if (lastTriggered != null && (currentTime - lastTriggered) < cooldownTicks) continue;

                // Trigger Second Wind
                cooldowns.put(player.getUUID(), currentTime);

                int speedDuration = ModConfig.get().secondWindSpeedDuration * 20;

                // Speed II for configured duration
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, speedDuration, 1, false, true, true));

                // Knockback resistance 100% for same duration
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1, 0, false, false, false) {
                    // This is just a workaround — we use the attribute approach below
                });
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN); // Remove the dummy
                // Apply full knockback resistance via effect
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, speedDuration, 0, false, true, true));

                // Apply knockback resistance as an attribute modifier
                var knockbackAttr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE);
                if (knockbackAttr != null) {
                    var modifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                            UUID.fromString("b3f7c466-7c2a-4c3f-9e1a-1d2f3a4b5c6d"),
                            "Second Wind knockback resistance",
                            1.0,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION
                    );
                    knockbackAttr.removeModifier(modifier);
                    knockbackAttr.addTransientModifier(modifier);

                    // Schedule removal after duration
                    server.execute(() -> {
                        scheduleKnockbackRemoval(player, modifier, speedDuration);
                    });
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
}
