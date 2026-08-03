package com.mentality.customenchants.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

/**
 * Server-safe, holder-aware enchantment access for Minecraft 1.21.1.
 *
 * <p>Gameplay level lookups read the applied {@code ItemEnchantments} component directly and
 * match on the enchantment's {@link ResourceKey}. This is registry-free (no {@link RegistryAccess}
 * needed), fail-closed (missing entry → level 0), null-safe, and makes no client-only assumption.
 * {@link #resolve} is provided for the few call sites (e.g. enchanted-book creation) that genuinely
 * need a {@link Holder}.
 */
public final class EnchantmentAccess {

    private EnchantmentAccess() {
    }

    /** Applied level of {@code key} on {@code stack}, or 0 if absent. */
    public static int getLevel(ItemStack stack, ResourceKey<Enchantment> key) {
        if (stack == null || stack.isEmpty() || key == null) {
            return 0;
        }
        for (var entry : stack.getEnchantments().entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static boolean has(ItemStack stack, ResourceKey<Enchantment> key) {
        return getLevel(stack, key) > 0;
    }

    /** Resolves a holder for {@code key} from the server registry; empty if the entry is missing. */
    public static Optional<Holder.Reference<Enchantment>> resolve(ResourceKey<Enchantment> key, RegistryAccess access) {
        if (key == null || access == null) {
            return Optional.empty();
        }
        return access.registryOrThrow(Registries.ENCHANTMENT).getHolder(key);
    }
}
