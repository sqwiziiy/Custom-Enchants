package com.mentality.customenchants.shadowblade;

import java.util.ArrayList;
import java.util.List;

/** Pure, bounded and deterministic destination ordering for Shadow Blade. */
public final class ShadowBladeTeleportPlanner {
    private static final double BASE_DISTANCE = 1.5D;
    private static final double SIDE_OFFSET = 0.55D;
    private static final double EPSILON = 1.0E-6D;

    private ShadowBladeTeleportPlanner() {
    }

    public record Candidate(double x, double y, double z) {
        public boolean isFinite() {
            return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
        }
    }

    public static List<Candidate> candidates(double targetX, double targetY, double targetZ,
                                              float targetYaw, double targetWidth, double attackerWidth) {
        if (!Double.isFinite(targetX) || !Double.isFinite(targetY) || !Double.isFinite(targetZ)
                || !Float.isFinite(targetYaw) || !Double.isFinite(targetWidth) || !Double.isFinite(attackerWidth)) {
            return List.of();
        }

        double yaw = Math.toRadians(targetYaw);
        double backX = Math.sin(yaw);
        double backZ = -Math.cos(yaw);
        double sideX = Math.cos(yaw);
        double sideZ = Math.sin(yaw);
        double distance = Math.max(BASE_DISTANCE, targetWidth / 2.0D + attackerWidth / 2.0D + 0.05D);

        List<Candidate> result = new ArrayList<>(7);
        add(result, targetX + backX * distance, targetY, targetZ + backZ * distance);
        add(result, targetX + backX * distance + sideX * SIDE_OFFSET, targetY, targetZ + backZ * distance + sideZ * SIDE_OFFSET);
        add(result, targetX + backX * distance - sideX * SIDE_OFFSET, targetY, targetZ + backZ * distance - sideZ * SIDE_OFFSET);
        add(result, targetX + backX * (distance + 0.55D), targetY, targetZ + backZ * (distance + 0.55D));
        add(result, targetX + backX * Math.max(1.0D, distance - 0.4D), targetY, targetZ + backZ * Math.max(1.0D, distance - 0.4D));
        add(result, targetX + backX * distance, targetY + 1.0D, targetZ + backZ * distance);
        add(result, targetX + backX * distance, targetY - 1.0D, targetZ + backZ * distance);
        return List.copyOf(result);
    }

    private static void add(List<Candidate> result, double x, double y, double z) {
        Candidate candidate = new Candidate(x, y, z);
        if (!candidate.isFinite() || result.stream().anyMatch(existing -> same(existing, candidate))) {
            return;
        }
        result.add(candidate);
    }

    private static boolean same(Candidate left, Candidate right) {
        return Math.abs(left.x - right.x) < EPSILON
                && Math.abs(left.y - right.y) < EPSILON
                && Math.abs(left.z - right.z) < EPSILON;
    }
}
