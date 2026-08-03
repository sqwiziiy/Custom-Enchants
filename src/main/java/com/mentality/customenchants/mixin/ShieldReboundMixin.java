package com.mentality.customenchants.mixin;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.shield.ShieldBlockContext;
import com.mentality.customenchants.shield.ShieldEnchantmentsPolicy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ShieldReboundMixin {
    @Inject(method = "hurt", at = @At("RETURN"))
    private void customEnchants$onConfirmedBlock(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity defender = (LivingEntity) (Object) this;
        if (!(defender instanceof Player player) || !ModConfig.get().reboundEnabled) return;
        ShieldBlockContext.Evidence evidence = ShieldBlockContext.current(defender, source);
        if (!evidence.vanillaBlocked()) return;
        ItemStack shield = evidence.shield();
        int level = ShieldEnchantmentsPolicy.reboundLevel(shield);
        if (level <= 0) return;

        // Rebound is melee-only: never substitute a distant projectile shooter for the direct attacker.
        Entity direct = evidence.directEntity();
        if (!(direct instanceof LivingEntity attacker) || direct instanceof Projectile
                || attacker.isRemoved() || attacker.level() != defender.level()) return;
        Vec3 delta = attacker.position().subtract(defender.position());
        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        if (!Double.isFinite(horizontal) || horizontal < 0.01) return;

        double strength = switch (level) {
            case 1 -> ModConfig.get().reboundKnockbackL1 / 10.0;
            case 2 -> ModConfig.get().reboundKnockbackL2 / 10.0;
            default -> ModConfig.get().reboundKnockbackL3 / 10.0;
        };
        if (!Double.isFinite(strength) || strength <= 0) return;
        double nx = delta.x / horizontal;
        double nz = delta.z / horizontal;
        attacker.setDeltaMovement(attacker.getDeltaMovement().add(nx * strength, 0.1, nz * strength));
        attacker.hurtMarked = true;
        double selfStrength = strength * 0.15;
        player.setDeltaMovement(player.getDeltaMovement().add(-nx * selfStrength, 0.02, -nz * selfStrength));
        player.hurtMarked = true;
    }
}
