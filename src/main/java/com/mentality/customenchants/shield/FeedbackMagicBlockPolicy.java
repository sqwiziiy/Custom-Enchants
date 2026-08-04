package com.mentality.customenchants.shield;

/**
 * Pure allowlist and validation policy for Feedback's independent magic block.
 * This policy applies to damage sources only; it intentionally does not claim
 * universal immunity to potion effect application (including splash/lingering
 * Poison or already-applied Instant Damage effects).
 */
public final class FeedbackMagicBlockPolicy {
    private FeedbackMagicBlockPolicy() {
    }

    public static boolean allowedSource(boolean directMagic, boolean indirectMagic,
                                        boolean shulkerBullet, boolean bypassesShield) {
        return !bypassesShield && (directMagic || indirectMagic || shulkerBullet);
    }

    public static boolean shouldBlock(boolean enabled, boolean activeFeedbackShield,
                                      boolean facingShield, boolean allowedSource) {
        return enabled && activeFeedbackShield && facingShield && allowedSource;
    }
}
