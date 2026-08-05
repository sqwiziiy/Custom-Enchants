package com.mentality.customenchants.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.ModEnchantments;
import com.mentality.customenchants.magnet.MagnetBreakDropContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class MagnetBlockDropContextMixin {
    @WrapMethod(method = "playerDestroy")
    private void customEnchants$openMagnetDropContext(
            Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity,
            ItemStack tool, Operation<Void> original) {
        if (!(level instanceof ServerLevel serverLevel)
                || !(player instanceof ServerPlayer serverPlayer)
                || EnchantmentAccess.getLevel(tool, ModEnchantments.MAGNET) <= 0) {
            original.call(level, player, pos, state, blockEntity, tool);
            return;
        }
        try (MagnetBreakDropContext.Scope ignored = MagnetBreakDropContext.open(serverLevel, serverPlayer, pos, tool)) {
            original.call(level, player, pos, state, blockEntity, tool);
        }
    }
}
