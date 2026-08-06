package com.mentality.customenchants.mixin;

import com.mentality.customenchants.enchantment.AutoSmeltHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public abstract class AutoSmeltBlockDropsMixin {

    @Inject(
            method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void customEnchants$transformPlayerDrops(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            BlockEntity blockEntity,
            Entity breaker,
            ItemStack tool,
            CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        List<ItemStack> transformed = AutoSmeltHandler.transformDrops(
                level, state, pos, blockEntity, breaker, tool, cir.getReturnValue());
        cir.setReturnValue(transformed);
    }
}
