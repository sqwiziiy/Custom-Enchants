package com.mentality.customenchants.mixin;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.shield.ShieldBlockContext;
import com.mentality.customenchants.shield.FeedbackMagicBlockPolicy;
import com.mentality.customenchants.shield.ShieldEnchantmentsPolicy;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ShieldFeedbackMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void customEnchants$blockAllowlistedMagic(DamageSource source, float amount,
                                                       CallbackInfoReturnable<Boolean> cir) {
        LivingEntity defender = (LivingEntity) (Object) this;
        if (!(defender instanceof Player player)) return;
        ItemStack shield = player.getUseItem();
        boolean allowed = FeedbackMagicBlockPolicy.allowedSource(
                source.is(DamageTypes.MAGIC), source.is(DamageTypes.INDIRECT_MAGIC),
                source.getDirectEntity() instanceof ShulkerBullet,
                source.is(DamageTypeTags.BYPASSES_SHIELD));
        boolean canBlock = FeedbackMagicBlockPolicy.shouldBlock(
                ModConfig.get().feedbackEnabled,
                ShieldEnchantmentsPolicy.feedbackLevel(shield) > 0,
                player.isDamageSourceBlocked(source), allowed);
        if (!canBlock) return;

        for (net.minecraft.world.effect.MobEffectInstance effect :
                new java.util.ArrayList<>(player.getActiveEffects())) {
            if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                player.removeEffect(effect.getEffect());
            }
        }
        player.heal(Math.max(0.0f, ModConfig.get().feedbackHealAmount));
        int repair = Math.max(0, ModConfig.get().feedbackRepairAmount);
        if (repair > 0) shield.setDamageValue(Math.max(0, shield.getDamageValue() - repair));
        cir.setReturnValue(false);
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void customEnchants$onConfirmedMagicBlock(DamageSource source, float amount,
                                                       CallbackInfoReturnable<Boolean> cir) {
        LivingEntity defender = (LivingEntity) (Object) this;
        if (!(defender instanceof Player player) || !ModConfig.get().feedbackEnabled) return;
        ShieldBlockContext.Evidence evidence = ShieldBlockContext.current(defender, source);
        if (!evidence.vanillaBlocked() || !ShieldEnchantmentsPolicy.feedbackDamage(source)) return;
        ItemStack shield = evidence.shield();
        if (ShieldEnchantmentsPolicy.feedbackLevel(shield) <= 0) return;

        player.heal(Math.max(0.0f, ModConfig.get().feedbackHealAmount));
        int repair = Math.max(0, ModConfig.get().feedbackRepairAmount);
        if (repair > 0) shield.setDamageValue(Math.max(0, shield.getDamageValue() - repair));
    }
}
