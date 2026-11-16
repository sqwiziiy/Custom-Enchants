package com.mentality.customenchants.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DrillBlockPlanner {
    private DrillBlockPlanner() {
    }

    public static List<BlockPos> plan(BlockPos origin, Direction minedFace) {
        if (origin == null || minedFace == null) return List.of();

        Direction[] perpendicular = perpendicularDirections(minedFace);
        List<BlockPos> planned = new ArrayList<>(8);
        for (int first = -1; first <= 1; first++) {
            for (int second = -1; second <= 1; second++) {
                if (first == 0 && second == 0) continue;
                BlockPos target = offsetSafely(origin, perpendicular[0], first, perpendicular[1], second);
                if (target == null) return List.of();
                planned.add(target);
            }
        }
        return Collections.unmodifiableList(planned);
    }

    private static Direction[] perpendicularDirections(Direction face) {
        return switch (face) {
            case UP, DOWN -> new Direction[]{Direction.NORTH, Direction.EAST};
            case NORTH, SOUTH -> new Direction[]{Direction.EAST, Direction.UP};
            case EAST, WEST -> new Direction[]{Direction.NORTH, Direction.UP};
        };
    }

    private static BlockPos offsetSafely(BlockPos origin, Direction first, int firstAmount,
                                         Direction second, int secondAmount) {
        long x = origin.getX() + (long) first.getStepX() * firstAmount + (long) second.getStepX() * secondAmount;
        long y = origin.getY() + (long) first.getStepY() * firstAmount + (long) second.getStepY() * secondAmount;
        long z = origin.getZ() + (long) first.getStepZ() * firstAmount + (long) second.getStepZ() * secondAmount;
        if (x < Integer.MIN_VALUE || x > Integer.MAX_VALUE
                || y < Integer.MIN_VALUE || y > Integer.MAX_VALUE
                || z < Integer.MIN_VALUE || z > Integer.MAX_VALUE) {
            return null;
        }
        return new BlockPos((int) x, (int) y, (int) z);
    }
}
