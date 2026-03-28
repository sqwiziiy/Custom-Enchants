package com.mentality.customenchants.mixin;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(LivingEntity.class)
public abstract class ShieldFeedbackMixin {

    /** Returns true if this entity is a player actively blocking with a Feedback shield. */
    @Unique
    private boolean customEnchants$hasFeedbackShield() {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) return false;
        if (!player.isBlocking()) return false;
        if (!ModConfig.get().feedbackEnabled) return false;
        ItemStack shield = player.getUseItem();
        if (shield.isEmpty() || !(shield.getItem() instanceof ShieldItem)) return false;
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FEEDBACK, shield) > 0;
    }

    /**
     * Cancel ALL magic-type damage (including instant damage from harming arrows/potions)
     * when the player is actively blocking with a Feedback shield.
     * This runs BEFORE the damage is applied, so it fully negates instant damage as well.
     */
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onFeedbackCancelMagic(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity directEntity = source.getDirectEntity();
        boolean isMagicAttack = source.is(DamageTypes.MAGIC)
                || source.is(DamageTypes.INDIRECT_MAGIC)
                || directEntity instanceof ShulkerBullet;
        if (!isMagicAttack) return;
        if (!customEnchants$hasFeedbackShield()) return;
        // Purge any active harmful effects and fully block the magic damage
        Player player = (Player) (Object) this;
        List<MobEffect> toRemove = player.getActiveEffects().stream()
                .filter(e -> e.getEffect().getCategory() == MobEffectCategory.HARMFUL)
                .map(MobEffectInstance::getEffect)
                .collect(Collectors.toList());
        toRemove.forEach(player::removeEffect);
        // Repair the shield slightly as a reward for blocking magic
        ItemStack shield = player.getUseItem();
        shield.setDamageValue(Math.max(0, shield.getDamageValue() - ModConfig.get().feedbackRepairAmount));
        cir.setReturnValue(false);
    }

    /**
     * On any blocked physical hit: purge ALL active harmful effects from the player.
     * Magic hits are already fully cancelled in onFeedbackCancelMagic.
     */
    @Inject(method = "hurt", at = @At("RETURN"))
    private void onFeedbackHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!customEnchants$hasFeedbackShield()) return;
        // Skip if no damage was actually applied (e.g. magic was already cancelled above)
        if (!cir.getReturnValue()) return;
        Player player = (Player) (Object) this;

        // Purge ALL currently-active harmful effects
        List<MobEffect> toRemove = player.getActiveEffects().stream()
                .filter(e -> e.getEffect().getCategory() == MobEffectCategory.HARMFUL)
                .map(MobEffectInstance::getEffect)
                .collect(Collectors.toList());
        toRemove.forEach(player::removeEffect);
    }

    /** Pre-emptively cancel any harmful effect being added via addEffect() while blocking. */
    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"), cancellable = true)
    private void onAddEffect(MobEffectInstance effectInstance, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (effectInstance.getEffect().getCategory() != MobEffectCategory.HARMFUL) return;
        if (!customEnchants$hasFeedbackShield()) return;
        cir.setReturnValue(false);
    }

    /** Also intercept forceAddEffect so nothing bypasses the protection. */
    @Inject(method = "forceAddEffect", at = @At("HEAD"), cancellable = true)
    private void onForceAddEffect(MobEffectInstance effectInstance, Entity source, CallbackInfo ci) {
        if (effectInstance.getEffect().getCategory() != MobEffectCategory.HARMFUL) return;
        if (!customEnchants$hasFeedbackShield()) return;
        ci.cancel();
    }
}
