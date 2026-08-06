package com.mentality.customenchants.util;

import net.minecraft.core.BlockPos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

public final class LumberjackBlockPlanner {
    private LumberjackBlockPlanner() {
    }

    /**
     * Plans additional connected blocks without reading or mutating a world.
     * A false loaded predicate stops the chain conservatively.
     */
    public static List<BlockPos> plan(BlockPos origin, int maxBlocks,
                                      Predicate<BlockPos> isMatching,
                                      Predicate<BlockPos> isLoaded) {
        if (origin == null || maxBlocks <= 0 || isMatching == null || isLoaded == null) return List.of();

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        List<BlockPos> planned = new ArrayList<>(maxBlocks);
        visited.add(origin);
        enqueueNeighbors(origin, origin, queue, visited, isMatching, isLoaded);

        while (!queue.isEmpty() && planned.size() < maxBlocks) {
            BlockPos current = queue.remove();
            // An unloaded branch is not safe to traverse, but it must not discard already
            // discovered loaded branches of the same real tree.
            if (!isLoaded.test(current)) continue;
            if (!isMatching.test(current)) continue;
            planned.add(current);
            enqueueNeighbors(current, origin, queue, visited, isMatching, isLoaded);
        }
        return Collections.unmodifiableList(planned);
    }

    private static void enqueueNeighbors(BlockPos current, BlockPos origin, Queue<BlockPos> queue,
                                          Set<BlockPos> visited, Predicate<BlockPos> isMatching,
                                          Predicate<BlockPos> isLoaded) {
        for (BlockPos neighbor : neighbors(current)) {
            if (visited.contains(neighbor) || neighbor.getY() < origin.getY()) continue;
            visited.add(neighbor);
            if (!isLoaded.test(neighbor)) {
                queue.add(neighbor);
            } else if (isMatching.test(neighbor)) {
                queue.add(neighbor);
            }
        }
    }

    public static List<BlockPos> neighbors(BlockPos pos) {
        if (pos == null) return List.of();
        List<BlockPos> result = new ArrayList<>(26);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    result.add(pos.offset(dx, dy, dz));
                }
            }
        }
        return Collections.unmodifiableList(result);
    }
}
