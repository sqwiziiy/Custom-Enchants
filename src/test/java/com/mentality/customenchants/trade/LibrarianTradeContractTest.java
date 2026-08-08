package com.mentality.customenchants.trade;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibrarianTradeContractTest {
    @Test
    void enchantedBookTradeRequiresAPlainBook() throws Exception {
        String trade = Files.readString(Path.of(
                "src/main/java/com/mentality/customenchants/trade/LibrarianEnchantTrade.java"));

        assertTrue(trade.contains("Optional.of(new ItemCost(Items.BOOK, 1))"));
        assertFalse(trade.contains("Optional.empty(), book"));
    }

    @Test
    void customBooksAreNotGatedByLegacyVillagerTier() throws Exception {
        String initializer = Files.readString(Path.of(
                "src/main/java/com/mentality/customenchants/CustomEnchantsMod.java"));

        assertTrue(initializer.contains("for (int villagerLevel = 1; villagerLevel <= 5; villagerLevel++)"));
        assertTrue(initializer.contains("LibrarianEnchantTrade.random(random)"));
        assertFalse(initializer.contains("offer.villagerLevel()"));
    }
}
