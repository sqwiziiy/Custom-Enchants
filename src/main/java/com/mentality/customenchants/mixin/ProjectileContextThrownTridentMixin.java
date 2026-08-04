package com.mentality.customenchants.mixin;

import com.mentality.customenchants.projectile.ProjectileEnchantmentContext;
import com.mentality.customenchants.projectile.ProjectileEnchantmentContextHolder;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    private void customEnchants$save(ValueOutput output, CallbackInfo ci) { customEnchants$context.save(output); }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void customEnchants$load(ValueInput input, CallbackInfo ci) { customEnchants$context = ProjectileEnchantmentContext.load(input); }
}
