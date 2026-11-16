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
import net.minecraft.world.effect.MobEffectCategory;
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
    public void feedbackBlocksOnlyNewHarmfulEffectsWhileRaised(GameTestHelper helper) {
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

            MobEffectInstance existingPoison = player.getEffect(MobEffects.POISON);
            helper.assertTrue(existingPoison != null,
                    "raising Feedback must not remove a harmful effect that was already active");
            int poisonDuration = existingPoison.getDuration();
            int poisonAmplifier = existingPoison.getAmplifier();

            boolean sourceLessApplied = player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200));
            helper.assertFalse(sourceLessApplied,
                    "Feedback must reject a new source-less harmful effect while raised");
            helper.assertFalse(player.hasEffect(MobEffects.WEAKNESS),
                    "Weakness must not become active while Feedback is raised");

            boolean sourcedApplied = player.addEffect(new MobEffectInstance(MobEffects.WITHER, 200), player);
            helper.assertFalse(sourcedApplied,
                    "Feedback must reject a new sourced harmful effect while raised");
            helper.assertFalse(player.hasEffect(MobEffects.WITHER),
                    "Wither must not become active while Feedback is raised");

            MobEffectInstance poisonAfterRejectedEffects = player.getEffect(MobEffects.POISON);
            helper.assertTrue(poisonAfterRejectedEffects != null,
                    "rejecting new harmful effects must not remove the pre-existing Poison");
            helper.assertTrue(poisonAfterRejectedEffects.getDuration() == poisonDuration,
                    "Feedback must not change the duration of a pre-existing harmful effect");
            helper.assertTrue(poisonAfterRejectedEffects.getAmplifier() == poisonAmplifier,
                    "Feedback must not change the amplifier of a pre-existing harmful effect");

            helper.assertTrue(MobEffects.REGENERATION.value().getCategory() == MobEffectCategory.BENEFICIAL,
                    "fixture must use a beneficial effect");
            boolean beneficialApplied = player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200));
            helper.assertTrue(beneficialApplied && player.hasEffect(MobEffects.REGENERATION),
                    "Feedback must not interfere with beneficial effects");

            helper.assertTrue(MobEffects.GLOWING.value().getCategory() == MobEffectCategory.NEUTRAL,
                    "fixture must use a neutral effect");
            boolean neutralApplied = player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
            helper.assertTrue(neutralApplied && player.hasEffect(MobEffects.GLOWING),
                    "Feedback must not interfere with neutral effects");

            helper.succeed();
        });
    }

    @GameTest(maxTicks = 100)
    public void feedbackCancelsActualInstantDamageWithoutTouchingExistingPoison(GameTestHelper helper) {
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

            MobEffectInstance poisonBeforeBlock = player.getEffect(MobEffects.POISON);
            helper.assertTrue(poisonBeforeBlock != null,
                    "pre-existing Poison must still exist after Feedback becomes active");
            int poisonDuration = poisonBeforeBlock.getDuration();
            int poisonAmplifier = poisonBeforeBlock.getAmplifier();

            // Exercise the actual vanilla Instant Damage implementation used by Harming potions,
            // rather than only manufacturing a magic DamageSource directly.
            float beforeHarming = player.getHealth();
            MobEffects.INSTANT_DAMAGE.value().applyInstantenousEffect(
                    level, potionSource, potionSource, player, 0, 1.0D);
            float afterHarming = player.getHealth();
            helper.assertTrue(afterHarming >= beforeHarming,
                    "Harming must not reduce health while Feedback is raised: before="
                            + beforeHarming + ", after=" + afterHarming);

            MobEffectInstance poisonAfterHarming = player.getEffect(MobEffects.POISON);
            helper.assertTrue(poisonAfterHarming != null,
                    "blocking Harming must not remove a harmful effect that already existed");
            helper.assertTrue(poisonAfterHarming.getDuration() == poisonDuration,
                    "blocking Harming must not change the existing Poison duration");
            helper.assertTrue(poisonAfterHarming.getAmplifier() == poisonAmplifier,
                    "blocking Harming must not change the existing Poison amplifier");

            // Keep a direct MAGIC source covered too. Feedback may heal on a successful block, so
            // the health contract is non-decreasing rather than strict equality.
            float beforeDirect = player.getHealth();
            DamageSource directMagic = level.damageSources().magic();
            helper.assertTrue(directMagic.is(DamageTypes.MAGIC),
                    "fixture must also exercise direct magic damage");
            boolean directApplied = player.hurtServer(level, directMagic, 6.0F);
            helper.assertFalse(directApplied,
                    "raised Feedback must cancel direct magic damage");
            float afterDirect = player.getHealth();
            helper.assertTrue(afterDirect >= beforeDirect,
                    "direct magic must not reduce health while Feedback is raised: before="
                            + beforeDirect + ", after=" + afterDirect);

            MobEffectInstance poisonAfterDirectMagic = player.getEffect(MobEffects.POISON);
            helper.assertTrue(poisonAfterDirectMagic != null,
                    "blocking direct magic must not remove the existing Poison");
            helper.assertTrue(poisonAfterDirectMagic.getDuration() == poisonDuration,
                    "blocking direct magic must not change the existing Poison duration");
            helper.assertTrue(poisonAfterDirectMagic.getAmplifier() == poisonAmplifier,
                    "blocking direct magic must not change the existing Poison amplifier");

            helper.succeed();
        });
    }
}
