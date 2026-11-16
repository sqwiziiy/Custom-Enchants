package com.mentality.customenchants.kinetic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class KineticDischargeTargetPolicy {
    private KineticDischargeTargetPolicy() {
    }

    public static boolean withinRadius(double dx, double dy, double dz, double radius) {
        return Double.isFinite(dx) && Double.isFinite(dy) && Double.isFinite(dz)
                && Double.isFinite(radius) && radius >= 0.0D
                && dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    public static boolean finiteHorizontalVector(double dx, double dz) {
        return Double.isFinite(dx) && Double.isFinite(dz);
    }

    public static boolean isEligible(ServerPlayer owner, LivingEntity target, double radius) {
        if (owner == null || target == null || target == owner || target.level() != owner.level()
                || !target.isAlive() || target.isRemoved() || target.isSpectator()) return false;
        if (!withinRadius(target.getX() - owner.getX(), target.getY() - owner.getY(),
                target.getZ() - owner.getZ(), radius)) return false;
        return !(target instanceof Player other) || owner.canHarmPlayer(other);
    }
}
