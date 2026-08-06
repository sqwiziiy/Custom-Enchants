package com.mentality.customenchants.magnet;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class MagnetBreakDropContextTest {

    @Test
    void nestedBreaksRestoreTheExactOuterContext() {
        try (MagnetBreakDropContext.Scope outer = MagnetBreakDropContext.open(null, null, new BlockPos(1, 64, 1), null)) {
            MagnetBreakDropContext.Context outerContext = MagnetBreakDropContext.current(null);
            try (MagnetBreakDropContext.Scope inner = MagnetBreakDropContext.open(null, null, new BlockPos(1, 65, 1), null)) {
                MagnetBreakDropContext.Context innerContext = MagnetBreakDropContext.current(null);
                assertEquals(new BlockPos(1, 65, 1), innerContext.pos());
            }
            assertSame(outerContext, MagnetBreakDropContext.current(null));
        }
        assertNull(MagnetBreakDropContext.current(null));
    }
}
