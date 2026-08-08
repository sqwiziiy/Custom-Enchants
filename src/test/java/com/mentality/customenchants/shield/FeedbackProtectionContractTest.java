package com.mentality.customenchants.shield;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedbackProtectionContractTest {
    @Test
    void feedbackInterceptsEveryHarmfulEffectApplicationPath() throws Exception {
        String mixin = Files.readString(Path.of(
                "src/main/java/com/mentality/customenchants/mixin/ShieldFeedbackMixin.java"));

        assertTrue(mixin.contains("customEnchants$hasActiveFeedbackShield()"));
        assertTrue(mixin.contains("addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"));
        assertTrue(mixin.contains("addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"));
        assertTrue(mixin.contains("@Inject(method = \"forceAddEffect\""));
        assertTrue(mixin.contains("effect.getEffect().getCategory() == MobEffectCategory.HARMFUL"));
        assertFalse(mixin.contains("source.position()"));
    }

    @Test
    void feedbackPreservesEffectsThatWereAlreadyActive() throws Exception {
        String mixin = Files.readString(Path.of(
                "src/main/java/com/mentality/customenchants/mixin/ShieldFeedbackMixin.java"));

        assertTrue(mixin.contains("if (!evidence.vanillaBlocked()) return;"));
        assertFalse(mixin.contains("player.removeEffect("));
        assertTrue(mixin.contains("if (!ShieldEnchantmentsPolicy.feedbackDamage(source)) return;"));
    }
}
