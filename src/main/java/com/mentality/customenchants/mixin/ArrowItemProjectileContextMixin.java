package com.mentality.customenchants.mixin;

import com.mentality.customenchants.projectile.ProjectileEnchantmentCapture;
import com.mentality.customenchants.projectile.ProjectileEnchantmentContextHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public abstract class ArrowItemProjectileContextMixin {
    @Inject(method = "createArrow", at = @At("RETURN"))
    private void customEnchants$attachBow(Level level, ItemStack projectile, LivingEntity owner,
                                          CallbackInfoReturnable<AbstractArrow> cir) {
        if (cir.getReturnValue() instanceof ProjectileEnchantmentContextHolder holder) {
            ProjectileEnchantmentCapture.attachIfMatching(owner, holder);
        }
    }
}
