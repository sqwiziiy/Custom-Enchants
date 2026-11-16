package com.mentality.customenchants.shield;

import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

public final class ShieldEnchantmentsPolicy {
    private ShieldEnchantmentsPolicy() {}

    public static boolean validShield(ItemStack shield) {
        if (shield == null || shield.isEmpty() || !(shield.getItem() instanceof ShieldItem)) return false;
        int count = level(ModEnchantments.REBOUND, shield) + level(ModEnchantments.FEEDBACK, shield)
                + level(ModEnchantments.GUARDIANS_GRACE, shield);
        return count == level(ModEnchantments.REBOUND, shield)
                || count == level(ModEnchantments.FEEDBACK, shield)
                || count == level(ModEnchantments.GUARDIANS_GRACE, shield);
    }

    public static int reboundLevel(ItemStack shield) { return validShield(shield) ? level(ModEnchantments.REBOUND, shield) : 0; }
    public static int feedbackLevel(ItemStack shield) { return validShield(shield) ? level(ModEnchantments.FEEDBACK, shield) : 0; }
    public static int guardiansGraceLevel(ItemStack shield) { return validShield(shield) ? level(ModEnchantments.GUARDIANS_GRACE, shield) : 0; }

    public static boolean feedbackDamage(DamageSource source) {
        if (source == null) return false;
        return FeedbackMagicBlockPolicy.allowedSource(
                source.is(DamageTypes.MAGIC),
                source.is(DamageTypes.INDIRECT_MAGIC),
                source.getDirectEntity() instanceof ShulkerBullet);
    }

    /**
     * Read-only equivalent of the pre-1.21.11 {@code LivingEntity.isDamageSourceBlocked(DamageSource)}
     * predicate. Mirrors the blocking-decision steps of {@code LivingEntity.applyItemBlocking}
     * (bypass tag, piercing arrows, facing angle, {@link BlocksAttacks#resolveBlockedDamage})
     * without its side effects (item damage, knockback), so it is safe to call speculatively
     * before vanilla's own {@code hurt} has run.
     *
     * This helper is for vanilla-style shield mechanics. Feedback's independent magic guard must
     * not use it, because Feedback intentionally cancels allowlisted magic that vanilla shields
     * may classify as bypassing or otherwise unblockable.
     */
    public static boolean wouldBlockDamage(LivingEntity defender, DamageSource source, float amount) {
        if (defender == null || source == null || amount <= 0f || !defender.isBlocking()) return false;
        ItemStack blockingWith = defender.getItemBlockingWith();
        if (blockingWith == null || blockingWith.isEmpty()) return false;
        BlocksAttacks blocksAttacks = blockingWith.get(DataComponents.BLOCKS_ATTACKS);
        if (blocksAttacks == null) return false;
        if (blocksAttacks.bypassedBy().map(source::is).orElse(false)) return false;
        if (source.getDirectEntity() instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0) return false;

        double angle;
        Vec3 sourcePos = source.getSourcePosition();
        if (sourcePos == null) {
            Entity sourceEntity = source.getDirectEntity() != null ? source.getDirectEntity() : source.getEntity();
            if (sourceEntity != null) sourcePos = sourceEntity.position();
        }
        if (sourcePos != null) {
            Vec3 view = defender.calculateViewVector(0f, defender.getYHeadRot());
            Vec3 diff = sourcePos.subtract(defender.position());
            Vec3 flat = new Vec3(diff.x, 0.0, diff.z).normalize();
            angle = Math.acos(flat.dot(view));
        } else {
            angle = Math.PI;
        }
        return blocksAttacks.resolveBlockedDamage(source, amount, angle) > 0f;
    }

    private static int level(ResourceKey<Enchantment> key, ItemStack shield) {
        return Math.max(0, Math.min(3, EnchantmentAccess.getLevel(shield, key)));
    }

}
