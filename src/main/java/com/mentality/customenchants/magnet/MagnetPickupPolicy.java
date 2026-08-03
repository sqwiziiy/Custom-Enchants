package com.mentality.customenchants.magnet;

import com.mentality.customenchants.mixin.ItemEntityOwnershipAccessor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/** Ownership and eligibility policy applied before using vanilla ItemEntity pickup. */
public final class MagnetPickupPolicy {
    public static final int FOREIGN_OWNERSHIP_WINDOW_TICKS = 6_000;

    private MagnetPickupPolicy() {
    }

    public static boolean eligible(ItemEntity item, Player player, double radius) {
        if (item == null || player == null || item.level() != player.level()
                || !item.isAlive() || item.getItem().isEmpty() || item.hasPickUpDelay()
                || !Double.isFinite(radius) || radius < 0.0D
                || !Double.isFinite(item.distanceToSqr(player))
                || item.distanceToSqr(player) > radius * radius) return false;

        UUID thrower = item instanceof ItemEntityOwnershipAccessor accessor
                ? accessor.customEnchants$getThrowerUuid() : null;
        return foreignThrowerAllowed(thrower, player.getUUID(), item.getAge());
    }

    public static boolean foreignThrowerAllowed(UUID thrower, UUID player, int age) {
        if (thrower == null || thrower.equals(player)) return true;
        return age >= FOREIGN_OWNERSHIP_WINDOW_TICKS;
    }
}
