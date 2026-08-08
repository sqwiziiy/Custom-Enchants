package com.mentality.customenchants.gametest;

import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
            // GameTest's mock player is not part of the normal player tick list, so advance its
            // item-use state explicitly until the vanilla shield activation delay has elapsed.
            player.tick();
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

    @GameTest(maxTicks = 100)
    public void feedbackCancelsHarmingLikeMagicDamageWithPreExistingPoison(GameTestHelper helper) {
        var level = helper.getLevel();
        Holder<Enchantment> feedback = EnchantmentAccess
                .resolve(ModEnchantments.FEEDBACK, level.registryAccess())
                .orElseThrow();

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setHealth(player.getMaxHealth());
        boolean poisonApplied = player.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
        helper.assertTrue(poisonApplied && player.hasEffect(MobEffects.POISON),
                "fixture must begin poisoned before the Feedback shield is raised");

        ItemStack shield = new ItemStack(Items.SHIELD);
        shield.enchant(feedback, 1);
        player.setItemInHand(InteractionHand.OFF_HAND, shield);
        player.startUsingItem(InteractionHand.OFF_HAND);

        LivingEntity potionSource = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));

        helper.onEachTick(() -> {
            player.tick();
            if (!player.isBlocking()) return;

            float beforeIndirect = player.getHealth();
            DamageSource indirectMagic = level.damageSources().indirectMagic(potionSource, potionSource);
            helper.assertTrue(indirectMagic.is(DamageTypes.INDIRECT_MAGIC),
                    "fixture must use the indirect-magic damage type used by potion-style magic paths");
            boolean indirectApplied = player.hurtServer(level, indirectMagic, 6.0F);
            helper.assertFalse(indirectApplied,
                    "raised Feedback must cancel Harming-like indirect magic before health damage is applied");
            helper.assertTrue(Float.compare(beforeIndirect, player.getHealth()) == 0,
                    "indirect magic must not reduce health while Feedback is raised");
            helper.assertFalse(player.hasEffect(MobEffects.POISON),
                    "a blocked magic hit must purge the Poison that existed before the shield was raised");

            float beforeDirect = player.getHealth();
            DamageSource directMagic = level.damageSources().magic();
            helper.assertTrue(directMagic.is(DamageTypes.MAGIC),
                    "fixture must also exercise direct magic damage");
            boolean directApplied = player.hurtServer(level, directMagic, 6.0F);
            helper.assertFalse(directApplied,
                    "raised Feedback must also cancel direct magic damage");
            helper.assertTrue(Float.compare(beforeDirect, player.getHealth()) == 0,
                    "direct magic must not reduce health while Feedback is raised");

            helper.succeed();
        });
    }
}
