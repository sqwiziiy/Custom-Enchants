package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Runtime logic for the XP Syphon enchantment. Identity/definition is data-driven
 * ({@link ModEnchantments#XP_SYPHON}); this neutral holder keeps the on-hit XP-orb drop,
 * invoked by the melee combat handler (wired in the gameplay-hook phase).
 */
public final class XpSyphonEnchantment {

    private XpSyphonEnchantment() {
    }

    public static void applyXpSyphon(Player attacker, Entity target, int level) {
        if (!ModConfig.get().xpSyphonEnabled) return;
        if (!(target instanceof LivingEntity)) return;
        if (!(attacker.level() instanceof ServerLevel serverLevel)) return;

        float chance = switch (level) {
            case 1 -> 0.05f;
            case 2 -> 0.10f;
            default -> 0.15f;
        };

        if (serverLevel.getRandom().nextFloat() >= chance) return;

        // Drop XP orbs at the target's position (1/2/3 XP per level)
        serverLevel.addFreshEntity(new ExperienceOrb(
                serverLevel,
                target.getX(), target.getY() + 0.5, target.getZ(),
                level
        ));
    }
}
