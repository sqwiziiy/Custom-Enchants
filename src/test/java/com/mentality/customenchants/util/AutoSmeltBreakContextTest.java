package com.mentality.customenchants.util;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoSmeltBreakContextTest {

    @Test
    void matchingBreakIsConsumedOnlyOnceAndWrongPositionIsIgnored() {
        BlockPos pos = new BlockPos(3, 64, 7);
        try (AutoSmeltBreakContext.Scope ignored = AutoSmeltBreakContext.open(null, null, pos, null, null, null)) {
            assertTrue(AutoSmeltBreakContext.consume(null, pos, null, null, null, null));
            assertFalse(AutoSmeltBreakContext.consume(null, pos, null, null, null, null));
            assertFalse(AutoSmeltBreakContext.consume(null, pos.above(), null, null, null, null));
        }
        assertFalse(AutoSmeltBreakContext.consume(null, pos, null, null, null, null));
    }

    @Test
    void nestedScopesAreIndependentAndExceptionSafe() {
        BlockPos pos = new BlockPos(0, 64, 0);
        try (AutoSmeltBreakContext.Scope outer = AutoSmeltBreakContext.open(null, null, pos, null, null, null)) {
            try (AutoSmeltBreakContext.Scope inner = AutoSmeltBreakContext.open(null, null, pos, null, null, null)) {
                assertTrue(AutoSmeltBreakContext.consume(null, pos, null, null, null, null));
            }
            assertTrue(AutoSmeltBreakContext.consume(null, pos, null, null, null, null));
        }

        try {
            try (AutoSmeltBreakContext.Scope ignored = AutoSmeltBreakContext.open(null, null, pos, null, null, null)) {
                throw new IllegalStateException("expected test exception");
            }
        } catch (IllegalStateException expected) {
            // Scope must be cleared by try-with-resources.
        }
        assertFalse(AutoSmeltBreakContext.consume(null, pos, null, null, null, null));
    }
}
