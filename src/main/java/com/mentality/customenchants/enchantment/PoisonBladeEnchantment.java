package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Runtime logic for the Poison Blade enchantment. Identity/definition is data-driven
 * ({@link ModEnchantments#POISON_BLADE}); this neutral holder keeps the on-hit poison effect,
 * invoked by the melee combat handler (wired in the gameplay-hook phase).
 */
public final class PoisonBladeEnchantment {

    private PoisonBladeEnchantment() {
    }

    public static void applyPoisonBlade(Player attacker, LivingEntity livingTarget, int level) {
        ModConfig config = ModConfig.get();
        if (!config.poisonBladeEnabled) {
            return;
        }
        int duration = switch (level) {
            case 1 -> config.poisonBladeDurationL1;
            case 2 -> config.poisonBladeDurationL2;
            case 3 -> config.poisonBladeDurationL3;
            default -> config.poisonBladeDurationL1;
        };
        livingTarget.addEffect(new MobEffectInstance(MobEffects.POISON, duration, 0));
    }
}
