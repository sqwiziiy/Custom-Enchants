package com.mentality.customenchants.mixin;

import com.mentality.customenchants.combat.KillingWeaponContext;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class KillingWeaponContextMixin {
    @WrapMethod(method = "hurt")
    private boolean customEnchants$captureKillingWeapon(DamageSource source, float amount,
                                                         Operation<Boolean> original) {
        try (KillingWeaponContext.Scope ignored = KillingWeaponContext.open(
                (LivingEntity) (Object) this, source)) {
            return original.call(source, amount);
        }
    }
}
