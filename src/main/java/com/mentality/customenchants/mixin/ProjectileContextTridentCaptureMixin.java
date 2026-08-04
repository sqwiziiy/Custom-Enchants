package com.mentality.customenchants.mixin;

import com.mentality.customenchants.projectile.ProjectileEnchantmentContext;
import com.mentality.customenchants.projectile.ProjectileEnchantmentContextHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public abstract class ProjectileContextTridentCaptureMixin {
    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    private void customEnchants$attachTrident(Level level, LivingEntity owner, ItemStack trident, CallbackInfo ci) {
        if (owner instanceof net.minecraft.world.entity.player.Player
                && this instanceof ProjectileEnchantmentContextHolder holder) {
            holder.customEnchants$setProjectileContext(ProjectileEnchantmentContext.fromWeapon(trident, owner));
        }
    }
}
