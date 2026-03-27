package com.mentality.customenchants.mixin;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ShieldFeedbackMixin {

    @Inject(method = "hurt", at = @At("RETURN"))
    private void onMagicBlockFeedback(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) return;
        if (!player.isBlocking()) return;
        if (!ModConfig.get().feedbackEnabled) return;

        // Check if the damage source is magical
        Entity directEntity = source.getDirectEntity();
        boolean isMagicAttack = source.is(DamageTypes.MAGIC)
                || source.is(DamageTypes.INDIRECT_MAGIC)
                || directEntity instanceof ShulkerBullet;
        if (!isMagicAttack) return;

        // Check for Feedback enchantment on the active shield
        ItemStack shield = player.getUseItem();
        if (shield.isEmpty() || !(shield.getItem() instanceof ShieldItem)) return;

        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FEEDBACK, shield);
        if (level <= 0) return;

        // Heal player (1 heart = 2 HP)
        float healAmount = ModConfig.get().feedbackHealAmount;
        player.heal(healAmount);

        // Repair shield durability
        int repairAmount = ModConfig.get().feedbackRepairAmount;
        shield.setDamageValue(Math.max(0, shield.getDamageValue() - repairAmount));
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"), cancellable = true)
    private void onAddEffect(MobEffectInstance effectInstance, Entity source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) return;
        if (!player.isBlocking()) return;
        if (!ModConfig.get().feedbackEnabled) return;

        // Only block harmful effects
        if (effectInstance.getEffect().getCategory() != MobEffectCategory.HARMFUL) return;

        // Check for Feedback enchantment on the active shield
        ItemStack shield = player.getUseItem();
        if (shield.isEmpty() || !(shield.getItem() instanceof ShieldItem)) return;

        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FEEDBACK, shield);
        if (level <= 0) return;

        // Cancel the harmful effect
        cir.setReturnValue(false);
    }
}
