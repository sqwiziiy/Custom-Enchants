package com.mentality.customenchants.mixin;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ShieldGuardianGraceMixin {

    // Captured at HEAD — before hurt() can modify any state
    private ItemStack customEnchants$guardiansGraceShield = null;

    @Inject(method = "hurt", at = @At("HEAD"))
    private void captureShieldOnHurtHead(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        customEnchants$guardiansGraceShield = null;
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) return;
        if (!player.isBlocking()) return;
        if (!ModConfig.get().guardiansGraceEnabled) return;
        if (amount <= 0) return;

        // Capture the shield now, before the hurt() processing can disable it
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        ItemStack shield = (mainHand.getItem() instanceof ShieldItem) ? mainHand
                : (offHand.getItem() instanceof ShieldItem) ? offHand
                : ItemStack.EMPTY;
        if (shield.isEmpty()) return;

        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.GUARDIANS_GRACE, shield);
        if (level > 0) {
            customEnchants$guardiansGraceShield = shield;
        }
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void onShieldBlockGuardiansGrace(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemStack capturedShield = customEnchants$guardiansGraceShield;
        customEnchants$guardiansGraceShield = null;
        if (capturedShield == null) return;

        // Only apply if hurt() returned false (damage was fully blocked by the shield)
        if (cir.getReturnValue()) return;

        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) return;

        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.GUARDIANS_GRACE, capturedShield);
        if (level <= 0) return;

        // Chance based on level
        int chance = switch (level) {
            case 1 -> ModConfig.get().guardiansGraceChanceL1;
            case 2 -> ModConfig.get().guardiansGraceChanceL2;
            default -> ModConfig.get().guardiansGraceChanceL3;
        };

        if (player.getRandom().nextInt(100) < chance) {
            FoodData food = player.getFoodData();
            if (food.getFoodLevel() < 20) {
                food.setFoodLevel(Math.min(20, food.getFoodLevel() + 1));
            }
        }

        // Level III: additional rare 10% chance to restore 1 heart (2 HP)
        if (level >= 3 && player.getRandom().nextInt(100) < 10) {
            player.heal(2.0f);
        }
    }
}
