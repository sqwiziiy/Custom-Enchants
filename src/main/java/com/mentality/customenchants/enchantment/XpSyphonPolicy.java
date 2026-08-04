package com.mentality.customenchants.enchantment;

/** Deterministic XP Syphon chance/amount policy used by runtime code and regression tests. */
public final class XpSyphonPolicy {
    private XpSyphonPolicy() { }
    public static float chance(int level) {
        return switch (Math.max(1, Math.min(3, level))) { case 1 -> 0.05f; case 2 -> 0.10f; default -> 0.15f; };
    }
    public static int orbValue(int level) { return Math.max(1, Math.min(3, level)); }
    public static boolean triggers(int level, float roll) { return Float.isFinite(roll) && roll >= 0.0f && roll < chance(level); }
}
