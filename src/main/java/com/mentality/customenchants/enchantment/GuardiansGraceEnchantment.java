package com.mentality.customenchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class GuardiansGraceEnchantment extends Enchantment {

    public GuardiansGraceEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.BREAKABLE,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ShieldItem;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof ReboundEnchantment) &&
               !(other instanceof FeedbackEnchantment) &&
               super.checkCompatibility(other);
    }
}
