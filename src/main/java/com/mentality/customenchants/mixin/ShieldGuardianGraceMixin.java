package com.mentality.customenchants.mixin;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.shield.ShieldBlockContext;
import com.mentality.customenchants.shield.ShieldEnchantmentsPolicy;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ShieldGuardianGraceMixin {
    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void customEnchants$onConfirmedBlock(ServerLevel serverLevel, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity defender = (LivingEntity) (Object) this;
        if (!(defender instanceof Player player) || !ModConfig.get().guardiansGraceEnabled) return;
        ShieldBlockContext.Evidence evidence = ShieldBlockContext.current(defender, source);
        if (!evidence.vanillaBlocked()) return;
        int level = ShieldEnchantmentsPolicy.guardiansGraceLevel(evidence.shield());
        if (level <= 0) return;
        int chance = switch (level) {
            case 1 -> ModConfig.get().guardiansGraceChanceL1;
            case 2 -> ModConfig.get().guardiansGraceChanceL2;
            default -> ModConfig.get().guardiansGraceChanceL3;
        };
        chance = Math.max(0, Math.min(100, chance));
        if (player.getRandom().nextInt(100) >= chance) return;
        FoodData food = player.getFoodData();
        food.setFoodLevel(Math.min(20, food.getFoodLevel() + 1));
        if (level >= 3 && player.getRandom().nextInt(100) < 10) player.heal(2.0f);
    }
}
