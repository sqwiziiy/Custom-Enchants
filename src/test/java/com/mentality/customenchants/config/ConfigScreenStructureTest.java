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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract-level evidence (source-text analysis, same approach as {@code ResourceContractTest})
 * for the config screen structure: the per-enchantment "Enabled" toggle lives only in one compact
 * "Enchantment Availability" category; individual enchantment tabs exist only for enchantments
 * with real tunable parameters (empty tabs were removed entirely, not padded with a placeholder
 * notice); every backend {@code *Enabled} field, JSON schema and default stayed untouched.
 */
class ConfigScreenStructureTest {

    /** All 19 stable enchantment IDs — every one gets an availability toggle. */
    private static final Set<String> ALL_IDS = new LinkedHashSet<>(List.of(
            "glow_strike", "double_jump", "drill", "poison_blade", "lumberjack", "shadow_blade",
            "magnet", "auto_smelt", "vegetation", "rebound", "feedback", "second_wind",
            "guardians_grace", "vulnerability", "tether_master", "sky_rage", "xp_syphon",
            "kinetic_discharge", "sculk_bloom"));

    /** The 13 enchantments with real tunable parameters — the only ones with an individual tab. */
    private static final Set<String> INDIVIDUAL_CATEGORY_IDS = new LinkedHashSet<>(List.of(
            "glow_strike", "poison_blade", "lumberjack", "shadow_blade", "magnet", "vegetation",
            "rebound", "feedback", "second_wind", "guardians_grace", "vulnerability", "sky_rage",
            "kinetic_discharge"));

    /**
     * The 6 enchantments with no tunable parameter beyond enable/disable — none of these may have
     * an individual tab; they are managed only through the availability category.
     */
    private static final Set<String> NO_PARAMETER_IDS = Set.of(
            "double_jump", "drill", "auto_smelt", "tether_master", "xp_syphon", "sculk_bloom");

    @Test
    void nineteenTotalIdsSplitIntoThirteenIndividualPlusSixParameterless() {
        assertEquals(19, ALL_IDS.size());
        assertEquals(13, INDIVIDUAL_CATEGORY_IDS.size());
        assertEquals(6, NO_PARAMETER_IDS.size());
        Set<String> union = new LinkedHashSet<>(INDIVIDUAL_CATEGORY_IDS);
        union.addAll(NO_PARAMETER_IDS);
        assertEquals(ALL_IDS, union, "every enchantment is exactly one of: has an individual tab, or availability-only");
    }

    @Test
    void fourteenCategoriesExistInTotal() throws IOException {
        String source = screenSource();
        int categories = countOccurrences(source, "builder.getOrCreateCategory(");
        assertEquals(14, categories, "13 enchantment tabs with real parameters + 1 availability tab");
    }

    @Test
    void individualCategorySetIsExactlyTheThirteenWithParameters() throws IOException {
        String source = screenSource();
        String body = source.substring(source.indexOf("ConfigCategory glowStrike"), source.indexOf("private static"));
        Matcher m = Pattern.compile(
                "ConfigCategory [a-zA-Z]+ = builder\\.getOrCreateCategory\\(\\s*Component\\.translatable\\(\"config\\.custom-enchants\\.category\\.([a-z0-9_]+)\"\\)\\);")
                .matcher(body);
        Set<String> found = new LinkedHashSet<>();
        while (m.find()) {
            found.add(m.group(1));
        }
        assertEquals(INDIVIDUAL_CATEGORY_IDS, found, "individual tabs must exist for exactly the 13 enchantments with real parameters");
    }

    @Test
    void removedEnchantmentsHaveNoIndividualCategory() throws IOException {
        String source = screenSource();
        for (String id : NO_PARAMETER_IDS) {
            assertFalse(source.contains("category." + id + "\""),
                    id + " must not have its own category (it is availability-only)");
        }
    }

    @Test
    void noParametersEntryHelperIsGone() throws IOException {
        String source = screenSource();
        assertFalse(source.contains("noParametersEntry"),
                "the placeholder 'no configurable parameters' entry must be removed, not just unused");
    }

    @Test
    void individualCategoriesDoNotContainBooleanToggles() throws IOException {
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
        assertEquals(ALL_IDS, foundIds, "availability toggles must cover exactly the stable nineteen enchantment IDs");
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
        for (String id : ALL_IDS) {
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
        assertTrue(helper.contains(".setDefaultValue(true)"),
                "the shared availability-toggle helper must default every toggle to enabled=true (matches ModConfig defaults)");
    }

    @Test
    void availabilityTranslationKeysExistInBothLanguagesWithParity() throws IOException {
        JsonObject en = readJson("src/main/resources/assets/custom-enchants/lang/en_us.json");
        JsonObject ru = readJson("src/main/resources/assets/custom-enchants/lang/ru_ru.json");
        for (String key : List.of(
                "config.custom-enchants.category.availability",
                "config.custom-enchants.availability.description",
                "config.custom-enchants.availability.toggle_tooltip")) {
            assertTrue(en.has(key), "missing EN key " + key);
            assertTrue(ru.has(key), "missing RU key " + key);
            assertFalse(en.get(key).getAsString().isBlank(), key + " EN value must not be blank");
            assertFalse(ru.get(key).getAsString().isBlank(), key + " RU value must not be blank");
        }
        assertEquals("Enabled", en.get("config.custom-enchants.availability.toggle_tooltip").getAsString());
        assertEquals("Разрешено", ru.get("config.custom-enchants.availability.toggle_tooltip").getAsString());
    }

    @Test
    void deadNoParametersTranslationKeyIsRemovedFromBothLanguages() throws IOException {
        JsonObject en = readJson("src/main/resources/assets/custom-enchants/lang/en_us.json");
        JsonObject ru = readJson("src/main/resources/assets/custom-enchants/lang/ru_ru.json");
        assertFalse(en.has("config.custom-enchants.common.no_parameters"), "dead EN key must be removed");
        assertFalse(ru.has("config.custom-enchants.common.no_parameters"), "dead RU key must be removed");
    }

    @Test
    void deadPerEnchantmentEnabledTranslationKeysAreRemovedFromBothLanguages() throws IOException {
        JsonObject en = readJson("src/main/resources/assets/custom-enchants/lang/en_us.json");
        JsonObject ru = readJson("src/main/resources/assets/custom-enchants/lang/ru_ru.json");
        for (String id : ALL_IDS) {
            String key = "config.custom-enchants." + id + ".enabled";
            assertFalse(en.has(key), "dead EN key must be removed: " + key);
            assertFalse(ru.has(key), "dead RU key must be removed: " + key);
        }
    }

    @Test
    void languageFilesStayInParityAfterCleanup() throws IOException {
        JsonObject en = readJson("src/main/resources/assets/custom-enchants/lang/en_us.json");
        JsonObject ru = readJson("src/main/resources/assets/custom-enchants/lang/ru_ru.json");
        assertEquals(en.keySet(), ru.keySet(), "EN/RU key sets must match exactly after removing dead keys");
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
