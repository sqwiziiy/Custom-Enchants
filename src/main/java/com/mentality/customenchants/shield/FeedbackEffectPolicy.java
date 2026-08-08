package com.mentality.customenchants.shield;

import com.mentality.customenchants.config.ModConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;

/** Source-independent harmful-effect protection while Feedback is actively blocking. */
public final class FeedbackEffectPolicy {
    private FeedbackEffectPolicy() {
    }

    public static boolean shouldBlock(LivingEntity defender) {
        if (!(defender instanceof Player player)) return false;
        ItemStack shield = player.getItemBlockingWith();
        int feedbackLevel = shield == null ? 0 : ShieldEnchantmentsPolicy.feedbackLevel(shield);
        BlocksAttacks blocks = shield == null ? null : shield.get(DataComponents.BLOCKS_ATTACKS);
        return shouldBlockState(
                ModConfig.get().feedbackEnabled,
                player.isBlocking(),
                feedbackLevel,
                shield != null && !shield.isEmpty() && shield.getItem() instanceof ShieldItem && blocks != null);
    }

    static boolean shouldBlockState(boolean enabled, boolean blocking, int feedbackLevel, boolean validBlockingShield) {
        return enabled && blocking && feedbackLevel > 0 && validBlockingShield;
    }
}
