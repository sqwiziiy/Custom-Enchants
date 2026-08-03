package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.CustomEnchantsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

/**
 * Registry identity for the 19 custom enchantments.
 *
 * <p>On Minecraft 1.21.1 enchantments are a data-driven (datapack) registry. The actual
 * definitions ship as JSON under {@code data/custom-enchants/enchantment/}. These
 * {@link ResourceKey} constants are the stable code-side identity used for holder lookups,
 * trades and gameplay handlers. No runtime {@code Registry.register(...)} is performed.
 */
public final class ModEnchantments {

    private ModEnchantments() {
    }

    public static final ResourceKey<Enchantment> GLOW_STRIKE = key("glow_strike");
    public static final ResourceKey<Enchantment> DOUBLE_JUMP = key("double_jump");
    public static final ResourceKey<Enchantment> DRILL = key("drill");
    public static final ResourceKey<Enchantment> POISON_BLADE = key("poison_blade");
    public static final ResourceKey<Enchantment> LUMBERJACK = key("lumberjack");
    public static final ResourceKey<Enchantment> SHADOW_BLADE = key("shadow_blade");
    public static final ResourceKey<Enchantment> MAGNET = key("magnet");
    public static final ResourceKey<Enchantment> AUTO_SMELT = key("auto_smelt");
    public static final ResourceKey<Enchantment> VEGETATION = key("vegetation");
    public static final ResourceKey<Enchantment> REBOUND = key("rebound");
    public static final ResourceKey<Enchantment> FEEDBACK = key("feedback");
    public static final ResourceKey<Enchantment> SECOND_WIND = key("second_wind");
    public static final ResourceKey<Enchantment> GUARDIANS_GRACE = key("guardians_grace");
    public static final ResourceKey<Enchantment> VULNERABILITY = key("vulnerability");
    public static final ResourceKey<Enchantment> TETHER_MASTER = key("tether_master");
    public static final ResourceKey<Enchantment> SKY_RAGE = key("sky_rage");
    public static final ResourceKey<Enchantment> XP_SYPHON = key("xp_syphon");
    public static final ResourceKey<Enchantment> KINETIC_DISCHARGE = key("kinetic_discharge");
    public static final ResourceKey<Enchantment> SCULK_BLOOM = key("sculk_bloom");

    /** All 19 keys in declaration order. */
    public static final List<ResourceKey<Enchantment>> ALL = List.of(
            GLOW_STRIKE, DOUBLE_JUMP, DRILL, POISON_BLADE, LUMBERJACK, SHADOW_BLADE, MAGNET,
            AUTO_SMELT, VEGETATION, REBOUND, FEEDBACK, SECOND_WIND, GUARDIANS_GRACE, VULNERABILITY,
            TETHER_MASTER, SKY_RAGE, XP_SYPHON, KINETIC_DISCHARGE, SCULK_BLOOM);

    public static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath(CustomEnchantsMod.MOD_ID, path));
    }
}
