package com.mentality.customenchants.shadowblade;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShadowBladeTeleportPlannerTest {
    @Test
    void candidatesAreDeterministicAndBounded() {
        List<ShadowBladeTeleportPlanner.Candidate> first = ShadowBladeTeleportPlanner.candidates(10, 64, -4, 0, 0.6, 0.6);
        assertEquals(first, ShadowBladeTeleportPlanner.candidates(10, 64, -4, 0, 0.6, 0.6));
        assertEquals(7, first.size());
        assertTrue(first.stream().allMatch(ShadowBladeTeleportPlanner.Candidate::isFinite));
    }

    @Test
    void primaryCandidateRemainsBehindTargetForCardinalYaw() {
        ShadowBladeTeleportPlanner.Candidate south = ShadowBladeTeleportPlanner.candidates(0, 64, 0, 0, 0.6, 0.6).get(0);
        ShadowBladeTeleportPlanner.Candidate west = ShadowBladeTeleportPlanner.candidates(0, 64, 0, 90, 0.6, 0.6).get(0);
        assertEquals(0, south.x(), 1.0e-9);
        assertEquals(-1.5, south.z(), 1.0e-9);
        assertEquals(1.5, west.x(), 1.0e-9);
        assertEquals(0, west.z(), 1.0e-9);
    }

    @Test
    void oversizedEntitiesIncreasePrimarySeparation() {
        assertEquals(-4.05, ShadowBladeTeleportPlanner.candidates(0, 64, 0, 0, 4, 4).get(0).z(), 1.0e-9);
    }

    @Test
    void nonFiniteInputFailsClosed() {
        assertTrue(ShadowBladeTeleportPlanner.candidates(Double.NaN, 64, 0, 0, 1, 1).isEmpty());
        assertTrue(ShadowBladeTeleportPlanner.candidates(0, 64, 0, Float.NaN, 1, 1).isEmpty());
    }
}
