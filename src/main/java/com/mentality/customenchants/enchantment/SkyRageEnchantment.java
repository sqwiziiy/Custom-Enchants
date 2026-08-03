package com.mentality.customenchants.enchantment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runtime state and logic for the Sky Rage enchantment. Identity/definition is data-driven
 * ({@link ModEnchantments#SKY_RAGE}); this neutral holder keeps the per-player lightning cooldown
 * state and trigger chances used by the projectile-impact handler and the server lifecycle cleanup.
 */
public final class SkyRageEnchantment {

    private SkyRageEnchantment() {
    }

    /** Cooldown in game ticks (1.5 seconds = 30 ticks) between lightning strikes per player. */
    public static final long COOLDOWN_TICKS = 30L;

    /** Stores the last game time a lightning was triggered, keyed by player UUID. */
    public static final Map<UUID, Long> lastLightningTime = new ConcurrentHashMap<>();

    /**
     * Returns the trigger chance for the given enchantment level (0.0–1.0).
     * Level 1 = 10%, level 2 = 20%, level 3 = 30%.
     */
    public static float getChance(int level) {
        return switch (level) {
            case 1 -> 0.10f;
            case 2 -> 0.20f;
            default -> 0.30f;
        };
    }

    public static void clear(UUID playerId) {
        lastLightningTime.remove(playerId);
    }

    public static void clearAll() {
        lastLightningTime.clear();
    }
}
