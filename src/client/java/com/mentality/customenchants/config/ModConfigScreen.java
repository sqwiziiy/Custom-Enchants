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
                .setTitle(Component.translatable("config.custom-enchants.title.local"));

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

        // Shadow Blade category
        ConfigCategory shadowBlade = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.shadow_blade"));

        shadowBlade.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.shadow_blade.enabled"),
                        config.shadowBladeEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.shadowBladeEnabled = val)
                .build());

        shadowBlade.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.shadow_blade.chance_l1"),
                        config.shadowBladeChanceL1)
                .setDefaultValue(15)
                .setMin(1).setMax(100)
                .setSaveConsumer(val -> config.shadowBladeChanceL1 = val)
                .build());

        shadowBlade.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.shadow_blade.chance_l2"),
                        config.shadowBladeChanceL2)
                .setDefaultValue(25)
                .setMin(1).setMax(100)
                .setSaveConsumer(val -> config.shadowBladeChanceL2 = val)
                .build());

        shadowBlade.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.shadow_blade.chance_l3"),
                        config.shadowBladeChanceL3)
                .setDefaultValue(35)
                .setMin(1).setMax(100)
                .setSaveConsumer(val -> config.shadowBladeChanceL3 = val)
                .build());

        shadowBlade.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.shadow_blade.slow_duration_l1"),
                        config.shadowBladeSlowDurationL1)
                .setDefaultValue(20)
                .setMin(1).setMax(6000)
                .setSaveConsumer(val -> config.shadowBladeSlowDurationL1 = val)
                .build());

        shadowBlade.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.shadow_blade.slow_duration_l2"),
                        config.shadowBladeSlowDurationL2)
                .setDefaultValue(40)
                .setMin(1).setMax(6000)
                .setSaveConsumer(val -> config.shadowBladeSlowDurationL2 = val)
                .build());

        shadowBlade.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.shadow_blade.slow_duration_l3"),
                        config.shadowBladeSlowDurationL3)
                .setDefaultValue(60)
                .setMin(1).setMax(6000)
                .setSaveConsumer(val -> config.shadowBladeSlowDurationL3 = val)
                .build());

        // Magnet category
        ConfigCategory magnet = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.magnet"));

        magnet.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.magnet.enabled"),
                        config.magnetEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.magnetEnabled = val)
                .build());

        magnet.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.magnet.radius"),
                        config.magnetRadius)
                .setDefaultValue(5)
                .setMin(1).setMax(20)
                .setSaveConsumer(val -> config.magnetRadius = val)
                .build());

        // Auto Smelt category
        ConfigCategory autoSmelt = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.auto_smelt"));

        autoSmelt.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.auto_smelt.enabled"),
                        config.autoSmeltEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.autoSmeltEnabled = val)
                .build());

        // Vegetation category
        ConfigCategory vegetation = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.vegetation"));

        vegetation.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.vegetation.enabled"),
                        config.vegetationEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.vegetationEnabled = val)
                .build());

        vegetation.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.vegetation.chance_l1"),
                        config.vegetationChanceL1)
                .setDefaultValue(30)
                .setMin(0).setMax(100)
                .setSaveConsumer(val -> config.vegetationChanceL1 = val)
                .build());

        vegetation.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.vegetation.chance_l2"),
                        config.vegetationChanceL2)
                .setDefaultValue(60)
                .setMin(0).setMax(100)
                .setSaveConsumer(val -> config.vegetationChanceL2 = val)
                .build());

        vegetation.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.vegetation.chance_l3"),
                        config.vegetationChanceL3)
                .setDefaultValue(100)
                .setMin(0).setMax(100)
                .setSaveConsumer(val -> config.vegetationChanceL3 = val)
                .build());

        // Rebound category
        ConfigCategory rebound = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.rebound"));

        rebound.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.rebound.enabled"),
                        config.reboundEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.reboundEnabled = val)
                .build());

        rebound.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.rebound.knockback_l1"),
                        config.reboundKnockbackL1)
                .setDefaultValue(5)
                .setMin(1).setMax(50)
                .setSaveConsumer(val -> config.reboundKnockbackL1 = val)
                .build());

        rebound.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.rebound.knockback_l2"),
                        config.reboundKnockbackL2)
                .setDefaultValue(10)
                .setMin(1).setMax(50)
                .setSaveConsumer(val -> config.reboundKnockbackL2 = val)
                .build());

        rebound.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.rebound.knockback_l3"),
                        config.reboundKnockbackL3)
                .setDefaultValue(20)
                .setMin(1).setMax(50)
                .setSaveConsumer(val -> config.reboundKnockbackL3 = val)
                .build());

        // Feedback category
        ConfigCategory feedback = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.feedback"));

        feedback.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.feedback.enabled"),
                        config.feedbackEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.feedbackEnabled = val)
                .build());

        feedback.addEntry(entryBuilder.startFloatField(
                        Component.translatable("config.custom-enchants.feedback.heal_amount"),
                        config.feedbackHealAmount)
                .setDefaultValue(2.0f)
                .setMin(0.0f).setMax(20.0f)
                .setSaveConsumer(val -> config.feedbackHealAmount = val)
                .build());

        feedback.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.feedback.repair_amount"),
                        config.feedbackRepairAmount)
                .setDefaultValue(2)
                .setMin(1).setMax(50)
                .setSaveConsumer(val -> config.feedbackRepairAmount = val)
                .build());

        // Second Wind category
        ConfigCategory secondWind = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.second_wind"));

        secondWind.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.second_wind.enabled"),
                        config.secondWindEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.secondWindEnabled = val)
                .build());

        secondWind.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.second_wind.speed_duration"),
                        config.secondWindSpeedDuration)
                .setDefaultValue(5)
                .setMin(1).setMax(60)
                .setSaveConsumer(val -> config.secondWindSpeedDuration = val)
                .build());

        secondWind.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.second_wind.cooldown"),
                        config.secondWindCooldown)
                .setDefaultValue(60)
                .setMin(10).setMax(600)
                .setSaveConsumer(val -> config.secondWindCooldown = val)
                .build());

        // Guardian's Grace category
        ConfigCategory guardiansGrace = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.guardians_grace"));

        guardiansGrace.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.guardians_grace.enabled"),
                        config.guardiansGraceEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.guardiansGraceEnabled = val)
                .build());

        guardiansGrace.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.guardians_grace.chance_l1"),
                        config.guardiansGraceChanceL1)
                .setDefaultValue(10)
                .setMin(0).setMax(100)
                .setSaveConsumer(val -> config.guardiansGraceChanceL1 = val)
                .build());

        guardiansGrace.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.guardians_grace.chance_l2"),
                        config.guardiansGraceChanceL2)
                .setDefaultValue(20)
                .setMin(0).setMax(100)
                .setSaveConsumer(val -> config.guardiansGraceChanceL2 = val)
                .build());

        guardiansGrace.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.guardians_grace.chance_l3"),
                        config.guardiansGraceChanceL3)
                .setDefaultValue(30)
                .setMin(0).setMax(100)
                .setSaveConsumer(val -> config.guardiansGraceChanceL3 = val)
                .build());

        // Vulnerability (Аналитик) category
        ConfigCategory vulnerability = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.vulnerability"));

        vulnerability.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.vulnerability.enabled"),
                        config.vulnerabilityEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.vulnerabilityEnabled = val)
                .build());

        vulnerability.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.vulnerability.ignore_l1"),
                        config.vulnerabilityIgnoreL1)
                .setDefaultValue(10)
                .setMin(0).setMax(100)
                .setSaveConsumer(val -> config.vulnerabilityIgnoreL1 = val)
                .build());

        vulnerability.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.vulnerability.ignore_l2"),
                        config.vulnerabilityIgnoreL2)
                .setDefaultValue(20)
                .setMin(0).setMax(100)
                .setSaveConsumer(val -> config.vulnerabilityIgnoreL2 = val)
                .build());

        vulnerability.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.vulnerability.ignore_l3"),
                        config.vulnerabilityIgnoreL3)
                .setDefaultValue(30)
                .setMin(0).setMax(100)
                .setSaveConsumer(val -> config.vulnerabilityIgnoreL3 = val)
                .build());

        // Tether Master (Крепкая нить) category
        ConfigCategory tetherMaster = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.tether_master"));

        tetherMaster.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.tether_master.enabled"),
                        config.tetherMasterEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.tetherMasterEnabled = val)
                .build());

        // Sky Rage (Ярость Неба) category
        ConfigCategory skyRage = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.sky_rage"));

        skyRage.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.sky_rage.enabled"),
                        config.skyRageEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.skyRageEnabled = val)
                .build());

        skyRage.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.custom-enchants.sky_rage.cooldown_ticks"),
                        config.skyRageCooldownTicks)
                .setDefaultValue(0)
                .setMin(0).setMax(600)
                .setSaveConsumer(val -> config.skyRageCooldownTicks = val)
                .build());

        // XP Syphon (Собиратель искр) category
        ConfigCategory xpSyphon = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.xp_syphon"));

        xpSyphon.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.xp_syphon.enabled"),
                        config.xpSyphonEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.xpSyphonEnabled = val)
                .build());

        // Kinetic Discharge (Кинетический разряд) category
        ConfigCategory kineticDischarge = builder.getOrCreateCategory(
                Component.translatable("config.custom-enchants.category.kinetic_discharge"));

        kineticDischarge.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.custom-enchants.kinetic_discharge.enabled"),
                        config.kineticDischargeEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.kineticDischargeEnabled = val)
                .build());

        kineticDischarge.addEntry(entryBuilder.startFloatField(
                        Component.translatable("config.custom-enchants.kinetic_discharge.min_speed"),
                        config.kineticDischargeMinSpeed)
                .setDefaultValue(1.2f)
                .setMin(0.1f).setMax(8.0f)
                .setSaveConsumer(val -> config.kineticDischargeMinSpeed = val)
                .build());

        kineticDischarge.addEntry(entryBuilder.startFloatField(
                        Component.translatable("config.custom-enchants.kinetic_discharge.knockback_l1"),
                        config.kineticDischargeKnockbackL1)
                .setDefaultValue(1.5f)
                .setMin(0.1f).setMax(10.0f)
                .setSaveConsumer(val -> config.kineticDischargeKnockbackL1 = val)
                .build());

        kineticDischarge.addEntry(entryBuilder.startFloatField(
                        Component.translatable("config.custom-enchants.kinetic_discharge.knockback_l2"),
                        config.kineticDischargeKnockbackL2)
                .setDefaultValue(2.5f)
                .setMin(0.1f).setMax(10.0f)
                .setSaveConsumer(val -> config.kineticDischargeKnockbackL2 = val)
                .build());

        kineticDischarge.addEntry(entryBuilder.startFloatField(
                        Component.translatable("config.custom-enchants.kinetic_discharge.knockback_l3"),
                        config.kineticDischargeKnockbackL3)
                .setDefaultValue(3.5f)
                .setMin(0.1f).setMax(10.0f)
                .setSaveConsumer(val -> config.kineticDischargeKnockbackL3 = val)
                .build());

        kineticDischarge.addEntry(entryBuilder.startFloatField(
                        Component.translatable("config.custom-enchants.kinetic_discharge.damage_l3"),
                        config.kineticDischargeDamageL3)
                .setDefaultValue(2.0f)
                .setMin(0.0f).setMax(20.0f)
                .setSaveConsumer(val -> config.kineticDischargeDamageL3 = val)
                .build());

        return builder.build();
    }
}
