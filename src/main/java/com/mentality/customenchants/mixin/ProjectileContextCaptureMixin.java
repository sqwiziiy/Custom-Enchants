package com.mentality.customenchants.mixin;

import com.mentality.customenchants.projectile.ProjectileEnchantmentCapture;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BowItem.class)
public abstract class ProjectileContextCaptureMixin {
    @WrapMethod(method = "releaseUsing")
    private void customEnchants$captureBow(ItemStack weapon, Level level, LivingEntity user, int remaining, Operation<Void> original) {
        try (ProjectileEnchantmentCapture.Scope ignored = ProjectileEnchantmentCapture.open(user, weapon)) {
            original.call(weapon, level, user, remaining);
        }
    }
}
