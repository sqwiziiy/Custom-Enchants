package com.mentality.customenchants.gametest;

import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameType;

public final class FeedbackGameTests {
    @GameTest(maxTicks = 100)
    public void feedbackBlocksNewHarmfulEffectsWhenAnotherHarmfulEffectIsAlreadyActive(GameTestHelper helper) {
        Holder<Enchantment> feedback = EnchantmentAccess
                .resolve(ModEnchantments.FEEDBACK, helper.getLevel().registryAccess())
                .orElseThrow();

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        boolean poisonApplied = player.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
        helper.assertTrue(poisonApplied && player.hasEffect(MobEffects.POISON),
                "fixture must begin with a pre-existing harmful effect");

        ItemStack shield = new ItemStack(Items.SHIELD);
        shield.enchant(feedback, 1);
        player.setItemInHand(InteractionHand.OFF_HAND, shield);
        player.startUsingItem(InteractionHand.OFF_HAND);

        helper.onEachTick(() -> {
            if (!player.isBlocking()) return;

            boolean sourceLessApplied = player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200));
            helper.assertFalse(sourceLessApplied,
                    "Feedback must reject a source-less harmful effect while another harmful effect is already active");
            helper.assertTrue(player.hasEffect(MobEffects.POISON),
                    "pre-existing Poison must remain until Feedback actually blocks an attack");
            helper.assertFalse(player.hasEffect(MobEffects.WEAKNESS),
                    "Weakness must not become active through the source-less addEffect path");

            boolean sourcedApplied = player.addEffect(new MobEffectInstance(MobEffects.WITHER, 200), player);
            helper.assertFalse(sourcedApplied,
                    "Feedback must reject a sourced harmful effect while another harmful effect is already active");
            helper.assertFalse(player.hasEffect(MobEffects.WITHER),
                    "Wither must not become active through the sourced addEffect path");

            helper.succeed();
        });
    }
}
