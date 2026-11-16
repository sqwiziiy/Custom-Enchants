package com.mentality.customenchants.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mentality.customenchants.CustomEnchantsMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

public class ModConfig {

    public static final int CURRENT_CONFIG_VERSION = 1;
    private static final int MAX_DURATION_TICKS = 6_000;
    private static final int MAX_COOLDOWN_SECONDS = 600;
    private static final int MAX_MAGNET_RADIUS = 20;
    private static final int MAX_BLOCK_LIMIT = 256;
    private static final DateTimeFormatter BACKUP_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final Gson GSON = new GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .setPrettyPrinting()
            .create();

    private static ModConfig INSTANCE = new ModConfig();

    public int configVersion = CURRENT_CONFIG_VERSION;

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
    public int skyRageCooldownTicks = 0;

    // XP Syphon (Собиратель искр) settings
    public boolean xpSyphonEnabled = true;

    // Kinetic Discharge (Кинетический разряд) settings
    public boolean kineticDischargeEnabled = true;
    public float kineticDischargeMinSpeed = 1.2f;
    public float kineticDischargeKnockbackL1 = 1.5f;
    public float kineticDischargeKnockbackL2 = 2.5f;
    public float kineticDischargeKnockbackL3 = 3.5f;
    public float kineticDischargeDamageL3 = 2.0f;

    // Sculk Bloom (Скалковое цветение) settings
    public boolean sculkBloomEnabled = true;

    public static ModConfig get() {
        return INSTANCE;
    }

    public static void load() {
        load(FabricLoader.getInstance().getConfigDir().resolve("custom-enchants.json"));
    }

    static void load(Path configPath) {
        if (!Files.exists(configPath)) {
            INSTANCE = new ModConfig();
            if (save(configPath)) {
                CustomEnchantsMod.LOGGER.info("Config created with defaults at {}", configPath);
            }
            return;
        }

        final String json;
        try {
            json = Files.readString(configPath);
        } catch (IOException exception) {
            INSTANCE = new ModConfig();
            CustomEnchantsMod.LOGGER.error("Config read failed; using defaults for {}", configPath, exception);
            return;
        }

        try {
            JsonElement parsed = JsonParser.parseString(json);
            if (!parsed.isJsonObject()) {
                throw new IllegalArgumentException("config root must be a JSON object");
            }

            JsonObject merged = GSON.toJsonTree(new ModConfig()).getAsJsonObject();
            parsed.getAsJsonObject().entrySet().forEach(entry -> merged.add(entry.getKey(), entry.getValue()));
            ModConfig loaded = GSON.fromJson(merged, ModConfig.class);
            boolean migrated = !parsed.getAsJsonObject().has("configVersion") || loaded.configVersion < CURRENT_CONFIG_VERSION;
            if (loaded.configVersion <= 0) {
                loaded.configVersion = CURRENT_CONFIG_VERSION;
            }
            String beforeNormalization = GSON.toJson(loaded);
            boolean normalized = ConfigValidator.normalize(loaded);
            INSTANCE = loaded;

            if (migrated) {
                CustomEnchantsMod.LOGGER.info("Config migrated to schema version {}", CURRENT_CONFIG_VERSION);
            }
            if (normalized) {
                CustomEnchantsMod.LOGGER.info("Config normalized fields: {}", changedFields(beforeNormalization, loaded));
            }
            if (migrated || normalized) {
                save(configPath);
            } else {
                CustomEnchantsMod.LOGGER.info("Config loaded from {}", configPath);
            }
        } catch (RuntimeException exception) {
            INSTANCE = new ModConfig();
            handleBrokenConfig(configPath, exception);
        }
    }

    private static String changedFields(String beforeJson, ModConfig after) {
        JsonObject before = JsonParser.parseString(beforeJson).getAsJsonObject();
        JsonObject current = GSON.toJsonTree(after).getAsJsonObject();
        return current.entrySet().stream()
                .filter(entry -> !entry.getValue().equals(before.get(entry.getKey())))
                .map(java.util.Map.Entry::getKey)
                .sorted()
                .reduce((left, right) -> left + ", " + right)
                .orElse("unknown");
    }

    public static void save() {
        save(FabricLoader.getInstance().getConfigDir().resolve("custom-enchants.json"));
    }

