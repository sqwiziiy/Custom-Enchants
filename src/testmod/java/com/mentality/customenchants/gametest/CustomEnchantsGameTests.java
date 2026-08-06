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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;

public final class CustomEnchantsGameTests implements FabricGameTest {

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 100)
    public void autoSmeltTransformsActualPlayerBreakDrops(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
        ItemStack tool = new ItemStack(Items.IRON_PICKAXE);
        tool.enchant(ModEnchantments.AUTO_SMELT, 1);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        BlockPos pos = new BlockPos(2, 1, 2);
        player.setPos(Vec3.atCenterOf(helper.absolutePos(pos)).add(3.0D, 0.0D, 0.0D));
        helper.setBlock(pos, Blocks.IRON_ORE);

        helper.assertTrue(player.gameMode.destroyBlock(helper.absolutePos(pos)), "The real server break path must destroy iron ore");
        helper.runAfterDelay(2, () -> {
            var drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                            new AABB(helper.absolutePos(pos)).inflate(4.0D))
                    .stream().map(ItemEntity::getItem).toList();
            helper.assertTrue(drops.stream().anyMatch(stack -> stack.is(Items.IRON_INGOT)),
                    "Auto Smelt must replace the actual spawned raw-iron drop with an iron ingot; observed=" + drops);
            helper.assertTrue(drops.stream().noneMatch(stack -> stack.is(Items.RAW_IRON)),
                    "Auto Smelt must not leave a duplicate raw-iron drop");
            helper.succeed();
        });
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 100)
    public void magnetCollectsEveryDropFromActualLumberjackSecondaryBreaks(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
        ItemStack tool = new ItemStack(Items.IRON_AXE);
        tool.enchant(ModEnchantments.LUMBERJACK, 1);
        tool.enchant(ModEnchantments.MAGNET, 1);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);

        for (int y = 1; y <= 5; y++) helper.setBlock(new BlockPos(3, y, 3), Blocks.OAK_LOG);
        helper.setBlock(new BlockPos(3, 6, 3), Blocks.OAK_LEAVES);
        helper.setBlock(new BlockPos(4, 6, 3), Blocks.OAK_LEAVES);
        player.setPos(Vec3.atCenterOf(helper.absolutePos(new BlockPos(1, 1, 3))));

        helper.assertTrue(player.gameMode.destroyBlock(helper.absolutePos(new BlockPos(3, 1, 3))),
                "The initial log must use the real server break path");
        helper.runAfterDelay(3, () -> {
            int collected = player.getInventory().countItem(Items.OAK_LOG);
            int remainder = helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                            new AABB(helper.absolutePos(new BlockPos(3, 3, 3))).inflate(8.0D))
                    .stream().map(ItemEntity::getItem).filter(stack -> stack.is(Items.OAK_LOG)).mapToInt(ItemStack::getCount).sum();
            helper.assertTrue(collected + remainder == 5,
                    "Five-log accounting invariant failed: picked=" + collected + ", remainder=" + remainder);
            helper.assertTrue(collected == 5 && remainder == 0,
                    "Magnet must pick all five drops from initial and Lumberjack secondary breaks with a free inventory");
            helper.succeed();
        });
    }

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
