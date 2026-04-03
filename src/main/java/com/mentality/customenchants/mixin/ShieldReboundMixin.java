package com.mentality.customenchants.mixin;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ShieldReboundMixin {

    @Inject(method = "hurt", at = @At("RETURN"))
    private void onShieldBlock(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) return;
        if (!player.isBlocking()) return;
        if (!ModConfig.get().reboundEnabled) return;

        Entity attacker = source.getEntity();
        if (!(attacker instanceof LivingEntity livingAttacker)) return;

        // Check for Rebound enchantment on the active shield
        ItemStack shield = player.getUseItem();
        if (shield.isEmpty() || !(shield.getItem() instanceof ShieldItem)) return;

        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.REBOUND, shield);
        if (level <= 0) return;

        // Knockback strength: L1=0.5, L2=1.0, L3=2.0
        double knockbackStrength = switch (level) {
            case 1 -> ModConfig.get().reboundKnockbackL1 / 10.0;
            case 2 -> ModConfig.get().reboundKnockbackL2 / 10.0;
            case 3 -> ModConfig.get().reboundKnockbackL3 / 10.0;
            default -> ModConfig.get().reboundKnockbackL1 / 10.0;
        };

        // Calculate direction from player to attacker
        double dx = livingAttacker.getX() - player.getX();
        double dz = livingAttacker.getZ() - player.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 0.01) return;
        double nx = dx / dist;
        double nz = dz / dist;

        // Knock the attacker away from the player
        livingAttacker.setDeltaMovement(livingAttacker.getDeltaMovement().add(
                nx * knockbackStrength, 0.1, nz * knockbackStrength));
        livingAttacker.hurtMarked = true;

        // Slight self-knockback (player pushed back)
        double selfKnockback = knockbackStrength * 0.15;
        player.setDeltaMovement(player.getDeltaMovement().add(
                -nx * selfKnockback, 0.02, -nz * selfKnockback));
        player.hurtMarked = true;

        // Extra shield durability cost
        shield.hurtAndBreak(level, player, p -> p.broadcastBreakEvent(
                player.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND
                        ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
    }
}
