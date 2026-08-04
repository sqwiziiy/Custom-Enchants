package com.mentality.customenchants.service;

import com.mentality.customenchants.util.ScopedReentrancyGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class AdditionalBlockBreakService {
    private static final ScopedReentrancyGuard<UUID> GUARD = new ScopedReentrancyGuard<>();

    private AdditionalBlockBreakService() {
    }

    public static boolean isChainActive(ServerPlayer player) {
        return player != null && GUARD.isActive(player.getUUID());
    }

    public static int destroyPlanned(ServerPlayer player, List<BlockPos> plannedPositions) {
        if (player == null || plannedPositions == null || plannedPositions.isEmpty()) return 0;
        ScopedReentrancyGuard<UUID>.Scope scope = GUARD.tryEnter(player.getUUID());
        if (scope == null) return 0;

        try (scope) {
            ServerLevel level = player.level();
            int destroyed = 0;
            Set<BlockPos> attempted = new HashSet<>();
            for (BlockPos position : plannedPositions) {
                if (!attempted.add(position)) continue;
                if (!isSafeTarget(player, level, position)) break;
                if (!player.gameMode.destroyBlock(position)) break;
                destroyed++;
                if (!hasUsableTool(player)) break;
            }
            return destroyed;
        }
    }

    private static boolean isSafeTarget(ServerPlayer player, ServerLevel level, BlockPos position) {
        if (player.isRemoved() || !player.isAlive() || position == null || position.equals(BlockPos.ZERO)) return false;
        if (player.level() != level || !level.getWorldBorder().isWithinBounds(position)) return false;
        if (!level.getChunkSource().hasChunk(position.getX() >> 4, position.getZ() >> 4)) return false;

        BlockState state = level.getBlockState(position);
        if (state.isAir() || state.getDestroySpeed(level, position) < 0) return false;
        if (level.getBlockEntity(position) != null) return false;
        return hasUsableTool(player) && player.hasCorrectToolForDrops(state);
    }

    private static boolean hasUsableTool(ServerPlayer player) {
        ItemStack tool = player.getMainHandItem();
        return !tool.isEmpty() && (!tool.isDamageableItem() || tool.getDamageValue() < tool.getMaxDamage());
    }
}
