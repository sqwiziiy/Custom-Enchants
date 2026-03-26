package com.mentality.customenchants.mixin;

import com.mentality.customenchants.CustomEnchantsMod;
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
    private void onShadowBladeHit(EntityHitResult result, CallbackInfo ci) {
        ThrownTrident self = (ThrownTrident) (Object) this;
        Entity owner = self.getOwner();
        Entity hitEntity = result.getEntity();

        CustomEnchantsMod.LOGGER.info("[ShadowBlade] Trident hit! Owner: {}, Target: {}, Item: {}",
                owner != null ? owner.getClass().getSimpleName() : "null",
                hitEntity != null ? hitEntity.getClass().getSimpleName() : "null",
                this.tridentItem);

        if (owner instanceof Player player && hitEntity instanceof LivingEntity livingTarget) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(
                    ModEnchantments.SHADOW_BLADE, this.tridentItem);
            CustomEnchantsMod.LOGGER.info("[ShadowBlade] Enchantment level: {}", level);
            if (level > 0) {
                ShadowBladeEnchantment.applyShadowBlade(player, livingTarget, level);
            }
        }
    }
}
