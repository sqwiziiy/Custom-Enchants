package com.mentality.customenchants.mixin;

import com.mentality.customenchants.shield.ShieldBlockContext;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class ShieldBlockContextMixin {
    @WrapMethod(method = "hurt")
    private boolean customEnchants$openShieldCall(DamageSource source, float amount, Operation<Boolean> original) {
        try (ShieldBlockContext.Scope ignored = ShieldBlockContext.open((LivingEntity) (Object) this, source, amount)) {
            return original.call(source, amount);
        }
    }

    @com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation(
            method = "hurt",
            at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
    private boolean customEnchants$recordVanillaBlock(LivingEntity defender, DamageSource source,
                                                       Operation<Boolean> original) {
        boolean blocked = original.call(defender, source);
        ShieldBlockContext.recordVanillaBlock(defender, source, blocked);
        return blocked;
    }
}
