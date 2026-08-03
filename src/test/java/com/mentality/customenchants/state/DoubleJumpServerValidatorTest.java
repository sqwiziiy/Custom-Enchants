package com.mentality.customenchants.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoubleJumpServerValidatorTest {
    @Test
    void acceptsOneAirbornePacketAndRejectsReplay() {
        DoubleJumpServerValidator.State initial = DoubleJumpServerValidator.State.initial();
        DoubleJumpServerValidator.Decision accepted = DoubleJumpServerValidator.accept(initial, 100, true);
        DoubleJumpServerValidator.Decision replay = DoubleJumpServerValidator.accept(accepted.state(), 101, true);

        assertTrue(accepted.accepted());
        assertFalse(replay.accepted());
    }

    @Test
    void rejectsIneligiblePacketAndAllowsNewAirTimeAfterLanding() {
        DoubleJumpServerValidator.State initial = DoubleJumpServerValidator.State.initial();
        assertFalse(DoubleJumpServerValidator.accept(initial, 100, false).accepted());

        DoubleJumpServerValidator.State accepted = DoubleJumpServerValidator.accept(initial, 100, true).state();
        DoubleJumpServerValidator.State landed = DoubleJumpServerValidator.resetAfterLanding(accepted);
        assertFalse(DoubleJumpServerValidator.accept(landed, 105, true).accepted());
        assertTrue(DoubleJumpServerValidator.accept(landed, 110, true).accepted());
    }
}
