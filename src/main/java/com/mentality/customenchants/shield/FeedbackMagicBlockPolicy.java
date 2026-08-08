package com.mentality.customenchants.shield;

/**
 * Damage-source policy for Feedback's independent magic guard.
 *
 * Feedback is intentionally stronger than vanilla shield damage resolution for its magic
 * allowlist: a raised Feedback shield can cancel magic that vanilla marks as shield-bypassing
 * or otherwise would not resolve as normal blocked shield damage (notably Harming potions).
 */
public final class FeedbackMagicBlockPolicy {
    private FeedbackMagicBlockPolicy() {
    }

    public static boolean allowedSource(boolean directMagic, boolean indirectMagic,
                                        boolean shulkerBullet) {
        return directMagic || indirectMagic || shulkerBullet;
    }

    public static boolean shouldBlock(boolean enabled, boolean activeFeedbackShield,
                                      boolean allowedSource) {
        return enabled && activeFeedbackShield && allowedSource;
    }
}
