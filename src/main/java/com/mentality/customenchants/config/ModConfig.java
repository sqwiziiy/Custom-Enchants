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
