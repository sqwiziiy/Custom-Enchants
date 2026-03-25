package com.mentality.customenchants.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModConfigScreen {

    public static Screen create(Screen parent) {
        ModConfig config = ModConfig.get();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.custom-enchants.title"));

        builder.setSavingRunnable(ModConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Glow Strike category
        ConfigCategory glowStrike = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.glow_strike"));

        glowStrike.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.glow_strike.enabled"),
                        config.glowStrikeEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.glowStrikeEnabled = val)
                .build());

        glowStrike.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.glow_strike.duration_l1"),
                        config.glowStrikeDurationL1)
                .setDefaultValue(40)
                .setMin(1).setMax(6000)
                .setSaveConsumer(val -> config.glowStrikeDurationL1 = val)
                .build());

        glowStrike.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.glow_strike.duration_l2"),
                        config.glowStrikeDurationL2)
                .setDefaultValue(80)
                .setMin(1).setMax(6000)
                .setSaveConsumer(val -> config.glowStrikeDurationL2 = val)
                .build());

        glowStrike.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.glow_strike.duration_l3"),
                        config.glowStrikeDurationL3)
                .setDefaultValue(140)
                .setMin(1).setMax(6000)
                .setSaveConsumer(val -> config.glowStrikeDurationL3 = val)
                .build());

        // Double Jump category
        ConfigCategory doubleJump = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.double_jump"));

        doubleJump.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.double_jump.enabled"),
                        config.doubleJumpEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.doubleJumpEnabled = val)
                .build());

        return builder.build();
    }
}
