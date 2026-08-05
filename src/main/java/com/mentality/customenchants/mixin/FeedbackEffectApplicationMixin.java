package com.mentality.customenchants.mixin;

import com.mentality.customenchants.shield.FeedbackEffectPolicy;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class FeedbackEffectApplicationMixin {
    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"), cancellable = true)
    private void customEnchants$blockHarmfulEffectFromFrontImpact(
            MobEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (effect != null && effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL
                && FeedbackEffectPolicy.shouldBlock((LivingEntity) (Object) this, source)) {
            cir.setReturnValue(false);
        }
    }
}
