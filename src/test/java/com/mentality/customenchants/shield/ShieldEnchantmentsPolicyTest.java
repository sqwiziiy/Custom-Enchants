package com.mentality.customenchants.shield;

import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ShieldEnchantmentsPolicyTest {
    @Test
    void emptyOrNonShieldStacksCannotActivateEffects() {
        assertFalse(ShieldEnchantmentsPolicy.validShield(ItemStack.EMPTY));
        assertEquals(0, ShieldEnchantmentsPolicy.reboundLevel(ItemStack.EMPTY));
        assertEquals(0, ShieldEnchantmentsPolicy.feedbackLevel(ItemStack.EMPTY));
        assertEquals(0, ShieldEnchantmentsPolicy.guardiansGraceLevel(ItemStack.EMPTY));
    }

    @Test
    void nullAndUnclassifiedDamageCannotTriggerFeedback() {
        assertFalse(ShieldEnchantmentsPolicy.feedbackDamage(null));
    }
}
