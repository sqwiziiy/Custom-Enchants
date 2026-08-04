package com.mentality.customenchants.anvil;

import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public final class AnvilResultPolicy {
    private AnvilResultPolicy() {
    }

    public static boolean rejectShadowBladeResult(boolean hasShadowBlade, boolean isTrident) {
        return hasShadowBlade && !isTrident;
    }

    public static boolean rejectSkyRageResult(boolean hasSkyRage, ItemStack result) {
        return hasSkyRage && (result == null || !(result.getItem() instanceof BowItem)
                && !(result.getItem() instanceof CrossbowItem));
    }
}
