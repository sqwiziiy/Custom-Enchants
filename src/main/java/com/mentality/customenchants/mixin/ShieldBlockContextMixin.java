package com.mentality.customenchants.mixin;

import com.mentality.customenchants.shield.ShieldBlockContext;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class ShieldBlockContextMixin {
    @WrapMethod(method = "hurtServer")
    private boolean customEnchants$openShieldCall(ServerLevel level, DamageSource source, float amount, Operation<Boolean> original) {
        try (ShieldBlockContext.Scope ignored = ShieldBlockContext.open((LivingEntity) (Object) this, source, amount)) {
            return original.call(level, source, amount);
        }
    }

    @com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation(
            method = "hurtServer",
            at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float customEnchants$recordVanillaBlock(LivingEntity defender, ServerLevel level, DamageSource source, float amount,
                                                      Operation<Float> original) {
        float blocked = original.call(defender, level, source, amount);
        ShieldBlockContext.recordVanillaBlock(defender, source, blocked > 0f);
        return blocked;
    }
}
