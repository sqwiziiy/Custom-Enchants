package com.mentality.customenchants.mixin;

import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.GlowStrikeEnchantment;
import com.mentality.customenchants.enchantment.ModEnchantments;
import com.mentality.customenchants.enchantment.PoisonBladeEnchantment;
import com.mentality.customenchants.enchantment.XpSyphonEnchantment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Melee on-hit hook for the custom enchantments that used to override the (now removed, since
 * {@code Enchantment} is final/data-driven) {@code Enchantment.doPostAttack}: Glow Strike,
 * Poison Blade and XP Syphon. {@code EnchantmentHelper.doPostAttackEffectsWithItemSource} is the
 * 1.21.1 replacement dispatch point — it is called only from the melee {@code Player.attack}
 * path (never from projectile-hit paths, which stay on {@link ThrownTridentMixin}), so this
 * mirrors the exact 1.20.1 melee-only coverage with no double application.
 */
@Mixin(EnchantmentHelper.class)
public abstract class MeleeEnchantmentEffectsMixin {

    @Inject(method = "doPostAttackEffectsWithItemSource", at = @At("HEAD"))
    private static void customEnchants$onMeleeHit(ServerLevel level, Entity target, DamageSource source,
                                                   ItemStack weapon, CallbackInfo ci) {
        if (!(source.getEntity() instanceof Player player) || !(target instanceof LivingEntity livingTarget)) {
            return;
        }

        int glowStrike = EnchantmentAccess.getLevel(weapon, ModEnchantments.GLOW_STRIKE);
        if (glowStrike > 0) {
            GlowStrikeEnchantment.applyGlowStrike(player, livingTarget, glowStrike);
        }

        int poisonBlade = EnchantmentAccess.getLevel(weapon, ModEnchantments.POISON_BLADE);
        if (poisonBlade > 0) {
            PoisonBladeEnchantment.applyPoisonBlade(player, livingTarget, poisonBlade);
        }

        int xpSyphon = EnchantmentAccess.getLevel(weapon, ModEnchantments.XP_SYPHON);
        if (xpSyphon > 0) {
            XpSyphonEnchantment.applyXpSyphon(player, target, xpSyphon);
        }
    }
}
