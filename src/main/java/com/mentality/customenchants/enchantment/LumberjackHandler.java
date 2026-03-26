package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class LumberjackHandler {

    private static boolean isFelling = false;

    // Max blocks per level: L1 = small trees, L2 = medium, L3 = large
    private static final int MAX_BLOCKS_L1 = 16;
    private static final int MAX_BLOCKS_L2 = 48;
    private static final int MAX_BLOCKS_L3 = 128;

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (isFelling) return;
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (!ModConfig.get().lumberjackEnabled) return;

            if (!(world instanceof ServerLevel serverLevel)) return;

            if (!state.is(BlockTags.LOGS)) return;

            ItemStack tool = player.getMainHandItem();
            if (tool.isEmpty()) return;

            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LUMBERJACK, tool);
            if (level <= 0) return;

            if (!isPartOfTree(serverLevel, pos, state)) return;

            int maxBlocks = switch (level) {
                case 1 -> ModConfig.get().lumberjackMaxBlocksL1;
                case 2 -> ModConfig.get().lumberjackMaxBlocksL2;
                case 3 -> ModConfig.get().lumberjackMaxBlocksL3;
                default -> ModConfig.get().lumberjackMaxBlocksL1;
            };

            isFelling = true;
            try {
                fellTree(serverPlayer, pos, state, maxBlocks);
            } finally {
                isFelling = false;
            }
        });
    }

    /**
     * Verify the broken log is part of a natural tree by checking:
     * 1. There are connected logs going upward
     * 2. There are leaves or wart blocks near the top logs
     */
    private static boolean isPartOfTree(ServerLevel world, BlockPos brokenPos, BlockState brokenState) {
        Block logBlock = brokenState.getBlock();

        // Find the highest connected log above the broken position
        BlockPos topLog = brokenPos;
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(brokenPos);
        visited.add(brokenPos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            for (BlockPos neighbor : getTreeNeighbors(current)) {
                if (visited.contains(neighbor)) continue;
                BlockState neighborState = world.getBlockState(neighbor);
                if (neighborState.is(BlockTags.LOGS) && neighborState.getBlock() == logBlock) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    if (neighbor.getY() > topLog.getY()) {
                        topLog = neighbor;
                    }
                }
                if (visited.size() > 200) break;
            }
            if (visited.size() > 200) break;
        }

        // Check if there are leaves or wart blocks near the top logs
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos leafCheck = topLog.offset(dx, dy, dz);
                    BlockState checkState = world.getBlockState(leafCheck);
                    if (checkState.is(BlockTags.LEAVES) || checkState.is(BlockTags.WART_BLOCKS)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Fell only the log blocks of the tree using BFS.
     * Only searches upward and sideways (not below the broken block) to avoid
     * breaking log walls or structures built by players.
     */
    private static void fellTree(ServerPlayer player, BlockPos brokenPos, BlockState brokenState, int maxBlocks) {
        Block logBlock = brokenState.getBlock();
        ServerLevel level = player.serverLevel();
        ItemStack tool = player.getMainHandItem();

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        // Seed BFS from neighbors of the broken block (it's already broken)
        visited.add(brokenPos);
        for (BlockPos neighbor : getTreeNeighbors(brokenPos)) {
            if (neighbor.getY() < brokenPos.getY()) continue; // don't go below
            BlockState neighborState = level.getBlockState(neighbor);
            if (neighborState.is(BlockTags.LOGS) && neighborState.getBlock() == logBlock) {
                queue.add(neighbor);
                visited.add(neighbor);
            }
        }

        int broken = 0;
        while (!queue.isEmpty() && broken < maxBlocks) {
            BlockPos current = queue.poll();
            BlockState currentState = level.getBlockState(current);

            // Double-check it's still a matching log
            if (!currentState.is(BlockTags.LOGS) || currentState.getBlock() != logBlock) continue;

            // Break the block
            BlockEntity be = level.getBlockEntity(current);
            Block.dropResources(currentState, level, current, be, player, tool);
            level.removeBlock(current, false);
            broken++;

            tool.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            if (tool.isEmpty()) return;

            // Add neighbors (only at same level or above)
            for (BlockPos neighbor : getTreeNeighbors(current)) {
                if (visited.contains(neighbor)) continue;
                if (neighbor.getY() < brokenPos.getY()) continue; // never go below the original block
                BlockState neighborState = level.getBlockState(neighbor);
                if (neighborState.is(BlockTags.LOGS) && neighborState.getBlock() == logBlock) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }

    /**
     * Returns all 26 neighbors in a 3×3×3 cube (excluding center).
     * This ensures branches in any direction are found (large oak, dark oak, jungle, etc.)
     */
    private static List<BlockPos> getTreeNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>(26);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    neighbors.add(pos.offset(dx, dy, dz));
                }
            }
        }
        return neighbors;
    }
}
