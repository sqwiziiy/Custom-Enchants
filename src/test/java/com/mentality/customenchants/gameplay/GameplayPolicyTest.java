package com.mentality.customenchants.gameplay;

import com.mentality.customenchants.anvil.AnvilResultPolicy;
import com.mentality.customenchants.combat.KillingWeaponPolicy;
import com.mentality.customenchants.kinetic.KineticDischargeTargetPolicy;
import com.mentality.customenchants.kinetic.KineticDischargeWearTracker;
import com.mentality.customenchants.magnet.MagnetPickupPolicy;
import com.mentality.customenchants.shield.FeedbackMagicBlockPolicy;
import com.mentality.customenchants.enchantment.XpSyphonPolicy;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameplayPolicyTest {
    @Test
    void feedbackMagicGuardIsIndependentOfVanillaShieldBypassAndFacingRules() {
        assertTrue(FeedbackMagicBlockPolicy.allowedSource(true, false, false));
        assertTrue(FeedbackMagicBlockPolicy.allowedSource(false, true, false));
        assertTrue(FeedbackMagicBlockPolicy.allowedSource(false, false, true));
        assertFalse(FeedbackMagicBlockPolicy.allowedSource(false, false, false));

        assertTrue(FeedbackMagicBlockPolicy.shouldBlock(true, true, true));
        assertFalse(FeedbackMagicBlockPolicy.shouldBlock(false, true, true));
        assertFalse(FeedbackMagicBlockPolicy.shouldBlock(true, false, true));
        assertFalse(FeedbackMagicBlockPolicy.shouldBlock(true, true, false));
    }

    @Test
    void kineticRefundNeverRepairsPreExistingDamage() {
        assertEquals(200, KineticDischargeWearTracker.refundOneNewWear(200, 200));
        assertEquals(200, KineticDischargeWearTracker.refundOneNewWear(201, 200));
        assertEquals(200, KineticDischargeWearTracker.refundOneNewWear(200, 200));
        assertEquals(200, KineticDischargeWearTracker.refundOneNewWear(201, 200));
        assertEquals(201, KineticDischargeWearTracker.refundOneNewWear(201, -1));
    }

    @Test
    void kineticRadiusAndVectorPolicyIsFiniteAndExact() {
        assertTrue(KineticDischargeTargetPolicy.withinRadius(3, 0, 4, 5));
        assertFalse(KineticDischargeTargetPolicy.withinRadius(3, 0, 4.01, 5));
        assertFalse(KineticDischargeTargetPolicy.withinRadius(Double.NaN, 0, 0, 5));
        assertTrue(KineticDischargeTargetPolicy.finiteHorizontalVector(0, 0));
        assertFalse(KineticDischargeTargetPolicy.finiteHorizontalVector(Double.NaN, 0));
    }

    @Test
    void killingWeaponRequiresDirectPlayerDamage() {
        assertTrue(KillingWeaponPolicy.directPlayerHit(true, true));
        assertFalse(KillingWeaponPolicy.directPlayerHit(true, false));
        assertFalse(KillingWeaponPolicy.directPlayerHit(false, true));
    }

    @Test
    void magnetForeignOwnershipWindowIsRespected() {
        UUID owner = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        assertTrue(MagnetPickupPolicy.foreignThrowerAllowed(null, owner, 0));
        assertTrue(MagnetPickupPolicy.foreignThrowerAllowed(owner, owner, 0));
        assertFalse(MagnetPickupPolicy.foreignThrowerAllowed(other, owner, 5_999));
        assertTrue(MagnetPickupPolicy.foreignThrowerAllowed(other, owner, 6_000));
    }

    @Test
    void invalidAnvilResultIsRejectedButTridentResultIsValid() {
        assertTrue(AnvilResultPolicy.rejectShadowBladeResult(true, false));
        assertFalse(AnvilResultPolicy.rejectShadowBladeResult(true, true));
        assertFalse(AnvilResultPolicy.rejectShadowBladeResult(false, false));
    }

    @Test
    void skyRageAnvilResultOnlyAllowsBowAndCrossbow() {
        assertTrue(AnvilResultPolicy.rejectSkyRageResult(true,
                new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.TRIDENT)));
        assertFalse(AnvilResultPolicy.rejectSkyRageResult(true,
                new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BOW)));
        assertFalse(AnvilResultPolicy.rejectSkyRageResult(true,
                new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.CROSSBOW)));
    }

    @Test
    void xpSyphonUsesDocumentedDeterministicChanceAndAmount() {
        assertEquals(0.15f, XpSyphonPolicy.chance(3));
        assertEquals(3, XpSyphonPolicy.orbValue(3));
        assertTrue(XpSyphonPolicy.triggers(3, 0.149f));
        assertFalse(XpSyphonPolicy.triggers(3, 0.15f));
    }
}
