package com.mentality.customenchants.trade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShieldTradeDefinitionTest {
    @Test
    void allShieldBookTradesHaveSafePositiveData() {
        assertEquals(7, ShieldTradeDefinition.all().size());
        ShieldTradeDefinition.all().forEach(definition -> {
            assertTrue(definition.bookLevel() >= 1 && definition.bookLevel() <= 3);
            assertTrue(definition.villagerLevel() >= 1 && definition.villagerLevel() <= 5);
            assertTrue(definition.emeralds() > 0);
            assertTrue(definition.maxUses() > 0);
            assertTrue(definition.villagerXp() > 0);
            assertTrue(definition.priceMultiplier() >= 0.0f);
        });
    }
}
