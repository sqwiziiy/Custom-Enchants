package com.mentality.customenchants.gametest;

import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;

public final class CustomEnchantsGameTests implements FabricGameTest {

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 100)
    public void registryAndRealWorldAreAvailable(GameTestHelper helper) {
        helper.assertTrue(
                EnchantmentAccess.resolve(ModEnchantments.AUTO_SMELT, helper.getLevel().registryAccess()).isPresent(),
                "Auto Smelt must be present in the data-driven enchantment registry");
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, Blocks.IRON_ORE);
        helper.assertBlockPresent(Blocks.IRON_ORE, pos);
        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 100)
    public void realEnchantedBookUsesRegisteredEnchantments(GameTestHelper helper) {
        Holder<Enchantment> holder = EnchantmentAccess
                .resolve(ModEnchantments.AUTO_SMELT, helper.getLevel().registryAccess())
                .orElseThrow();
        ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(holder, 1));
        ItemEnchantments stored = book.get(DataComponents.STORED_ENCHANTMENTS);
        helper.assertTrue(stored != null
                        && stored.getLevel(holder) == 1
                        && stored.keySet().stream().anyMatch(h -> h.is(ModEnchantments.AUTO_SMELT)),
                "Real enchanted book must retain the registered Auto Smelt level");
        helper.succeed();
    }
}
