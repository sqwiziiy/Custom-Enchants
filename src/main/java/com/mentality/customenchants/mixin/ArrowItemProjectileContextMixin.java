package com.mentality.customenchants.mixin;

import com.mentality.customenchants.projectile.ProjectileEnchantmentContext;
import com.mentality.customenchants.projectile.ProjectileEnchantmentContextHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Attaches the shot-time enchantment context to every arrow entity created by
 * {@link ArrowItem#createArrow}. Since 1.21.1 this method receives the firing weapon directly
 * ({@code ProjectileWeaponItem.createProjectile} calls it for both bow and crossbow ammo), so the
 * context is computed inline; no capture-scope indirection is needed anymore.
 */
@Mixin(ArrowItem.class)
public abstract class ArrowItemProjectileContextMixin {
    @Inject(method = "createArrow", at = @At("RETURN"))
    private void customEnchants$attachWeapon(Level level, ItemStack ammo, LivingEntity owner, ItemStack weapon,
                                             CallbackInfoReturnable<AbstractArrow> cir) {
        if (cir.getReturnValue() instanceof ProjectileEnchantmentContextHolder holder) {
            holder.customEnchants$setProjectileContext(ProjectileEnchantmentContext.fromWeapon(weapon, owner));
        }
    }
}
