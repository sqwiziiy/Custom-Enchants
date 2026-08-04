package com.mentality.customenchants.projectile;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Immutable, shot-time snapshot of the custom enchantments on a projectile's weapon. */
public record ProjectileEnchantmentContext(
        int skyRage,
        int vulnerability,
        int shadowBlade,
        int glowStrike,
        String weaponType
) {
    public static final int SCHEMA_VERSION = 1;
    public static final String NBT_KEY = "custom_enchants";
    public static final ProjectileEnchantmentContext EMPTY = new ProjectileEnchantmentContext(0, 0, 0, 0, "none");

    public ProjectileEnchantmentContext {
        skyRage = clamp(skyRage);
        vulnerability = clamp(vulnerability);
        shadowBlade = clamp(shadowBlade);
        glowStrike = clamp(glowStrike);
        weaponType = weaponType == null ? "none" : switch (weaponType) {
            case "bow", "crossbow", "trident" -> weaponType;
            default -> "none";
        };
    }

    public static ProjectileEnchantmentContext fromWeapon(ItemStack weapon, Entity owner) {
        if (weapon == null || weapon.isEmpty() || owner == null) return EMPTY;
        if (weapon.getItem() instanceof BowItem) {
            return new ProjectileEnchantmentContext(level(ModEnchantments.SKY_RAGE, weapon),
                    level(ModEnchantments.VULNERABILITY, weapon), 0, 0, "bow");
        }
        if (weapon.getItem() instanceof CrossbowItem) {
            return new ProjectileEnchantmentContext(level(ModEnchantments.SKY_RAGE, weapon),
                    level(ModEnchantments.VULNERABILITY, weapon), 0, 0, "crossbow");
        }
        if (weapon.getItem() instanceof TridentItem) {
            return new ProjectileEnchantmentContext(0, 0,
                    level(ModEnchantments.SHADOW_BLADE, weapon), level(ModEnchantments.GLOW_STRIKE, weapon), "trident");
        }
        return EMPTY;
    }

    private static int level(ResourceKey<Enchantment> key, ItemStack stack) {
        return clamp(EnchantmentAccess.getLevel(stack, key));
    }

    private static int clamp(int value) { return Math.max(0, Math.min(3, value)); }

    public int vulnerabilityPercent() {
        return switch (vulnerability) {
            case 1 -> ModConfig.get().vulnerabilityIgnoreL1;
            case 2 -> ModConfig.get().vulnerabilityIgnoreL2;
            case 3 -> ModConfig.get().vulnerabilityIgnoreL3;
            default -> 0;
        };
    }

    public void save(CompoundTag parent) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("version", SCHEMA_VERSION);
        tag.putInt("sky_rage", skyRage);
        tag.putInt("vulnerability", vulnerability);
        tag.putInt("shadow_blade", shadowBlade);
        tag.putInt("glow_strike", glowStrike);
        tag.putString("weapon_type", weaponType);
        parent.put(NBT_KEY, tag);
    }

    public static ProjectileEnchantmentContext load(CompoundTag parent) {
        if (parent == null) return EMPTY;
        CompoundTag tag = parent.getCompoundOrEmpty(NBT_KEY);
        if (tag.getInt("version").orElse(-1) != SCHEMA_VERSION) return EMPTY;
        return new ProjectileEnchantmentContext(tag.getInt("sky_rage").orElse(0), tag.getInt("vulnerability").orElse(0),
                tag.getInt("shadow_blade").orElse(0), tag.getInt("glow_strike").orElse(0),
                tag.getString("weapon_type").orElse("none"));
    }

    public void save(ValueOutput parent) {
        ValueOutput tag = parent.child(NBT_KEY);
        tag.putInt("version", SCHEMA_VERSION);
        tag.putInt("sky_rage", skyRage);
        tag.putInt("vulnerability", vulnerability);
        tag.putInt("shadow_blade", shadowBlade);
        tag.putInt("glow_strike", glowStrike);
        tag.putString("weapon_type", weaponType);
    }

    public static ProjectileEnchantmentContext load(ValueInput parent) {
        if (parent == null) return EMPTY;
        ValueInput tag = parent.childOrEmpty(NBT_KEY);
        if (tag.getIntOr("version", -1) != SCHEMA_VERSION) return EMPTY;
        return new ProjectileEnchantmentContext(tag.getIntOr("sky_rage", 0), tag.getIntOr("vulnerability", 0),
                tag.getIntOr("shadow_blade", 0), tag.getIntOr("glow_strike", 0),
                tag.getStringOr("weapon_type", "none"));
    }
}
