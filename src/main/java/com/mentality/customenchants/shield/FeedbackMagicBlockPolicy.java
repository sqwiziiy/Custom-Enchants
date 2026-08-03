package com.mentality.customenchants.shield;

/** Pure allowlist and validation policy for Feedback's independent magic block. */
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
