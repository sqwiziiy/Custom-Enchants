package com.mentality.customenchants.mixin;

import com.mentality.customenchants.projectile.ProjectileEnchantmentContext;
import com.mentality.customenchants.projectile.ProjectileEnchantmentContextHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public abstract class ProjectileContextCrossbowMixin {
    @Inject(method = "getArrow", at = @At("RETURN"))
    private static void customEnchants$attachCrossbow(Level level, LivingEntity shooter, ItemStack crossbow,
                                                      ItemStack projectile, CallbackInfoReturnable<AbstractArrow> cir) {
        if (shooter instanceof net.minecraft.world.entity.player.Player
                && cir.getReturnValue() instanceof ProjectileEnchantmentContextHolder holder) {
            holder.customEnchants$setProjectileContext(ProjectileEnchantmentContext.fromWeapon(crossbow, shooter));
        }
    }
}
