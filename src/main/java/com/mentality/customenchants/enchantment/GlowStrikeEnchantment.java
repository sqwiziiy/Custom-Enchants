package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Runtime logic for the Glow Strike enchantment. The enchantment identity/definition is
 * data-driven ({@link ModEnchantments#GLOW_STRIKE}); this neutral holder keeps the on-hit
 * effect logic invoked by the combat/projectile handlers.
 */
public final class GlowStrikeEnchantment {

    private GlowStrikeEnchantment() {
    }

    public static void applyGlowStrike(Player player, LivingEntity livingTarget, int level) {
        ModConfig config = ModConfig.get();
        if (!config.glowStrikeEnabled) return;
        int duration = switch (level) {
            case 1 -> config.glowStrikeDurationL1;
            case 2 -> config.glowStrikeDurationL2;
            case 3 -> config.glowStrikeDurationL3;
            default -> config.glowStrikeDurationL1;
        };
        livingTarget.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration, 0));
    }
}
