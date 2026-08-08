package com.mentality.customenchants.shield;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedbackProtectionContractTest {
    @Test
    void feedbackRestoresSourceIndependentHarmfulEffectInterception() throws Exception {
        String mixin = Files.readString(Path.of(
                "src/main/java/com/mentality/customenchants/mixin/ShieldFeedbackMixin.java"));

        assertTrue(mixin.contains("customEnchants$hasActiveFeedbackShield()"));
        assertTrue(mixin.contains("@Inject(method = \"addEffect"));
        assertTrue(mixin.contains("@Inject(method = \"forceAddEffect\""));
        assertTrue(mixin.contains("effect.getEffect().getCategory() == MobEffectCategory.HARMFUL"));
        assertFalse(mixin.contains("source.position()"));
    }

    @Test
    void confirmedBlockClearsExistingHarmfulEffectsBeforeMagicRewardGate() throws Exception {
        String mixin = Files.readString(Path.of(
                "src/main/java/com/mentality/customenchants/mixin/ShieldFeedbackMixin.java"));

        int confirmedBlock = mixin.indexOf("if (!evidence.vanillaBlocked()) return;");
        int purge = mixin.indexOf("player.removeEffect(effect.getEffect())", confirmedBlock);
        int rewardGate = mixin.indexOf("if (!ShieldEnchantmentsPolicy.feedbackDamage(source)) return;", confirmedBlock);

        assertTrue(confirmedBlock >= 0);
        assertTrue(purge > confirmedBlock);
        assertTrue(rewardGate > purge);
    }
}
