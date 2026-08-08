package com.mentality.customenchants.enchantment;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Guards the server-authoritative movement path when a live ServerPlayer is unavailable to unit tests. */
class DoubleJumpServerHandlerContractTest {
    @Test
    void airborneJumpUsesExplicitVelocityAndSynchronizesItBeforeParticles() throws IOException {
        String source = Files.readString(Path.of("src/main/java/com/mentality/customenchants/enchantment/DoubleJumpServerHandler.java"));

        assertFalse(source.contains("player.jumpFromGround()"),
                "jumpFromGround is grounded-only and cannot implement an airborne second jump");
        assertTrue(source.contains("player.setDeltaMovement("));
        assertTrue(source.contains("player.hurtMarked = true"));
        assertTrue(source.contains("player.resetFallDistance()"));
        assertFalse(source.contains("ClientboundSetEntityMotionPacket"),
                "a stale full-vector owner packet can overwrite sprint X/Z prediction");
        assertTrue(source.contains("new DoubleJumpApprovedPayload("));
        assertTrue(source.indexOf("new DoubleJumpApprovedPayload(")
                        < source.indexOf("sendParticles(ParticleTypes.CLOUD"),
                "particles are emitted only after authoritative velocity is synchronized");
    }

    @Test
    void stateIsNotConsumedUntilTheBootEnchantmentIsConfirmed() throws IOException {
        String source = Files.readString(Path.of("src/main/java/com/mentality/customenchants/enchantment/DoubleJumpServerHandler.java"));
        assertTrue(source.indexOf("ItemStack boots") < source.indexOf("DoubleJumpServerValidator.accept("));
    }
}
