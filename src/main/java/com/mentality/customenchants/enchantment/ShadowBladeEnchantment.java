package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.shadowblade.SafeTeleportService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Runtime logic for the Shadow Blade enchantment. Identity/definition is data-driven
 * ({@link ModEnchantments#SHADOW_BLADE}); this neutral holder keeps the teleport-behind logic.
 */
public final class ShadowBladeEnchantment {

    private ShadowBladeEnchantment() {
    }

    public static void applyShadowBlade(Player player, LivingEntity livingTarget, int level) {
        ModConfig config = ModConfig.get();
        if (!config.shadowBladeEnabled) {
            return;
        }
        float chance = switch (level) {
            case 1 -> config.shadowBladeChanceL1 / 100f;
            case 2 -> config.shadowBladeChanceL2 / 100f;
            case 3 -> config.shadowBladeChanceL3 / 100f;
            default -> config.shadowBladeChanceL1 / 100f;
        };
        int slownessDuration = switch (level) {
            case 1 -> config.shadowBladeSlowDurationL1;
            case 2 -> config.shadowBladeSlowDurationL2;
            case 3 -> config.shadowBladeSlowDurationL3;
            default -> config.shadowBladeSlowDurationL1;
        };

        // Distance bonus: up to +10% at 30 blocks distance
        double distance = player.distanceTo(livingTarget);
        if (!Double.isFinite(distance)) {
            return;
        }
        float distanceBonus = (float) (Math.min(distance / 30.0, 1.0) * 0.10);
        chance += distanceBonus;

        if (player.getRandom().nextFloat() < chance) {
            if (player instanceof ServerPlayer serverPlayer) {
                SafeTeleportService.tryTeleportBehind(serverPlayer, livingTarget);
            }

            // Preserve the historical effect timing: a successful chance roll slows the target
            // even when every destination is rejected as unsafe.
            livingTarget.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, slownessDuration, 1));
        }
    }
}
