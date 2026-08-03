package com.mentality.customenchants.state;

/** Pure server-side replay/airborne policy for the Double Jump packet. */
public final class DoubleJumpServerValidator {
    public static final long MIN_PACKET_INTERVAL_TICKS = 10L;

    private DoubleJumpServerValidator() {
    }

    public record State(boolean consumedThisAirTime, long lastAcceptedTick) {
        public static State initial() {
            return new State(false, Long.MIN_VALUE);
        }
    }

    public record Decision(boolean accepted, State state) {
    }

    public static Decision accept(State previous, long currentTick, boolean eligible) {
        boolean tooSoon = previous.lastAcceptedTick() != Long.MIN_VALUE
                && currentTick - previous.lastAcceptedTick() < MIN_PACKET_INTERVAL_TICKS;
        if (!eligible || previous.consumedThisAirTime() || tooSoon) {
            return new Decision(false, previous);
        }
        return new Decision(true, new State(true, currentTick));
    }

    public static State resetAfterLanding(State state) {
        return new State(false, state.lastAcceptedTick());
    }
}
