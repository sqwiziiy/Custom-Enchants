package com.mentality.customenchants.trade;

import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;

/** Canonical librarian custom enchanted-book offers for Minecraft 1.20.1. */
public record LibrarianEnchantTrade(Enchantment enchantment, int bookLevel, int villagerLevel,
                                    int emeralds, int maxUses, int villagerXp, float priceMultiplier) {

    /** Builds an enchanted-book trade that always requires emeralds plus one plain book. */
    public MerchantOffer createOffer() {
        ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(
                new EnchantmentInstance(enchantment, bookLevel));
        return new MerchantOffer(
                new ItemStack(Items.EMERALD, emeralds),
                new ItemStack(Items.BOOK, 1),
                enchantedBook,
                maxUses,
                villagerXp,
                priceMultiplier);
    }

    /** Picks one custom enchanted-book definition for the current librarian trade roll. */
    public static LibrarianEnchantTrade random(RandomSource random) {
        List<LibrarianEnchantTrade> offers = all();
        return offers.get(random.nextInt(offers.size()));
    }

    private static LibrarianEnchantTrade t(Enchantment enchantment, int bookLevel, int villagerLevel,
                                           int emeralds, int maxUses, int villagerXp) {
        return new LibrarianEnchantTrade(enchantment, bookLevel, villagerLevel,
                emeralds, maxUses, villagerXp, 0.2f);
    }

    /** The complete 44-offer table, preserving the existing 3.2.x costs and book levels. */
    public static List<LibrarianEnchantTrade> all() {
        return List.of(
                t(ModEnchantments.GLOW_STRIKE, 1, 1, 10, 12, 5),
                t(ModEnchantments.GLOW_STRIKE, 2, 3, 28, 6, 15),
                t(ModEnchantments.GLOW_STRIKE, 3, 5, 48, 3, 30),
                t(ModEnchantments.DOUBLE_JUMP, 1, 4, 38, 3, 25),
                t(ModEnchantments.DRILL, 1, 5, 50, 2, 30),
                t(ModEnchantments.POISON_BLADE, 1, 1, 12, 12, 5),
                t(ModEnchantments.POISON_BLADE, 2, 3, 30, 6, 15),
                t(ModEnchantments.POISON_BLADE, 3, 5, 50, 3, 30),
                t(ModEnchantments.LUMBERJACK, 1, 2, 14, 12, 5),
                t(ModEnchantments.LUMBERJACK, 2, 4, 32, 6, 15),
                t(ModEnchantments.LUMBERJACK, 3, 5, 52, 3, 30),
                t(ModEnchantments.SHADOW_BLADE, 1, 2, 16, 12, 5),
                t(ModEnchantments.SHADOW_BLADE, 2, 4, 34, 6, 15),
                t(ModEnchantments.SHADOW_BLADE, 3, 5, 54, 3, 30),
                t(ModEnchantments.MAGNET, 1, 3, 24, 6, 15),
                t(ModEnchantments.AUTO_SMELT, 1, 4, 32, 6, 15),
                t(ModEnchantments.VEGETATION, 1, 2, 14, 12, 5),
                t(ModEnchantments.VEGETATION, 2, 4, 30, 6, 15),
                t(ModEnchantments.VEGETATION, 3, 5, 48, 3, 30),
                t(ModEnchantments.REBOUND, 1, 2, 16, 12, 5),
                t(ModEnchantments.REBOUND, 2, 4, 32, 6, 15),
                t(ModEnchantments.REBOUND, 3, 5, 50, 3, 30),
                t(ModEnchantments.FEEDBACK, 1, 3, 22, 6, 15),
                t(ModEnchantments.GUARDIANS_GRACE, 1, 2, 16, 12, 5),
                t(ModEnchantments.GUARDIANS_GRACE, 2, 4, 32, 6, 15),
                t(ModEnchantments.GUARDIANS_GRACE, 3, 5, 50, 3, 30),
                t(ModEnchantments.SECOND_WIND, 1, 5, 52, 2, 30),
                t(ModEnchantments.VULNERABILITY, 1, 2, 18, 12, 5),
                t(ModEnchantments.VULNERABILITY, 2, 4, 36, 6, 15),
                t(ModEnchantments.VULNERABILITY, 3, 5, 52, 3, 30),
                t(ModEnchantments.TETHER_MASTER, 1, 1, 10, 12, 5),
                t(ModEnchantments.TETHER_MASTER, 2, 3, 22, 6, 15),
                t(ModEnchantments.TETHER_MASTER, 3, 4, 36, 6, 15),
                t(ModEnchantments.SKY_RAGE, 1, 3, 22, 8, 15),
                t(ModEnchantments.SKY_RAGE, 2, 4, 38, 5, 20),
                t(ModEnchantments.SKY_RAGE, 3, 5, 55, 3, 30),
                t(ModEnchantments.XP_SYPHON, 1, 2, 14, 12, 5),
                t(ModEnchantments.XP_SYPHON, 2, 3, 26, 6, 15),
                t(ModEnchantments.XP_SYPHON, 3, 4, 40, 6, 20),
                t(ModEnchantments.KINETIC_DISCHARGE, 1, 4, 28, 5, 20),
                t(ModEnchantments.KINETIC_DISCHARGE, 2, 5, 44, 3, 30),
                t(ModEnchantments.KINETIC_DISCHARGE, 3, 5, 62, 2, 30),
                t(ModEnchantments.SCULK_BLOOM, 1, 4, 30, 5, 20),
                t(ModEnchantments.SCULK_BLOOM, 2, 5, 50, 3, 30));
    }
}
