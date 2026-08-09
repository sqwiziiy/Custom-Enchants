package com.mentality.customenchants.enchantment;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DoubleJumpMovementRegressionTest {
    @Test
    void acceptedPathAppliesRealAirborneVerticalMotionBeforeVisuals() throws Exception {
        String server = Files.readString(Path.of("src/main/java/com/mentality/customenchants/enchantment/DoubleJumpServerHandler.java"));

        int velocityCall = server.indexOf("applyAirborneJumpVelocity(player, sprinting, payload.yawDegrees())");
        int approval = server.indexOf("ServerPlayNetworking.send(player, new DoubleJumpApprovedPayload(");
        int particles = server.indexOf("sendParticles(ParticleTypes.CLOUD");

        assertTrue(velocityCall >= 0, "accepted Double Jump must apply an airborne velocity impulse");
        assertTrue(approval > velocityCall, "server approval must follow authoritative motion");
        assertTrue(particles > approval, "particles must not be the only accepted-jump effect");
        assertTrue(server.contains("Math.max(velocity.y, DOUBLE_JUMP_Y_VELOCITY)"));
        assertTrue(server.contains("sprintRequested || player.isSprinting()"),
                "sprint state must survive airborne server/client timing differences");
    }
}
