package com.mentality.customenchants.enchantment;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoubleJumpSprintImpulseTest {
    private static final double EPSILON = 1.0E-6D;

    @Test
    void matchesVanillaSprintJumpDirections() {
        assertVector(DoubleJumpServerHandler.sprintJumpImpulse(0.0F, true), 0.0D, 0.2D);
        assertVector(DoubleJumpServerHandler.sprintJumpImpulse(90.0F, true), -0.2D, 0.0D);
        assertVector(DoubleJumpServerHandler.sprintJumpImpulse(180.0F, true), 0.0D, -0.2D);
        assertVector(DoubleJumpServerHandler.sprintJumpImpulse(-90.0F, true), 0.2D, 0.0D);
    }

    @Test
    void nonSprintingAddsNoHorizontalImpulse() {
        assertVector(DoubleJumpServerHandler.sprintJumpImpulse(37.0F, false), 0.0D, 0.0D);
    }

    private static void assertVector(Vec3 actual, double x, double z) {
        assertEquals(x, actual.x, EPSILON);
        assertEquals(z, actual.z, EPSILON);
        assertEquals(0.0D, actual.y, EPSILON);
    }
}
