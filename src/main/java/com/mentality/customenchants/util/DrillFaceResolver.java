package com.mentality.customenchants.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

/**
 * Resolves the face of the block that the player's view ray enters.
 *
 * <p>The old Drill implementation inferred the face from the player's eye position relative to
 * the block centre. That ignores the actual camera direction and becomes unstable near diagonal
 * viewing angles. This resolver uses the view ray against the mined block's unit cube and falls
 * back to the dominant view axis only when the ray no longer intersects the cube.</p>
 */
public final class DrillFaceResolver {
    private static final double EPSILON = 1.0e-7;

    private DrillFaceResolver() {
    }

    public static Direction resolve(Vec3 eyePosition, Vec3 lookDirection, BlockPos blockPos) {
        if (eyePosition == null || lookDirection == null || blockPos == null) return null;
        if (!finite(eyePosition) || !finite(lookDirection) || lookDirection.lengthSqr() <= EPSILON * EPSILON) {
            return null;
        }

        Direction rayFace = rayBoxEntryFace(eyePosition, lookDirection, blockPos);
        return rayFace != null ? rayFace : dominantViewFace(lookDirection);
    }

    private static Direction rayBoxEntryFace(Vec3 origin, Vec3 direction, BlockPos blockPos) {
        AxisInterval x = interval(origin.x, direction.x, blockPos.getX(), blockPos.getX() + 1.0,
                Direction.WEST, Direction.EAST);
        AxisInterval y = interval(origin.y, direction.y, blockPos.getY(), blockPos.getY() + 1.0,
                Direction.DOWN, Direction.UP);
        AxisInterval z = interval(origin.z, direction.z, blockPos.getZ(), blockPos.getZ() + 1.0,
                Direction.NORTH, Direction.SOUTH);
        if (x == null || y == null || z == null) return null;

        double enter = Math.max(x.near(), Math.max(y.near(), z.near()));
        double exit = Math.min(x.far(), Math.min(y.far(), z.far()));
        if (enter < 0.0 || exit + EPSILON < enter) return null;

        Direction result = null;
        double bestStrength = -1.0;
        if (x.face() != null && close(x.near(), enter)) {
            result = x.face();
            bestStrength = Math.abs(direction.x);
        }
        if (y.face() != null && close(y.near(), enter) && Math.abs(direction.y) > bestStrength + EPSILON) {
            result = y.face();
            bestStrength = Math.abs(direction.y);
        }
        if (z.face() != null && close(z.near(), enter) && Math.abs(direction.z) > bestStrength + EPSILON) {
            result = z.face();
        }
        return result;
    }

    private static AxisInterval interval(double origin, double direction, double min, double max,
                                         Direction minFace, Direction maxFace) {
        if (Math.abs(direction) <= EPSILON) {
            if (origin < min - EPSILON || origin > max + EPSILON) return null;
            return new AxisInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
        }

        if (direction > 0.0) {
            return new AxisInterval((min - origin) / direction, (max - origin) / direction, minFace);
        }
        return new AxisInterval((max - origin) / direction, (min - origin) / direction, maxFace);
    }

    private static Direction dominantViewFace(Vec3 direction) {
        double x = Math.abs(direction.x);
        double y = Math.abs(direction.y);
        double z = Math.abs(direction.z);

        if (y >= x && y >= z) return direction.y > 0.0 ? Direction.DOWN : Direction.UP;
        if (x >= z) return direction.x > 0.0 ? Direction.WEST : Direction.EAST;
        return direction.z > 0.0 ? Direction.NORTH : Direction.SOUTH;
    }

    private static boolean finite(Vec3 vector) {
        return Double.isFinite(vector.x) && Double.isFinite(vector.y) && Double.isFinite(vector.z);
    }

    private static boolean close(double first, double second) {
        return Math.abs(first - second) <= EPSILON;
    }

    private record AxisInterval(double near, double far, Direction face) {
    }
}
