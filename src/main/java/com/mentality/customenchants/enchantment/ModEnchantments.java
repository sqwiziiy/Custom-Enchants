package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.CustomEnchantsMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {

    public static final Enchantment GLOW_STRIKE = new GlowStrikeEnchantment();
    public static final Enchantment DOUBLE_JUMP = new DoubleJumpEnchantment();
    public static final Enchantment DRILL = new DrillEnchantment();
    public static final Enchantment POISON_BLADE = new PoisonBladeEnchantment();
    public static final Enchantment LUMBERJACK = new LumberjackEnchantment();
    public static final Enchantment SHADOW_BLADE = new ShadowBladeEnchantment();
    public static final Enchantment MAGNET = new MagnetEnchantment();
    public static final Enchantment AUTO_SMELT = new AutoSmeltEnchantment();
    public static final Enchantment VEGETATION = new VegetationEnchantment();
    public static final Enchantment REBOUND = new ReboundEnchantment();
    public static final Enchantment FEEDBACK = new FeedbackEnchantment();
    public static final Enchantment SECOND_WIND = new SecondWindEnchantment();
    public static final Enchantment GUARDIANS_GRACE = new GuardiansGraceEnchantment();
    public static final Enchantment VULNERABILITY = new VulnerabilityEnchantment();
    public static final Enchantment TETHER_MASTER = new TetherMasterEnchantment();
    public static final Enchantment SKY_RAGE = new SkyRageEnchantment();

    public static void register() {
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "glow_strike"),
                GLOW_STRIKE
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "double_jump"),
                DOUBLE_JUMP
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "drill"),
                DRILL
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "poison_blade"),
                POISON_BLADE
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "lumberjack"),
                LUMBERJACK
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "shadow_blade"),
                SHADOW_BLADE
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "magnet"),
                MAGNET
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "auto_smelt"),
                AUTO_SMELT
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "vegetation"),
                VEGETATION
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "rebound"),
                REBOUND
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "feedback"),
                FEEDBACK
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "second_wind"),
                SECOND_WIND
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "guardians_grace"),
                GUARDIANS_GRACE
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "vulnerability"),
                VULNERABILITY
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "tether_master"),
                TETHER_MASTER
        );
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation(CustomEnchantsMod.MOD_ID, "sky_rage"),
                SKY_RAGE
        );
    }
}
