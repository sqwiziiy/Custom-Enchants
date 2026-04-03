package com.mentality.customenchants.mixin;

import com.mentality.customenchants.enchantment.GlowStrikeEnchantment;
import com.mentality.customenchants.enchantment.ModEnchantments;
import com.mentality.customenchants.enchantment.ShadowBladeEnchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin {

    @Shadow
    private ItemStack tridentItem;

    @Inject(method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", at = @At("HEAD"))
    private void onTridentHitEntity(EntityHitResult result, CallbackInfo ci) {
        ThrownTrident self = (ThrownTrident) (Object) this;
        Entity owner = self.getOwner();
        Entity hitEntity = result.getEntity();

        if (owner instanceof Player player && hitEntity instanceof LivingEntity livingTarget) {
            // Shadow Blade
            int shadowLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    ModEnchantments.SHADOW_BLADE, this.tridentItem);
            if (shadowLevel > 0) {
                ShadowBladeEnchantment.applyShadowBlade(player, livingTarget, shadowLevel);
            }

            // Glow Strike
            int glowLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    ModEnchantments.GLOW_STRIKE, this.tridentItem);
            if (glowLevel > 0) {
                GlowStrikeEnchantment.applyGlowStrike(player, livingTarget, glowLevel);
            }
        }
    }
}
