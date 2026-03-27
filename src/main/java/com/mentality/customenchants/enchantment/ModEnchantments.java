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
    }
}
