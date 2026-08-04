package com.mentality.customenchants.trade;

import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;
import java.util.Optional;

/**
 * Canonical, single-source librarian enchanted-book offers.
 *
 * <p>Ported to the Minecraft 1.21.1 {@link MerchantOffer}/{@link ItemCost} API. Every offer keeps
 * its exact 1.20.1 tier, emerald cost, book level, max uses, villager XP and price multiplier.
 * The enchantment holder is resolved from the server registry at offer-build time and the offer
 * is fail-closed: a missing registry entry yields {@code null} (no empty/broken book offer).
 */
public record LibrarianEnchantTrade(ResourceKey<Enchantment> enchantment, int bookLevel, int villagerLevel,
                                    int emeralds, int maxUses, int villagerXp, float priceMultiplier) {

    /** Builds the offer, or {@code null} if the enchantment registry entry is missing (fail-closed). */
    public MerchantOffer createOffer(RegistryAccess access) {
        Optional<Holder.Reference<Enchantment>> holder = EnchantmentAccess.resolve(enchantment, access);
        if (holder.isEmpty()) {
            return null;
        }
        ItemStack book = EnchantmentHelper.createBook(new EnchantmentInstance(holder.get(), bookLevel));
        return new MerchantOffer(new ItemCost(Items.EMERALD, emeralds), Optional.empty(), book,
                maxUses, villagerXp, priceMultiplier);
    }

    private static LibrarianEnchantTrade t(ResourceKey<Enchantment> e, int bookLevel, int villagerLevel,
                                           int emeralds, int maxUses, int villagerXp) {
        return new LibrarianEnchantTrade(e, bookLevel, villagerLevel, emeralds, maxUses, villagerXp, 0.2f);
    }

    /** The complete librarian offer table (44 offers), preserving 1.20.1 economy exactly. */
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
                // Shield trio (mutually exclusive)
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

    /** The 7 shield-enchantment offers (Rebound, Feedback, Guardian's Grace). */
    public static List<LibrarianEnchantTrade> shieldOffers() {
        return all().stream()
                .filter(o -> o.enchantment().equals(ModEnchantments.REBOUND)
                        || o.enchantment().equals(ModEnchantments.FEEDBACK)
                        || o.enchantment().equals(ModEnchantments.GUARDIANS_GRACE))
                .toList();
    }
}
