package com.mentality.customenchants.enchantment;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoubleJumpMomentumContractTest {
    @Test
    void serverAndApprovedClientPathPreserveHorizontalComponents() throws Exception {
        String server = Files.readString(Path.of("src/main/java/com/mentality/customenchants/enchantment/DoubleJumpServerHandler.java"));
        String client = Files.readString(Path.of("src/client/java/com/mentality/customenchants/enchantment/DoubleJumpHandler.java"));
        assertTrue(server.contains("velocity.x + sprintImpulse.x, Math.max(velocity.y, DOUBLE_JUMP_Y_VELOCITY)"));
        assertTrue(server.contains("-Mth.sin(yawRadians) * VANILLA_SPRINT_JUMP_IMPULSE"));
        assertTrue(server.contains("Mth.cos(yawRadians) * VANILLA_SPRINT_JUMP_IMPULSE"));
        assertTrue(client.contains("current.x + payload.horizontalImpulseX()"));
        assertTrue(client.contains("payload.sequence() <= lastApprovalSequence"));
        assertFalse(server.contains("ClientboundSetEntityMotionPacket"));
    }
}
