package com.mentality.customenchants.net;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DoubleJumpApprovedPayloadContractTest {
    @Test
    void approvalPayloadCarriesSequenceAndOnlyMovementComponentsNeededByOwner() throws Exception {
        String source = Files.readString(Path.of("src/main/java/com/mentality/customenchants/net/DoubleJumpApprovedPayload.java"));

        assertTrue(source.contains("long sequence"));
        assertTrue(source.contains("double verticalVelocity"));
        assertTrue(source.contains("double horizontalImpulseX"));
        assertTrue(source.contains("double horizontalImpulseZ"));
        assertTrue(source.contains("Identifier.fromNamespaceAndPath"));
        assertTrue(source.contains("\"double_jump_approved\""));
        assertTrue(source.contains("ByteBufCodecs.VAR_LONG"));
        assertTrue(source.contains("ByteBufCodecs.DOUBLE"));
    }
}
