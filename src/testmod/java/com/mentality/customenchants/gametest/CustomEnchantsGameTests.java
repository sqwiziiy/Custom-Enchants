package com.mentality.customenchants.gametest;

import com.mentality.customenchants.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;

public final class CustomEnchantsGameTests implements FabricGameTest {

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 100)
    public void registryAndRealWorldAreAvailable(GameTestHelper helper) {
        helper.assertTrue(BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation("custom-enchants", "auto_smelt")) == ModEnchantments.AUTO_SMELT,
                "Auto Smelt must be registered in the real Minecraft registry");
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, Blocks.IRON_ORE);
        helper.assertBlockPresent(Blocks.IRON_ORE, pos);
        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 100)
    public void realEnchantedBookUsesRegisteredEnchantments(GameTestHelper helper) {
        ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ModEnchantments.AUTO_SMELT, 1));
        CompoundTag stored = EnchantedBookItem.getEnchantments(book).getCompound(0);
        helper.assertTrue(EnchantedBookItem.getEnchantments(book).size() == 1
                        && "custom-enchants:auto_smelt".equals(stored.getString("id"))
                        && stored.getInt("lvl") == 1,
                "Real enchanted book must retain the registered level");
        helper.succeed();
    }
}
