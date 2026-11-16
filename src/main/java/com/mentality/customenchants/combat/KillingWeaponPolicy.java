package com.mentality.customenchants.combat;

public final class KillingWeaponPolicy {
    private KillingWeaponPolicy() {
    }

    public static boolean directPlayerHit(boolean sourceEntityIsPlayer, boolean directEntityIsSourceEntity) {
        return sourceEntityIsPlayer && directEntityIsSourceEntity;
    }
}
