package com.mentality.customenchants.shield;

import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ShieldEnchantmentsPolicy {
    private ShieldEnchantmentsPolicy() {}

    public static boolean validShield(ItemStack shield) {
        if (shield == null || shield.isEmpty() || !(shield.getItem() instanceof ShieldItem)) return false;
        int count = level(ModEnchantments.REBOUND, shield) + level(ModEnchantments.FEEDBACK, shield)
                + level(ModEnchantments.GUARDIANS_GRACE, shield);
        return count == level(ModEnchantments.REBOUND, shield)
                || count == level(ModEnchantments.FEEDBACK, shield)
                || count == level(ModEnchantments.GUARDIANS_GRACE, shield);
    }

    public static int reboundLevel(ItemStack shield) { return validShield(shield) ? level(ModEnchantments.REBOUND, shield) : 0; }
    public static int feedbackLevel(ItemStack shield) { return validShield(shield) ? level(ModEnchantments.FEEDBACK, shield) : 0; }
    public static int guardiansGraceLevel(ItemStack shield) { return validShield(shield) ? level(ModEnchantments.GUARDIANS_GRACE, shield) : 0; }

    public static boolean feedbackDamage(DamageSource source) {
        if (source == null) return false;
        return FeedbackMagicBlockPolicy.allowedSource(
                source.is(DamageTypes.MAGIC),
                source.is(DamageTypes.INDIRECT_MAGIC),
                source.getDirectEntity() instanceof ShulkerBullet);
    }

    public static boolean customFeedbackBlock(Player player, DamageSource source) {
        return player != null && source != null && feedbackDamage(source) && player.isDamageSourceBlocked(source);
    }

    private static int level(ResourceKey<Enchantment> key, ItemStack shield) {
        return Math.max(0, Math.min(3, EnchantmentAccess.getLevel(shield, key)));
    }

}
