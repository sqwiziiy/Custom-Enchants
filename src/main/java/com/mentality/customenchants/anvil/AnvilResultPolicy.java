package com.mentality.customenchants.anvil;

public final class AnvilResultPolicy {
    private AnvilResultPolicy() {
    }

    public static boolean rejectShadowBladeResult(boolean hasShadowBlade, boolean isTrident) {
        return hasShadowBlade && !isTrident;
    }
}
