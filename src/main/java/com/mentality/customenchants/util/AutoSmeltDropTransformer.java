package com.mentality.customenchants.util;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class AutoSmeltDropTransformer {

    private AutoSmeltDropTransformer() {
    }

    /**
     * Purely transforms materialized drops. The resolver is called once per non-empty input
     * stack, so the result cannot recursively enter the transformer.
     */
    public static List<ItemStack> transform(List<ItemStack> originalDrops, Function<ItemStack, ItemStack> resolver) {
        if (originalDrops == null || originalDrops.isEmpty()) return List.of();
        if (resolver == null) return copyNonEmpty(originalDrops);

        List<ItemStack> transformed = new ArrayList<>();
        for (ItemStack original : originalDrops) {
            if (original == null || original.isEmpty()) continue;

            ItemStack input = original.copy();
            ItemStack output = resolver.apply(input.copy());
            if (output == null || output.isEmpty()) {
                transformed.add(input);
                continue;
            }

            long totalCount = (long) input.getCount() * output.getCount();
            int maxStackSize = output.getMaxStackSize();
            if (totalCount <= 0 || totalCount > Integer.MAX_VALUE || maxStackSize <= 0) {
                transformed.add(input);
                continue;
            }

            while (totalCount > 0) {
                int count = (int) Math.min(totalCount, maxStackSize);
                ItemStack split = output.copy();
                split.setCount(count);
                transformed.add(split);
                totalCount -= count;
            }
        }
        return List.copyOf(transformed);
    }

    private static List<ItemStack> copyNonEmpty(List<ItemStack> originalDrops) {
        List<ItemStack> copied = new ArrayList<>();
        for (ItemStack original : originalDrops) {
            if (original != null && !original.isEmpty()) copied.add(original.copy());
        }
        return List.copyOf(copied);
    }
}
