package com.mentality.customenchants.enchantment;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoubleJumpMomentumContractTest {
    @Test
    void predictsOnInputAndUsesApprovalOnlyAsAcknowledgement() throws Exception {
        String server = Files.readString(Path.of("src/main/java/com/mentality/customenchants/enchantment/DoubleJumpServerHandler.java"));
        String client = Files.readString(Path.of("src/client/java/com/mentality/customenchants/enchantment/DoubleJumpHandler.java"));
        String payload = Files.readString(Path.of("src/main/java/com/mentality/customenchants/net/DoubleJumpPayload.java"));

        assertTrue(server.contains("velocity.x + sprintImpulse.x"));
        assertTrue(server.contains("Math.max(velocity.y, DOUBLE_JUMP_Y_VELOCITY)"));
        assertTrue(server.contains("-Mth.sin(yawRadians) * VANILLA_SPRINT_JUMP_IMPULSE"));
        assertTrue(server.contains("Mth.cos(yawRadians) * VANILLA_SPRINT_JUMP_IMPULSE"));
        assertTrue(server.contains("payload.sprinting()"));
        assertTrue(server.contains("sprintRequested || player.isSprinting()"));
        assertTrue(client.contains("applyLocalPrediction(player, sprinting, yawDegrees)"));
        assertTrue(client.contains("new DoubleJumpPayload(sprinting, yawDegrees)"));
        assertTrue(server.contains("payload.yawDegrees()"));
        assertTrue(server.contains("applyAirborneJumpVelocity(player, sprinting, payload.yawDegrees())"));
        assertTrue(client.contains("Float.isFinite(yawDegrees)"));
        assertFalse(client.contains("current.x + payload.horizontalImpulseX()"));
        assertTrue(client.contains("payload.sequence() <= lastApprovalSequence"));
        assertTrue(payload.contains("ByteBufCodecs.BOOL"));
        assertTrue(payload.contains("ByteBufCodecs.FLOAT"));
        assertFalse(server.contains("ClientboundSetEntityMotionPacket"));
    }
}
