package com.mentality.customenchants.config;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModConfigTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void resetBefore() {
        ModConfig.resetForTests();
    }

    @AfterEach
    void resetAfter() {
        ModConfig.resetForTests();
    }

    @Test
    void missingFileCreatesDefaults() throws Exception {
        Path path = tempDir.resolve("custom-enchants.json");
        ModConfig.load(path);

        assertNotNull(ModConfig.get());
        assertEquals(5, ModConfig.get().magnetRadius);
        assertTrue(Files.exists(path));
    }

    @Test
    void fullJsonLoadsAndUnknownFieldIsIgnored() throws Exception {
        Path path = write("{\"configVersion\":1,\"magnetRadius\":7,\"unknownField\":123}");
        ModConfig.load(path);

        assertEquals(7, ModConfig.get().magnetRadius);
        assertNotNull(JsonParser.parseString(Files.readString(path)));
    }

    @Test
    void partialJsonMergesDefaults() throws Exception {
        Path path = write("{\"magnetRadius\":3}");
        ModConfig.load(path);

        assertEquals(3, ModConfig.get().magnetRadius);
        assertEquals(40, ModConfig.get().glowStrikeDurationL1);
        assertTrue(ModConfig.get().feedbackEnabled);
    }

    @Test
    void emptyMalformedNullArrayStringAndWrongTypeUseDefaultsAndBackup() throws Exception {
        String[] invalid = {"", "{", "null", "[]", "\"text\"", "{\"magnetRadius\":\"bad\"}"};
        for (int i = 0; i < invalid.length; i++) {
            Path path = tempDir.resolve("invalid-" + i + ".json");
            Files.writeString(path, invalid[i]);
            ModConfig.load(path);
            assertNotNull(ModConfig.get());
            assertEquals(5, ModConfig.get().magnetRadius);
            assertTrue(findBackups(path).size() == 1);
        }
    }

    @Test
    void duplicateBrokenBytesDoNotCreateBackupChain() throws Exception {
        Path path = write("{broken");
        ModConfig.load(path);
        Files.writeString(path, "{broken");
        ModConfig.load(path);

        assertEquals(1, findBackups(path).size());
    }

    @Test
    void normalizationClampsDangerousValuesAndPreservesSafeValues() throws Exception {
        Path path = write("{" +
                "\"magnetRadius\":999," +
                "\"shadowBladeChanceL1\":-5," +
                "\"shadowBladeChanceL2\":101," +
                "\"secondWindCooldown\":-1," +
                "\"glowStrikeDurationL1\":9000," +
                "\"lumberjackMaxBlocksL1\":0," +
                "\"feedbackHealAmount\":\"NaN\"," +
                "\"kineticDischargeMinSpeed\":999," +
                "\"vegetationChanceL1\":50," +
                "\"vegetationChanceL2\":40," +
                "\"vegetationChanceL3\":30," +
                "\"secondWindSpeedDuration\":12}");
        ModConfig.load(path);

        assertEquals(20, ModConfig.get().magnetRadius);
        assertEquals(0, ModConfig.get().shadowBladeChanceL1);
        assertEquals(100, ModConfig.get().shadowBladeChanceL2);
        assertEquals(0, ModConfig.get().secondWindCooldown);
        assertEquals(6000, ModConfig.get().glowStrikeDurationL1);
        assertEquals(1, ModConfig.get().lumberjackMaxBlocksL1);
        assertEquals(0.0f, ModConfig.get().feedbackHealAmount);
        assertEquals(8.0f, ModConfig.get().kineticDischargeMinSpeed);
        assertEquals(50, ModConfig.get().vegetationChanceL1);
        assertEquals(50, ModConfig.get().vegetationChanceL2);
        assertEquals(50, ModConfig.get().vegetationChanceL3);
        assertEquals(12, ModConfig.get().secondWindSpeedDuration);
    }

    @Test
    void saveCreatesValidJsonReplacesTargetAndLeavesNoTemp() throws Exception {
        Path path = tempDir.resolve("custom-enchants.json");
        ModConfig.get().magnetRadius = 9;
        assertTrue(ModConfig.save(path));
        assertEquals(9, JsonParser.parseString(Files.readString(path)).getAsJsonObject().get("magnetRadius").getAsInt());

        ModConfig.get().magnetRadius = 11;
        assertTrue(ModConfig.save(path));
        assertEquals(11, JsonParser.parseString(Files.readString(path)).getAsJsonObject().get("magnetRadius").getAsInt());
        assertTrue(findTempFiles(path).isEmpty());
    }

    @Test
    void brokenBackupPreservesOriginalBytes() throws Exception {
        Path path = write("{\n  \"magnetRadius\": \"not-a-number\"\n}");
        byte[] original = Files.readAllBytes(path);
        ModConfig.load(path);

        assertEquals(1, findBackups(path).size());
        assertTrue(java.util.Arrays.equals(original, Files.readAllBytes(findBackups(path).get(0))));
    }

    @Test
    void legacyVersionMigratesAndKeepsCustomValue() throws Exception {
        Path path = write("{\"magnetRadius\":8}");
        ModConfig.load(path);

        assertEquals(1, ModConfig.get().configVersion);
        assertEquals(8, ModConfig.get().magnetRadius);
        assertEquals(1, JsonParser.parseString(Files.readString(path)).getAsJsonObject().get("configVersion").getAsInt());
    }

    @Test
    void deadSettingsHaveRuntimeFormulas() {
        assertEquals(40, com.mentality.customenchants.enchantment.SecondWindHandler.calculateSpeedTicks(5, 1));
        assertEquals(60, com.mentality.customenchants.enchantment.SecondWindHandler.calculateSpeedTicks(5, 2));
        assertEquals(60, com.mentality.customenchants.enchantment.SecondWindHandler.calculateSpeedTicks(5, 3));
        assertEquals(80, com.mentality.customenchants.enchantment.SecondWindHandler.calculateSpeedTicks(5, 4));
        assertEquals(144, com.mentality.customenchants.enchantment.SecondWindHandler.calculateSpeedTicks(12, 2));
    }

    private Path write(String content) throws Exception {
        Path path = tempDir.resolve("custom-enchants-" + System.nanoTime() + ".json");
        Files.writeString(path, content, StandardCharsets.UTF_8);
        return path;
    }

    private List<Path> findBackups(Path path) throws Exception {
        try (var paths = Files.list(path.getParent())) {
            return paths.filter(candidate -> candidate.getFileName().toString().startsWith(path.getFileName() + ".broken-"))
                    .sorted().collect(Collectors.toList());
        }
    }

    private List<Path> findTempFiles(Path path) throws Exception {
        try (var paths = Files.list(path.getParent())) {
            return paths.filter(candidate -> candidate.getFileName().toString().startsWith(path.getFileName() + ".tmp-"))
                    .collect(Collectors.toList());
        }
    }
}
