package com.mentality.customenchants.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScopedReentrancyGuardTest {

    @Test
    void firstEntryIsAllowedNestedSameKeyIsDeniedAndOtherPlayerIsAllowed() {
        ScopedReentrancyGuard<String> guard = new ScopedReentrancyGuard<>();
        try (var first = guard.tryEnter("player-a")) {
            assertNotNull(first);
            assertTrue(guard.isActive("player-a"));
            assertNull(guard.tryEnter("player-a"));
            try (var other = guard.tryEnter("player-b")) {
                assertNotNull(other);
            }
        }
        assertTrue(!guard.isActive("player-a"));
    }

    @Test
    void scopeCleansUpAfterException() {
        ScopedReentrancyGuard<String> guard = new ScopedReentrancyGuard<>();
        try {
            try (var ignored = guard.tryEnter("player-a")) {
                throw new IllegalStateException("test");
            }
        } catch (IllegalStateException expected) {
            // expected
        }
        try (var scope = guard.tryEnter("player-a")) {
            assertNotNull(scope);
        }
    }

    @Test
    void drillAndLumberjackPolicyCanShareOneGuard() {
        ScopedReentrancyGuard<String> shared = new ScopedReentrancyGuard<>();
        try (var drill = shared.tryEnter("player-a")) {
            assertNotNull(drill);
            assertNull(shared.tryEnter("player-a"));
        }
        try (var lumberjack = shared.tryEnter("player-a")) {
            assertNotNull(lumberjack);
        }
    }
}
