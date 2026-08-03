package com.mentality.customenchants.enchantment;

/**
 * Runtime logic for the Tether Master enchantment. Identity/definition is data-driven
 * ({@link ModEnchantments#TETHER_MASTER}); this neutral holder keeps the pull-strength curve
 * used by the fishing-rod pull handler.
 */
public final class TetherMasterEnchantment {

    private TetherMasterEnchantment() {
    }

    /**
     * Returns the pull strength multiplier for the given enchantment level.
     * Level I: 1.15, Level II: 1.25, Level III: 1.40
     */
    public static float getPullMultiplier(int level) {
        return switch (level) {
            case 1 -> 1.15f;
            case 2 -> 1.25f;
            case 3 -> 1.40f;
            default -> 1.0f;
        };
    }
}
