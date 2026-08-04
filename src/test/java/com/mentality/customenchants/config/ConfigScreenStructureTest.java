package com.mentality.customenchants.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract-level evidence (source-text analysis, same approach as {@code ResourceContractTest})
 * for the "Simplify enchantment configuration UI" change: the per-enchantment "Enabled" toggle
 * moved out of each individual tab into one compact "Enchantment Availability" category, while
 * every backend {@code *Enabled} field, JSON schema and default stayed untouched.
 */
class ConfigScreenStructureTest {

    private static final Set<String> IDS = new LinkedHashSet<>(List.of(
            "glow_strike", "double_jump", "drill", "poison_blade", "lumberjack", "shadow_blade",
            "magnet", "auto_smelt", "vegetation", "rebound", "feedback", "second_wind",
            "guardians_grace", "vulnerability", "tether_master", "sky_rage", "xp_syphon",
            "kinetic_discharge", "sculk_bloom"));

    /** The 6 enchantments with no tunable parameters other than enable/disable. */
    private static final Set<String> NO_PARAMETER_IDS = Set.of(
            "double_jump", "drill", "auto_smelt", "tether_master", "xp_syphon");

    @Test
    void individualCategoriesDoNotAddAnEnabledToggle() throws IOException {
        String source = screenSource();
        String individualCategoriesBody = source.substring(
                source.indexOf("ConfigCategory glowStrike"), source.indexOf("private static"));
        assertFalse(individualCategoriesBody.contains(".enabled\""),
                "no individual enchantment category may reference an *.enabled translation key");
        assertFalse(individualCategoriesBody.contains("startBooleanToggle"),
                "no individual enchantment category may build its own boolean toggle");
    }

    @Test
    void availabilityCategoryHasExactlyNineteenToggles() throws IOException {
        String source = screenSource();
        String availabilityBody = source.substring(
                source.indexOf("ConfigCategory availability"), source.indexOf("ConfigCategory glowStrike"));
        Matcher m = Pattern.compile("availabilityToggle\\(entryBuilder, \"([a-z0-9_]+)\", config\\.([a-zA-Z0-9]+),").matcher(availabilityBody);
        Set<String> foundIds = new LinkedHashSet<>();
        while (m.find()) {
            foundIds.add(m.group(1));
        }
        assertEquals(19, foundIds.size(), "availability category must expose exactly 19 toggles");
        assertEquals(IDS, foundIds, "availability toggles must cover exactly the stable nineteen enchantment IDs");
    }

    @Test
    void everyAvailabilityToggleBindsItsOwnCorrectConfigField() throws IOException {
        String source = screenSource();
        String availabilityBody = source.substring(
                source.indexOf("ConfigCategory availability"), source.indexOf("ConfigCategory glowStrike"));
        Matcher m = Pattern.compile(
                "availabilityToggle\\(entryBuilder, \"([a-z0-9_]+)\", config\\.([a-zA-Z0-9]+),\\s*\\n\\s*val -> config\\.([a-zA-Z0-9]+) = val\\)")
                .matcher(availabilityBody);
        int matches = 0;
        while (m.find()) {
            matches++;
            String id = m.group(1);
            String readField = m.group(2);
            String writtenField = m.group(3);
            String expected = toEnabledFieldName(id);
            assertEquals(expected, readField, "read field must be the enchantment's own *Enabled field for " + id);
            assertEquals(expected, writtenField, "save consumer must write the same field it read for " + id);
        }
        assertEquals(19, matches, "every one of the 19 toggles must bind exactly its own config field");
    }

    @Test
    void noConfigEnabledFieldIsBoundMoreThanOnce() throws IOException {
        String source = screenSource();
        for (String id : IDS) {
            String field = toEnabledFieldName(id);
            int assignments = countOccurrences(source, "config." + field + " = val");
            assertEquals(1, assignments, field + " must be written by exactly one save consumer (no duplicate binding)");
        }
    }

