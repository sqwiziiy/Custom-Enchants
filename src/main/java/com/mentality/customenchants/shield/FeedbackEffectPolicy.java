package com.mentality.customenchants.shield;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.phys.Vec3;

/** Front-facing, source-scoped harmful-effect protection for Feedback. */
public final class FeedbackEffectPolicy {
    private FeedbackEffectPolicy() {
    }

    public static boolean shouldBlock(LivingEntity defender, Entity source) {
        if (!(defender instanceof Player player) || source == null || source.level() != defender.level()) return false;
        ItemStack shield = player.getItemBlockingWith();
        if (shield == null || shield.isEmpty() || !(shield.getItem() instanceof ShieldItem)
                || ShieldEnchantmentsPolicy.feedbackLevel(shield) <= 0 || !player.isBlocking()) return false;
        Vec3 sourcePos = source.position();
        if (!Double.isFinite(sourcePos.x) || !Double.isFinite(sourcePos.y) || !Double.isFinite(sourcePos.z)) return false;
        BlocksAttacks blocks = shield.get(DataComponents.BLOCKS_ATTACKS);
        if (blocks == null) return false;
        Vec3 view = defender.calculateViewVector(0f, defender.getYHeadRot());
        Vec3 diff = sourcePos.subtract(defender.position());
        Vec3 flat = new Vec3(diff.x, 0.0D, diff.z);
        if (flat.lengthSqr() < 1.0E-8D) return true;
        double angle = Math.acos(Math.max(-1.0D, Math.min(1.0D, flat.normalize().dot(view))));
        return angle <= Math.PI / 2.0D;
    }
}
