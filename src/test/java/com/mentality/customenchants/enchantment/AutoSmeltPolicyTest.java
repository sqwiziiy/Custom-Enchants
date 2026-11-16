package com.mentality.customenchants.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.server.Bootstrap;
import net.minecraft.SharedConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        Holder<Enchantment> silkTouch = VanillaRegistries.createLookup()
                .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
        tool.enchant(silkTouch, 1);

        assertTrue(AutoSmeltHandler.hasSilkTouch(tool));
    }

    @Test
    void ordinaryToolDoesNotHaveSilkTouch() {
        assertFalse(AutoSmeltHandler.hasSilkTouch(new ItemStack(Items.DIAMOND_PICKAXE)));
    }
}
