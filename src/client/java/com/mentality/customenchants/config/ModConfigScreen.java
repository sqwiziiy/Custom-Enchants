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

        // Drill category
        ConfigCategory drill = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.drill"));

        drill.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.drill.enabled"),
                        config.drillEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.drillEnabled = val)
                .build());

        // Poison Blade category
        ConfigCategory poisonBlade = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.poison_blade"));

        poisonBlade.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.poison_blade.enabled"),
                        config.poisonBladeEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.poisonBladeEnabled = val)
                .build());

        poisonBlade.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.poison_blade.duration_l1"),
                        config.poisonBladeDurationL1)
                .setDefaultValue(40)
                .setMin(1).setMax(6000)
                .setSaveConsumer(val -> config.poisonBladeDurationL1 = val)
                .build());

        poisonBlade.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.poison_blade.duration_l2"),
                        config.poisonBladeDurationL2)
                .setDefaultValue(60)
                .setMin(1).setMax(6000)
                .setSaveConsumer(val -> config.poisonBladeDurationL2 = val)
                .build());

        poisonBlade.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.poison_blade.duration_l3"),
                        config.poisonBladeDurationL3)
                .setDefaultValue(80)
                .setMin(1).setMax(6000)
                .setSaveConsumer(val -> config.poisonBladeDurationL3 = val)
                .build());

        // Lumberjack category
        ConfigCategory lumberjack = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.lumberjack"));

        lumberjack.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.lumberjack.enabled"),
                        config.lumberjackEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.lumberjackEnabled = val)
                .build());

        lumberjack.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.lumberjack.max_blocks_l1"),
                        config.lumberjackMaxBlocksL1)
                .setDefaultValue(16)
                .setMin(1).setMax(256)
                .setSaveConsumer(val -> config.lumberjackMaxBlocksL1 = val)
                .build());

        lumberjack.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.lumberjack.max_blocks_l2"),
                        config.lumberjackMaxBlocksL2)
                .setDefaultValue(48)
                .setMin(1).setMax(256)
                .setSaveConsumer(val -> config.lumberjackMaxBlocksL2 = val)
                .build());

        lumberjack.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.lumberjack.max_blocks_l3"),
                        config.lumberjackMaxBlocksL3)
                .setDefaultValue(128)
                .setMin(1).setMax(256)
                .setSaveConsumer(val -> config.lumberjackMaxBlocksL3 = val)
                .build());

        return builder.build();
    }
}
