package com.mentality.customenchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class TetherMasterEnchantment extends Enchantment {

    public TetherMasterEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.FISHING_ROD, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof FishingRodItem;
    }

    @Override
    public int getMinCost(int level) {
        return 1 + (level - 1) * 8;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }

    /**
     * Returns the pull strength multiplier for the given enchantment level.
     * Level I: 1.15, Level II: 1.25, Level III: 1.40
     */
    public static float getPullMultiplier(int level) {
        return switch (level) {
            case 1 -> 1.15f;
            case 2 -> 1.25f;
            case 3 -> 1.40f;
            default -> 1.0f;
        };
    }
}
