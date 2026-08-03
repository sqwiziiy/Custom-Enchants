package com.mentality.customenchants.enchantment;

import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Validates the registry-free {@link EnchantmentAccess} lookup: present/missing entries, level
 * bounds, and null/empty safety, using vanilla enchantment holders as the fixture.
 */
class EnchantmentAccessTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static Holder<Enchantment> holder(net.minecraft.resources.ResourceKey<Enchantment> key) {
        return VanillaRegistries.createLookup().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }

    @Test
    void presentEnchantmentReportsItsLevel() {
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        tool.enchant(holder(Enchantments.SILK_TOUCH), 1);
        assertEquals(1, EnchantmentAccess.getLevel(tool, Enchantments.SILK_TOUCH));
        assertTrue(EnchantmentAccess.has(tool, Enchantments.SILK_TOUCH));
    }

    @Test
    void missingEnchantmentFailsClosedToZero() {
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        tool.enchant(holder(Enchantments.SILK_TOUCH), 1);
        assertEquals(0, EnchantmentAccess.getLevel(tool, Enchantments.FORTUNE));
        assertFalse(EnchantmentAccess.has(tool, Enchantments.FORTUNE));
    }

    @Test
    void multiLevelEnchantmentIsReadExactly() {
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        tool.enchant(holder(Enchantments.FORTUNE), 3);
        assertEquals(3, EnchantmentAccess.getLevel(tool, Enchantments.FORTUNE));
    }

    @Test
    void emptyAndUnenchantedStacksAreSafe() {
        assertEquals(0, EnchantmentAccess.getLevel(ItemStack.EMPTY, Enchantments.SILK_TOUCH));
        assertEquals(0, EnchantmentAccess.getLevel(new ItemStack(Items.DIAMOND_PICKAXE), Enchantments.SILK_TOUCH));
        assertFalse(EnchantmentAccess.has(new ItemStack(Items.DIAMOND_PICKAXE), Enchantments.SILK_TOUCH));
    }
}
