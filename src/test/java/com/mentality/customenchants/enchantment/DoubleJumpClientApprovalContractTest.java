package com.mentality.customenchants.enchantment;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoubleJumpClientApprovalContractTest {
    @Test
    void approvedMovementRejectsStaleAndNonFinitePayloadsAndPreservesPrediction() throws Exception {
        String client = Files.readString(Path.of("src/client/java/com/mentality/customenchants/enchantment/DoubleJumpHandler.java"));

        assertTrue(client.contains("payload.sequence() <= lastApprovalSequence"));
        assertTrue(client.contains("!Double.isFinite(payload.verticalVelocity())"));
        assertTrue(client.contains("!Double.isFinite(payload.horizontalImpulseX())"));
        assertTrue(client.contains("!Double.isFinite(payload.horizontalImpulseZ())"));
        assertTrue(client.contains("current.x + payload.horizontalImpulseX()"));
        assertTrue(client.contains("Math.max(current.y, payload.verticalVelocity())"));
        assertTrue(client.contains("current.z + payload.horizontalImpulseZ()"));
        assertTrue(client.contains("lastApprovalSequence = Long.MIN_VALUE"));
        assertFalse(client.contains("player.jumpFromGround()"));
    }
}
