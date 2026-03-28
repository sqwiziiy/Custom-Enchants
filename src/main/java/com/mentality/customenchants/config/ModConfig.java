package com.mentality.customenchants.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mentality.customenchants.CustomEnchantsMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

    private static ModConfig INSTANCE = new ModConfig();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Glow Strike settings
    public int glowStrikeDurationL1 = 40;
    public int glowStrikeDurationL2 = 80;
    public int glowStrikeDurationL3 = 140;
    public boolean glowStrikeEnabled = true;

    // Double Jump settings
    public boolean doubleJumpEnabled = true;

    // Drill settings
    public boolean drillEnabled = true;

    // Poison Blade settings
    public int poisonBladeDurationL1 = 40;
    public int poisonBladeDurationL2 = 60;
    public int poisonBladeDurationL3 = 80;
    public boolean poisonBladeEnabled = true;

    // Lumberjack settings
    public boolean lumberjackEnabled = true;
    public int lumberjackMaxBlocksL1 = 16;
    public int lumberjackMaxBlocksL2 = 48;
    public int lumberjackMaxBlocksL3 = 128;

    // Shadow Blade settings
    public boolean shadowBladeEnabled = true;
    public int shadowBladeChanceL1 = 15;
    public int shadowBladeChanceL2 = 25;
    public int shadowBladeChanceL3 = 35;
    public int shadowBladeSlowDurationL1 = 20;
    public int shadowBladeSlowDurationL2 = 40;
    public int shadowBladeSlowDurationL3 = 60;

    // Magnet settings
    public boolean magnetEnabled = true;
    public int magnetRadius = 5;

    // Auto Smelt settings
    public boolean autoSmeltEnabled = true;

    // Vegetation settings
    public boolean vegetationEnabled = true;
    public int vegetationChanceL1 = 30;
    public int vegetationChanceL2 = 60;
    public int vegetationChanceL3 = 100;

    // Rebound settings
    public boolean reboundEnabled = true;
    public int reboundKnockbackL1 = 5;
    public int reboundKnockbackL2 = 10;
    public int reboundKnockbackL3 = 20;

    // Feedback settings
    public boolean feedbackEnabled = true;
    public float feedbackHealAmount = 2.0f;
    public int feedbackRepairAmount = 2;

    // Second Wind settings
    public boolean secondWindEnabled = true;
    public int secondWindSpeedDuration = 5;
    public int secondWindCooldown = 60;

    // Guardian's Grace settings
    public boolean guardiansGraceEnabled = true;
    public int guardiansGraceChanceL1 = 10;
    public int guardiansGraceChanceL2 = 20;
    public int guardiansGraceChanceL3 = 30;

    // Vulnerability (Аналитик) settings
    public boolean vulnerabilityEnabled = true;
    public int vulnerabilityIgnoreL1 = 10;
    public int vulnerabilityIgnoreL2 = 20;
    public int vulnerabilityIgnoreL3 = 30;

    // Tether Master (Крепкая нить) settings
    public boolean tetherMasterEnabled = true;

    // Sky Rage (Ярость Неба) settings
    public boolean skyRageEnabled = true;
    public int skyRageCooldownTicks = 30;

    // XP Syphon (Собиратель искр) settings
    public boolean xpSyphonEnabled = true;

    public static ModConfig get() {
        return INSTANCE;
    }

    public static void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("custom-enchants.json");
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                INSTANCE = GSON.fromJson(json, ModConfig.class);
                CustomEnchantsMod.LOGGER.info("Config loaded from {}", configPath);
            } catch (IOException e) {
                CustomEnchantsMod.LOGGER.error("Failed to load config, using defaults", e);
                INSTANCE = new ModConfig();
            }
        } else {
            INSTANCE = new ModConfig();
            save();
        }
    }

    public static void save() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("custom-enchants.json");
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            CustomEnchantsMod.LOGGER.error("Failed to save config", e);
        }
    }
}
