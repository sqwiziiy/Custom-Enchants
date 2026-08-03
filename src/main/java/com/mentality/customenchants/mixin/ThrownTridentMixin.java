package com.mentality.customenchants.mixin;

import com.mentality.customenchants.enchantment.GlowStrikeEnchantment;
import com.mentality.customenchants.enchantment.ShadowBladeEnchantment;
import com.mentality.customenchants.projectile.ProjectileEnchantmentContext;
import com.mentality.customenchants.projectile.ProjectileEnchantmentContextHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin {
    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean customEnchants$applyAfterDamage(Entity target, DamageSource source, float damage, Operation<Boolean> original) {
        boolean successful = original.call(target, source, damage);
        if (!successful || !(target instanceof LivingEntity livingTarget)) return false;
        ThrownTrident trident = (ThrownTrident) (Object) this;
        if (!(trident.getOwner() instanceof Player player)) return successful;
        ProjectileEnchantmentContext context = trident instanceof ProjectileEnchantmentContextHolder holder
                ? holder.customEnchants$getProjectileContext() : ProjectileEnchantmentContext.EMPTY;
        if (context.shadowBlade() > 0) ShadowBladeEnchantment.applyShadowBlade(player, livingTarget, context.shadowBlade());
        if (context.glowStrike() > 0) GlowStrikeEnchantment.applyGlowStrike(player, livingTarget, context.glowStrike());
        return successful;
    }
}
