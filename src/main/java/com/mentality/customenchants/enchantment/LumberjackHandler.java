package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.service.AdditionalBlockBreakService;
import com.mentality.customenchants.util.LumberjackBlockPlanner;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class LumberjackHandler {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (AdditionalBlockBreakService.isChainActive(serverPlayer)) return;
            if (!ModConfig.get().lumberjackEnabled || !(world instanceof ServerLevel serverLevel)) return;
            if (!isNaturalLog(state)) return;

            ItemStack tool = player.getMainHandItem();
            int level = EnchantmentAccess.getLevel(tool, ModEnchantments.LUMBERJACK);
            if (tool.isEmpty() || level <= 0 || !isPartOfTree(serverLevel, pos, state)) return;

            int maxBlocks = switch (level) {
                case 1 -> ModConfig.get().lumberjackMaxBlocksL1;
                case 2 -> ModConfig.get().lumberjackMaxBlocksL2;
                case 3 -> ModConfig.get().lumberjackMaxBlocksL3;
                default -> ModConfig.get().lumberjackMaxBlocksL1;
            };
            Block logBlock = state.getBlock();
            var planned = LumberjackBlockPlanner.plan(pos, maxBlocks,
                    target -> isNaturalLog(serverLevel.getBlockState(target))
                            && serverLevel.getBlockState(target).getBlock() == logBlock,
                    target -> serverLevel.getChunkSource().hasChunk(target.getX() >> 4, target.getZ() >> 4));
            boolean magnet = ModConfig.get().magnetEnabled
                    && EnchantmentAccess.getLevel(tool, ModEnchantments.MAGNET) > 0;
            Set<java.util.UUID> existing = magnet ? MagnetHandler.takePreBreakItems(serverPlayer) : Set.of();
            if (magnet) MagnetHandler.beginBatch(serverPlayer);
            try {
                // Each destroyBlock call enters its own Magnet child drop context through
                // MagnetBlockDropContextMixin; the batch only coordinates diagnostics/cleanup.
                var successful = new java.util.ArrayList<BlockPos>();
                successful.add(pos.immutable());
                successful.addAll(AdditionalBlockBreakService.destroyPlannedPositions(serverPlayer, planned));
                if (magnet) MagnetHandler.completeBatch(serverLevel, serverPlayer, successful, existing);
            } finally {
                if (magnet) MagnetHandler.completeBatch(serverLevel, serverPlayer, java.util.List.of(), existing);
            }
        });
    }

    private static boolean isPartOfTree(ServerLevel level, BlockPos origin, BlockState brokenState) {
        Block logBlock = brokenState.getBlock();
        BlockPos topLog = origin;
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(origin);
        visited.add(origin);

        while (!queue.isEmpty() && visited.size() <= 200) {
            BlockPos current = queue.remove();
            if (current.getY() > topLog.getY()) topLog = current;
            for (BlockPos neighbor : LumberjackBlockPlanner.neighbors(current)) {
                if (visited.contains(neighbor)) continue;
                if (!level.getChunkSource().hasChunk(neighbor.getX() >> 4, neighbor.getZ() >> 4)) return false;
                BlockState neighborState = level.getBlockState(neighbor);
                if (isNaturalLog(neighborState) && neighborState.getBlock() == logBlock) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos leafCheck = topLog.offset(dx, dy, dz);
                    if (!level.getChunkSource().hasChunk(leafCheck.getX() >> 4, leafCheck.getZ() >> 4)) return false;
                    BlockState checkState = level.getBlockState(leafCheck);
                    if (checkState.is(BlockTags.LEAVES) || checkState.is(BlockTags.WART_BLOCKS)) return true;
                }
            }
        }
        return false;
    }

    static boolean isNaturalLog(BlockState state) {
        if (state == null || !state.is(BlockTags.LOGS)) return false;
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String path = id.getPath();
        return path.endsWith("_log") || path.endsWith("_stem");
    }
}
