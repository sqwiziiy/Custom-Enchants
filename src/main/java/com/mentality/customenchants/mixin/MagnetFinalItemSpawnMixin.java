package com.mentality.customenchants.mixin;

import com.mentality.customenchants.enchantment.MagnetHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class MagnetFinalItemSpawnMixin {
    @Inject(method = "addFreshEntity", at = @At("RETURN"))
    private void customEnchants$captureFinalItemEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && entity instanceof ItemEntity item
                && (Object) this instanceof ServerLevel level) {
            MagnetHandler.captureFinalSpawn(level, item);
        }
    }
}
