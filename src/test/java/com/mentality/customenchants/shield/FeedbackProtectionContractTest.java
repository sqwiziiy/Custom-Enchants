package com.mentality.customenchants.shield;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedbackProtectionContractTest {
    @Test
    void activeFeedbackShieldBlocksHarmfulEffectsWithoutDependingOnSource() {
        assertTrue(FeedbackEffectPolicy.shouldBlockState(true, true, 1, true));
        assertFalse(FeedbackEffectPolicy.shouldBlockState(false, true, 1, true));
        assertFalse(FeedbackEffectPolicy.shouldBlockState(true, false, 1, true));
        assertFalse(FeedbackEffectPolicy.shouldBlockState(true, true, 0, true));
        assertFalse(FeedbackEffectPolicy.shouldBlockState(true, true, 1, false));
    }

    @Test
    void effectApplicationAndConfirmedBlockKeepOriginalFeedbackContract() throws Exception {
        String effectMixin = Files.readString(Path.of(
                "src/main/java/com/mentality/customenchants/mixin/FeedbackEffectApplicationMixin.java"));
        String policy = Files.readString(Path.of(
                "src/main/java/com/mentality/customenchants/shield/FeedbackEffectPolicy.java"));
        String shieldMixin = Files.readString(Path.of(
                "src/main/java/com/mentality/customenchants/mixin/ShieldFeedbackMixin.java"));

        assertTrue(effectMixin.contains("FeedbackEffectPolicy.shouldBlock((LivingEntity) (Object) this)"));
        assertFalse(effectMixin.contains("FeedbackEffectPolicy.shouldBlock((LivingEntity) (Object) this, source)"));
        assertFalse(policy.contains("source == null"));
        assertFalse(policy.contains("source.position()"));
        assertTrue(shieldMixin.contains("if (!evidence.vanillaBlocked()) return;"));
        assertTrue(shieldMixin.contains("player.removeEffect(effect.getEffect())"));
        assertTrue(shieldMixin.contains("if (!ShieldEnchantmentsPolicy.feedbackDamage(source)) return;"));
    }
}
