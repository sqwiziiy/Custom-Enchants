package com.mentality.customenchants.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntity.class)
public interface ItemEntityPickupDelayAccessor {
    @Accessor("pickupDelay")
    int customEnchants$getPickupDelay();
}
