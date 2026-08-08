package com.mentality.customenchants.shield;

import com.mentality.customenchants.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

/** Source-independent harmful-effect protection while Feedback is actively blocking. */
public final class FeedbackEffectPolicy {
    private FeedbackEffectPolicy() {
    }

    public static boolean shouldBlock(LivingEntity defender) {
        if (!(defender instanceof Player player)) return false;
        ItemStack shield = player.getUseItem();
        int feedbackLevel = ShieldEnchantmentsPolicy.feedbackLevel(shield);
        return shouldBlockState(
                ModConfig.get().feedbackEnabled,
                player.isBlocking(),
                feedbackLevel,
                shield != null && !shield.isEmpty() && shield.getItem() instanceof ShieldItem);
    }

    static boolean shouldBlockState(boolean enabled, boolean blocking, int feedbackLevel, boolean validShield) {
        return enabled && blocking && feedbackLevel > 0 && validShield;
    }
}
