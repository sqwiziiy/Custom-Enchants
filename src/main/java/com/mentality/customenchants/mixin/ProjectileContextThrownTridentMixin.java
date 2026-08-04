package com.mentality.customenchants.mixin;

import com.mentality.customenchants.projectile.ProjectileEnchantmentContext;
import com.mentality.customenchants.projectile.ProjectileEnchantmentContextHolder;
import com.mentality.customenchants.projectile.ProjectileEnchantmentContextNbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public abstract class ProjectileContextThrownTridentMixin implements ProjectileEnchantmentContextHolder {
    @Unique private ProjectileEnchantmentContext customEnchants$context = ProjectileEnchantmentContext.EMPTY;
    @Override public ProjectileEnchantmentContext customEnchants$getProjectileContext() { return customEnchants$context; }
    @Override public void customEnchants$setProjectileContext(ProjectileEnchantmentContext context) {
        customEnchants$context = context == null ? ProjectileEnchantmentContext.EMPTY : context;
    }
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void customEnchants$save(CompoundTag tag, CallbackInfo ci) { ProjectileEnchantmentContextNbt.save(tag, customEnchants$context); }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void customEnchants$load(CompoundTag tag, CallbackInfo ci) { customEnchants$context = ProjectileEnchantmentContextNbt.load(tag); }
}
