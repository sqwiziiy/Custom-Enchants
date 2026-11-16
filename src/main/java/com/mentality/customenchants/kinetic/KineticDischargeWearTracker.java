package com.mentality.customenchants.kinetic;

/** Prevents Kinetic Discharge from repairing damage that predates the flight cycle. */
public final class KineticDischargeWearTracker {
    private KineticDischargeWearTracker() {
    }

    public static int refundOneNewWear(int currentDamage, int activationBaseline) {
        if (activationBaseline < 0 || currentDamage <= activationBaseline) return currentDamage;
        return Math.max(activationBaseline, currentDamage - 1);
    }
}
