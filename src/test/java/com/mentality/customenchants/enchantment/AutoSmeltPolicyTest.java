package com.mentality.customenchants.enchantment;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.server.Bootstrap;
import net.minecraft.SharedConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoSmeltPolicyTest {

    @BeforeAll
    static void bootstrapMinecraftRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void silkTouchDominatesWhenCommandsCreateAnInvalidCombination() {
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        EnchantmentHelper.setEnchantments(Map.of(Enchantments.SILK_TOUCH, 1), tool);

        assertTrue(AutoSmeltHandler.hasSilkTouch(tool));
    }

    @Test
    void ordinaryToolDoesNotHaveSilkTouch() {
        assertFalse(AutoSmeltHandler.hasSilkTouch(new ItemStack(Items.DIAMOND_PICKAXE)));
    }
}