    static boolean save(Path configPath) {
        Path absolutePath = configPath.toAbsolutePath();
        Path parent = absolutePath.getParent();
        if (parent == null) {
            CustomEnchantsMod.LOGGER.error("Config save failed: no parent directory for {}", configPath);
            return false;
        }

        Path temporaryPath = parent.resolve(absolutePath.getFileName() + ".tmp-" + UUID.randomUUID());
        try {
            Files.createDirectories(parent);
            byte[] bytes = GSON.toJson(INSTANCE).getBytes(StandardCharsets.UTF_8);
            try (FileChannel channel = FileChannel.open(temporaryPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                channel.force(true);
            }

            try {
                Files.move(temporaryPath, absolutePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                CustomEnchantsMod.LOGGER.warn("Atomic config move unavailable; using replace fallback for {}", configPath);
                Files.move(temporaryPath, absolutePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException | RuntimeException exception) {
            CustomEnchantsMod.LOGGER.error("Config save failed for {}", configPath, exception);
            return false;
        } finally {
            try {
                Files.deleteIfExists(temporaryPath);
            } catch (IOException exception) {
                CustomEnchantsMod.LOGGER.warn("Could not remove temporary config file {}", temporaryPath);
            }
        }
    }

    private static void handleBrokenConfig(Path configPath, RuntimeException cause) {
        try {
            Path backup = backupBrokenConfig(configPath);
            CustomEnchantsMod.LOGGER.warn("Broken config backed up to {}; using defaults", backup);
            save(configPath);
        } catch (IOException backupFailure) {
            CustomEnchantsMod.LOGGER.error("Broken config could not be backed up; keeping original and using defaults", configPath, backupFailure);
        }
        CustomEnchantsMod.LOGGER.error("Config is invalid; using defaults", cause);
    }

    private static Path backupBrokenConfig(Path configPath) throws IOException {
        byte[] original = Files.readAllBytes(configPath);
        String baseName = configPath.getFileName() + ".broken-" + LocalDateTime.now().format(BACKUP_TIME);
        Path candidate = configPath.resolveSibling(baseName);
        int suffix = 1;
        while (Files.exists(candidate)) {
            if (Arrays.equals(original, Files.readAllBytes(candidate))) {
                return candidate;
            }
            candidate = configPath.resolveSibling(baseName + "." + suffix++);
        }
        Files.copy(configPath, candidate, StandardCopyOption.COPY_ATTRIBUTES);
        return candidate;
    }

    static void resetForTests() {
        INSTANCE = new ModConfig();
    }

    static final class ConfigValidator {
        private ConfigValidator() {
        }

        static boolean normalize(ModConfig config) {
            boolean changed = false;
            changed |= config.configVersion <= 0;
            config.configVersion = CURRENT_CONFIG_VERSION;

            changed |= set(config.glowStrikeDurationL1, clamp(config.glowStrikeDurationL1, 0, MAX_DURATION_TICKS), value -> config.glowStrikeDurationL1 = value);
            changed |= set(config.glowStrikeDurationL2, clamp(config.glowStrikeDurationL2, 0, MAX_DURATION_TICKS), value -> config.glowStrikeDurationL2 = value);
            changed |= set(config.glowStrikeDurationL3, clamp(config.glowStrikeDurationL3, 0, MAX_DURATION_TICKS), value -> config.glowStrikeDurationL3 = value);
            changed |= nonDecreasing(() -> new int[]{config.glowStrikeDurationL1, config.glowStrikeDurationL2, config.glowStrikeDurationL3}, values -> {
                config.glowStrikeDurationL1 = values[0]; config.glowStrikeDurationL2 = values[1]; config.glowStrikeDurationL3 = values[2];
            });

            changed |= set(config.poisonBladeDurationL1, clamp(config.poisonBladeDurationL1, 0, MAX_DURATION_TICKS), value -> config.poisonBladeDurationL1 = value);
            changed |= set(config.poisonBladeDurationL2, clamp(config.poisonBladeDurationL2, 0, MAX_DURATION_TICKS), value -> config.poisonBladeDurationL2 = value);
            changed |= set(config.poisonBladeDurationL3, clamp(config.poisonBladeDurationL3, 0, MAX_DURATION_TICKS), value -> config.poisonBladeDurationL3 = value);
            changed |= nonDecreasing(() -> new int[]{config.poisonBladeDurationL1, config.poisonBladeDurationL2, config.poisonBladeDurationL3}, values -> {
                config.poisonBladeDurationL1 = values[0]; config.poisonBladeDurationL2 = values[1]; config.poisonBladeDurationL3 = values[2];
            });

            changed |= set(config.lumberjackMaxBlocksL1, clamp(config.lumberjackMaxBlocksL1, 1, MAX_BLOCK_LIMIT), value -> config.lumberjackMaxBlocksL1 = value);
            changed |= set(config.lumberjackMaxBlocksL2, clamp(config.lumberjackMaxBlocksL2, 1, MAX_BLOCK_LIMIT), value -> config.lumberjackMaxBlocksL2 = value);
            changed |= set(config.lumberjackMaxBlocksL3, clamp(config.lumberjackMaxBlocksL3, 1, MAX_BLOCK_LIMIT), value -> config.lumberjackMaxBlocksL3 = value);
            changed |= nonDecreasing(() -> new int[]{config.lumberjackMaxBlocksL1, config.lumberjackMaxBlocksL2, config.lumberjackMaxBlocksL3}, values -> {
                config.lumberjackMaxBlocksL1 = values[0]; config.lumberjackMaxBlocksL2 = values[1]; config.lumberjackMaxBlocksL3 = values[2];
            });

            changed |= set(config.shadowBladeChanceL1, clamp(config.shadowBladeChanceL1, 0, 100), value -> config.shadowBladeChanceL1 = value);
            changed |= set(config.shadowBladeChanceL2, clamp(config.shadowBladeChanceL2, 0, 100), value -> config.shadowBladeChanceL2 = value);
            changed |= set(config.shadowBladeChanceL3, clamp(config.shadowBladeChanceL3, 0, 100), value -> config.shadowBladeChanceL3 = value);
            changed |= nonDecreasing(() -> new int[]{config.shadowBladeChanceL1, config.shadowBladeChanceL2, config.shadowBladeChanceL3}, values -> {
                config.shadowBladeChanceL1 = values[0]; config.shadowBladeChanceL2 = values[1]; config.shadowBladeChanceL3 = values[2];
            });
            changed |= set(config.shadowBladeSlowDurationL1, clamp(config.shadowBladeSlowDurationL1, 0, MAX_DURATION_TICKS), value -> config.shadowBladeSlowDurationL1 = value);
            changed |= set(config.shadowBladeSlowDurationL2, clamp(config.shadowBladeSlowDurationL2, 0, MAX_DURATION_TICKS), value -> config.shadowBladeSlowDurationL2 = value);
            changed |= set(config.shadowBladeSlowDurationL3, clamp(config.shadowBladeSlowDurationL3, 0, MAX_DURATION_TICKS), value -> config.shadowBladeSlowDurationL3 = value);
            changed |= nonDecreasing(() -> new int[]{config.shadowBladeSlowDurationL1, config.shadowBladeSlowDurationL2, config.shadowBladeSlowDurationL3}, values -> {
                config.shadowBladeSlowDurationL1 = values[0]; config.shadowBladeSlowDurationL2 = values[1]; config.shadowBladeSlowDurationL3 = values[2];
            });

            changed |= set(config.magnetRadius, clamp(config.magnetRadius, 1, MAX_MAGNET_RADIUS), value -> config.magnetRadius = value);
            changed |= set(config.vegetationChanceL1, clamp(config.vegetationChanceL1, 0, 100), value -> config.vegetationChanceL1 = value);
            changed |= set(config.vegetationChanceL2, clamp(config.vegetationChanceL2, 0, 100), value -> config.vegetationChanceL2 = value);
            changed |= set(config.vegetationChanceL3, clamp(config.vegetationChanceL3, 0, 100), value -> config.vegetationChanceL3 = value);
            changed |= nonDecreasing(() -> new int[]{config.vegetationChanceL1, config.vegetationChanceL2, config.vegetationChanceL3}, values -> {
                config.vegetationChanceL1 = values[0]; config.vegetationChanceL2 = values[1]; config.vegetationChanceL3 = values[2];
            });

            changed |= set(config.reboundKnockbackL1, clamp(config.reboundKnockbackL1, 0, 50), value -> config.reboundKnockbackL1 = value);
            changed |= set(config.reboundKnockbackL2, clamp(config.reboundKnockbackL2, 0, 50), value -> config.reboundKnockbackL2 = value);
            changed |= set(config.reboundKnockbackL3, clamp(config.reboundKnockbackL3, 0, 50), value -> config.reboundKnockbackL3 = value);
            changed |= nonDecreasing(() -> new int[]{config.reboundKnockbackL1, config.reboundKnockbackL2, config.reboundKnockbackL3}, values -> {
                config.reboundKnockbackL1 = values[0]; config.reboundKnockbackL2 = values[1]; config.reboundKnockbackL3 = values[2];
            });

            changed |= setFloat(config.feedbackHealAmount, clamp(config.feedbackHealAmount, 0, 20), value -> config.feedbackHealAmount = value);
            changed |= set(config.feedbackRepairAmount, clamp(config.feedbackRepairAmount, 0, 50), value -> config.feedbackRepairAmount = value);
            changed |= set(config.secondWindSpeedDuration, clamp(config.secondWindSpeedDuration, 1, 60), value -> config.secondWindSpeedDuration = value);
            changed |= set(config.secondWindCooldown, clamp(config.secondWindCooldown, 0, MAX_COOLDOWN_SECONDS), value -> config.secondWindCooldown = value);

            changed |= set(config.guardiansGraceChanceL1, clamp(config.guardiansGraceChanceL1, 0, 100), value -> config.guardiansGraceChanceL1 = value);
            changed |= set(config.guardiansGraceChanceL2, clamp(config.guardiansGraceChanceL2, 0, 100), value -> config.guardiansGraceChanceL2 = value);
            changed |= set(config.guardiansGraceChanceL3, clamp(config.guardiansGraceChanceL3, 0, 100), value -> config.guardiansGraceChanceL3 = value);
            changed |= nonDecreasing(() -> new int[]{config.guardiansGraceChanceL1, config.guardiansGraceChanceL2, config.guardiansGraceChanceL3}, values -> {
                config.guardiansGraceChanceL1 = values[0]; config.guardiansGraceChanceL2 = values[1]; config.guardiansGraceChanceL3 = values[2];
            });

            changed |= set(config.vulnerabilityIgnoreL1, clamp(config.vulnerabilityIgnoreL1, 0, 100), value -> config.vulnerabilityIgnoreL1 = value);
            changed |= set(config.vulnerabilityIgnoreL2, clamp(config.vulnerabilityIgnoreL2, 0, 100), value -> config.vulnerabilityIgnoreL2 = value);
            changed |= set(config.vulnerabilityIgnoreL3, clamp(config.vulnerabilityIgnoreL3, 0, 100), value -> config.vulnerabilityIgnoreL3 = value);
            changed |= nonDecreasing(() -> new int[]{config.vulnerabilityIgnoreL1, config.vulnerabilityIgnoreL2, config.vulnerabilityIgnoreL3}, values -> {
                config.vulnerabilityIgnoreL1 = values[0]; config.vulnerabilityIgnoreL2 = values[1]; config.vulnerabilityIgnoreL3 = values[2];
            });

            changed |= set(config.skyRageCooldownTicks, clamp(config.skyRageCooldownTicks, 0, MAX_DURATION_TICKS), value -> config.skyRageCooldownTicks = value);
            changed |= setFloat(config.kineticDischargeMinSpeed, clamp(config.kineticDischargeMinSpeed, 0.1f, 8.0f), value -> config.kineticDischargeMinSpeed = value);
            changed |= setFloat(config.kineticDischargeKnockbackL1, clamp(config.kineticDischargeKnockbackL1, 0, 10), value -> config.kineticDischargeKnockbackL1 = value);
            changed |= setFloat(config.kineticDischargeKnockbackL2, clamp(config.kineticDischargeKnockbackL2, 0, 10), value -> config.kineticDischargeKnockbackL2 = value);
            changed |= setFloat(config.kineticDischargeKnockbackL3, clamp(config.kineticDischargeKnockbackL3, 0, 10), value -> config.kineticDischargeKnockbackL3 = value);
            changed |= nonDecreasingFloat(() -> new float[]{config.kineticDischargeKnockbackL1, config.kineticDischargeKnockbackL2, config.kineticDischargeKnockbackL3}, values -> {
                config.kineticDischargeKnockbackL1 = values[0]; config.kineticDischargeKnockbackL2 = values[1]; config.kineticDischargeKnockbackL3 = values[2];
            });
            changed |= setFloat(config.kineticDischargeDamageL3, clamp(config.kineticDischargeDamageL3, 0, 20), value -> config.kineticDischargeDamageL3 = value);
            return changed;
        }

        private static int clamp(int value, int min, int max) {
            return Math.max(min, Math.min(max, value));
        }

        private static float clamp(float value, float min, float max) {
            if (!Float.isFinite(value)) return min;
            return Math.max(min, Math.min(max, value));
        }

        private static boolean set(int before, int after, IntSetter setter) {
            if (before == after) return false;
            setter.set(after);
            return true;
        }

        private static boolean setFloat(float before, float after, FloatSetter setter) {
            if (Float.compare(before, after) == 0) return false;
            setter.set(after);
            return true;
        }

        private static boolean nonDecreasing(IntArrayGetter getter, IntArraySetter setter) {
            int[] values = getter.get();
            int[] original = values.clone();
            for (int i = 1; i < values.length; i++) values[i] = Math.max(values[i], values[i - 1]);
            setter.set(values);
            return !Arrays.equals(original, values);
        }

        private static boolean nonDecreasingFloat(FloatArrayGetter getter, FloatArraySetter setter) {
            float[] values = getter.get();
            float[] original = values.clone();
            for (int i = 1; i < values.length; i++) values[i] = Math.max(values[i], values[i - 1]);
            setter.set(values);
            return !Arrays.equals(original, values);
        }

        @FunctionalInterface private interface IntSetter { void set(int value); }
        @FunctionalInterface private interface FloatSetter { void set(float value); }
        @FunctionalInterface private interface IntArrayGetter { int[] get(); }
        @FunctionalInterface private interface IntArraySetter { void set(int[] values); }
        @FunctionalInterface private interface FloatArrayGetter { float[] get(); }
        @FunctionalInterface private interface FloatArraySetter { void set(float[] values); }
    }
}
