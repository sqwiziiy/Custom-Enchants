package com.mentality.customenchants.mixin;

import com.mentality.customenchants.enchantment.AutoSmeltHandler;
import com.mentality.customenchants.util.AutoSmeltBreakContext;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public abstract class AutoSmeltBlockDropsMixin {

    @WrapMethod(method = "playerDestroy")
    private void customEnchants$withBreakContext(
            Level level,
            Player player,
            BlockPos pos,
            BlockState state,
            BlockEntity blockEntity,
            ItemStack tool,
            Operation<Void> original
    ) {
        if (!(level instanceof ServerLevel serverLevel)
                || !(player instanceof ServerPlayer serverPlayer)
                || !AutoSmeltHandler.isEligible(tool)) {
            original.call(level, player, pos, state, blockEntity, tool);
            return;
        }

        try (AutoSmeltBreakContext.Scope ignored = AutoSmeltBreakContext.open(
                serverPlayer, serverLevel, pos, state, blockEntity, tool)) {
            original.call(level, player, pos, state, blockEntity, tool);
        }
    }

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
        if (!AutoSmeltBreakContext.consume(level, pos, state, blockEntity, breaker, tool)) return;
        List<ItemStack> transformed = AutoSmeltHandler.transformDrops(
                level, state, pos, blockEntity, breaker, tool, cir.getReturnValue());
        cir.setReturnValue(transformed);
    }
}
