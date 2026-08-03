package com.mentality.customenchants.trade;

import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;

/** Canonical data for the three shield-enchantment librarian offers. */
public record ShieldTradeDefinition(Enchantment enchantment, int bookLevel, int villagerLevel,
                                    int emeralds, int maxUses, int villagerXp, float priceMultiplier) {
    public static List<ShieldTradeDefinition> all() {
        return List.of(
                new ShieldTradeDefinition(ModEnchantments.REBOUND, 1, 2, 16, 12, 5, 0.2f),
                new ShieldTradeDefinition(ModEnchantments.REBOUND, 2, 4, 32, 6, 15, 0.2f),
                new ShieldTradeDefinition(ModEnchantments.REBOUND, 3, 5, 50, 3, 30, 0.2f),
                new ShieldTradeDefinition(ModEnchantments.FEEDBACK, 1, 3, 22, 6, 15, 0.2f),
                new ShieldTradeDefinition(ModEnchantments.GUARDIANS_GRACE, 1, 2, 16, 12, 5, 0.2f),
                new ShieldTradeDefinition(ModEnchantments.GUARDIANS_GRACE, 2, 4, 32, 6, 15, 0.2f),
                new ShieldTradeDefinition(ModEnchantments.GUARDIANS_GRACE, 3, 5, 50, 3, 30, 0.2f)
        );
    }

    public MerchantOffer createOffer() {
        ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, bookLevel));
        return new MerchantOffer(new ItemStack(Items.EMERALD, emeralds), book,
                maxUses, villagerXp, priceMultiplier);
    }
}
