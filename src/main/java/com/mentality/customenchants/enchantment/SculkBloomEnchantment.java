package com.mentality.customenchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SculkBloomEnchantment extends Enchantment {

    public SculkBloomEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 20;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 30;
    }
}