    @Test
    void availabilityToggleHelperDefaultsToEnabledTrue() throws IOException {
        String source = screenSource();
        String helper = source.substring(source.indexOf(
                "static me.shedaniel.clothconfig2.api.AbstractConfigListEntry<Boolean> availabilityToggle"));
        String helperBody = helper.substring(0, helper.indexOf("noParametersEntry"));
        assertTrue(helperBody.contains(".setDefaultValue(true)"),
                "the shared availability-toggle helper must default every toggle to enabled=true (matches ModConfig defaults)");
    }

    @Test
    void exactlyFiveCategoriesShowTheNoParametersNotice() throws IOException {
        String source = screenSource();
        Matcher m = Pattern.compile("ConfigCategory ([a-zA-Z]+) = builder\\.getOrCreateCategory\\(\\s*Component\\.translatable\\(\"config\\.custom-enchants\\.category\\.([a-z0-9_]+)\"\\)\\);\\s*\\n\\s*\\n\\s*\\1\\.addEntry\\(noParametersEntry\\(entryBuilder\\)\\);")
                .matcher(source);
        Set<String> found = new LinkedHashSet<>();
        while (m.find()) {
            found.add(m.group(2));
        }
        assertEquals(NO_PARAMETER_IDS, found,
                "exactly the enchantments with no tunable parameters must show the neutral no-parameters notice");
    }

    @Test
    void nineteenCategoriesExistInTotal() throws IOException {
        String source = screenSource();
        int categories = countOccurrences(source, "builder.getOrCreateCategory(");
        assertEquals(19, categories, "18 enchantment tabs + 1 availability tab");
    }

    @Test
    void availabilityTranslationKeysExistInBothLanguagesWithParity() throws IOException {
        JsonObject en = readJson("src/main/resources/assets/custom-enchants/lang/en_us.json");
        JsonObject ru = readJson("src/main/resources/assets/custom-enchants/lang/ru_ru.json");
        for (String key : List.of(
                "config.custom-enchants.category.availability",
                "config.custom-enchants.availability.description",
                "config.custom-enchants.availability.toggle_tooltip",
                "config.custom-enchants.common.no_parameters")) {
            assertTrue(en.has(key), "missing EN key " + key);
            assertTrue(ru.has(key), "missing RU key " + key);
            assertFalse(en.get(key).getAsString().isBlank(), key + " EN value must not be blank");
            assertFalse(ru.get(key).getAsString().isBlank(), key + " RU value must not be blank");
        }
        // English "Enabled" bare word must not be reused verbatim as the availability toggle
        // description without qualification -- it must be the dedicated, disambiguated key.
        assertEquals("Enabled", en.get("config.custom-enchants.availability.toggle_tooltip").getAsString());
        assertEquals("Разрешено", ru.get("config.custom-enchants.availability.toggle_tooltip").getAsString());
    }

    private static String toEnabledFieldName(String id) {
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (char c : id.toCharArray()) {
            if (c == '_') {
                upperNext = true;
                continue;
            }
            sb.append(upperNext ? Character.toUpperCase(c) : c);
            upperNext = false;
        }
        sb.append("Enabled");
        return sb.toString();
    }

    private static int countOccurrences(String source, String needle) {
        int count = 0;
        int from = 0;
        while ((from = source.indexOf(needle, from)) >= 0) {
            count++;
            from += needle.length();
        }
        return count;
    }

    private static String screenSource() throws IOException {
        return read("src/client/java/com/mentality/customenchants/config/ModConfigScreen.java");
    }

    private static JsonObject readJson(String path) throws IOException {
        return JsonParser.parseString(read(path)).getAsJsonObject();
    }

    private static String read(String path) throws IOException {
        return Files.readString(root().resolve(path));
    }

    private static Path root() {
        return Path.of(System.getProperty("projectDir", ".")).toAbsolutePath().normalize();
    }
}
