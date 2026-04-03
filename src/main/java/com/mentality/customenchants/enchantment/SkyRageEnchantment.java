package com.mentality.customenchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkyRageEnchantment extends Enchantment {

    /** Cooldown in game ticks (1.5 seconds = 30 ticks) between lightning strikes per player. */
    public static final long COOLDOWN_TICKS = 30L;

    /** Stores the last game time a lightning was triggered, keyed by player UUID. */
    public static final Map<UUID, Long> lastLightningTime = new ConcurrentHashMap<>();

    public SkyRageEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem;
    }

    @Override
    public int getMinCost(int level) {
        return 10 + (level - 1) * 15;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return super.checkCompatibility(other)
                && other != Enchantments.FLAMING_ARROWS   // Flame
                && other != Enchantments.INFINITY_ARROWS  // Infinity
                && other != Enchantments.PIERCING;        // Piercing
    }

    /**
     * Returns the trigger chance for the given enchantment level (0.0–1.0).
     * Level 1 = 10%, level 2 = 20%, level 3 = 30%.
     */
    public static float getChance(int level) {
        return switch (level) {
            case 1 -> 0.10f;
            case 2 -> 0.20f;
            default -> 0.30f;
        };
    }
}
