package com.mentality.customenchants.mixin;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.enchantment.ModEnchantments;
import com.mentality.customenchants.enchantment.TetherMasterEnchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public abstract class TetherMasterMixin {

    @Shadow
    public Entity hookedIn;

    // Capture hookedIn before vanilla clears it inside retrieve()
    @Unique
    private Entity customEnchants$capturedHookedIn;

    @Inject(method = "retrieve", at = @At("HEAD"))
    private void onRetrieveHead(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        customEnchants$capturedHookedIn = this.hookedIn;
    }

    @Inject(method = "retrieve", at = @At("RETURN"))
    private void onRetrieve(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        Entity caught = customEnchants$capturedHookedIn;
        customEnchants$capturedHookedIn = null;

        if (caught == null) return;
        if (!ModConfig.get().tetherMasterEnabled) return;

        FishingHook self = (FishingHook) (Object) this;

        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.TETHER_MASTER, stack);
        if (level <= 0) return;

        Entity ownerEntity = self.getOwner();
        if (!(ownerEntity instanceof Player owner)) return;

        float multiplier = TetherMasterEnchantment.getPullMultiplier(level);
        double bonus = multiplier - 1.0;

        // At RETURN, vanilla has already set deltaMovement = (delta * 0.1)
        // We add bonus impulse on top so total = delta * 0.1 * multiplier
        double dx = owner.getX() - caught.getX();
        double dy = owner.getY() - caught.getY();
        double dz = owner.getZ() - caught.getZ();

        Vec3 current = caught.getDeltaMovement();
        caught.setDeltaMovement(
                current.x + dx * 0.1 * bonus,
                current.y + dy * 0.1 * bonus + 0.1 * bonus,
                current.z + dz * 0.1 * bonus
        );
        caught.hasImpulse = true;
    }
}
