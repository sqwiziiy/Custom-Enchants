package com.mentality.customenchants.contract;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceContractTest {
    private static final Set<String> ENCHANTMENT_IDS = Set.of(
            "glow_strike", "double_jump", "drill", "poison_blade", "lumberjack", "shadow_blade",
            "magnet", "auto_smelt", "vegetation", "rebound", "feedback", "second_wind",
            "guardians_grace", "vulnerability", "tether_master", "sky_rage", "xp_syphon",
            "kinetic_discharge", "sculk_bloom"
    );

    @Test
    void fabricMetadataContract() throws IOException {
        String text = read("src/main/resources/fabric.mod.json");
        assertNoDuplicateKeys(text);
        JsonObject metadata = JsonParser.parseString(text).getAsJsonObject();
        assertEquals("custom-enchants", metadata.get("id").getAsString());
        assertEquals("${version}", metadata.get("version").getAsString());
        assertEquals("~1.20.1", metadata.getAsJsonObject("depends").get("minecraft").getAsString());
        assertEquals(">=17", metadata.getAsJsonObject("depends").get("java").getAsString());
        assertEquals(">=0.92.7+1.20.1", metadata.getAsJsonObject("depends").get("fabric-api").getAsString());
        assertFalse(metadata.getAsJsonObject("depends").get("fabric-api").getAsString().equals("*"));
        assertEquals(">=11.1.118", metadata.getAsJsonObject("depends").get("cloth-config").getAsString());
        assertEquals(">=7.2.2", metadata.getAsJsonObject("suggests").get("modmenu").getAsString());
        assertEquals("https://github.com/sqwiziiy/Custom-Enchants", metadata.getAsJsonObject("contact").get("sources").getAsString());
        assertEquals("https://github.com/sqwiziiy/Custom-Enchants/issues", metadata.getAsJsonObject("contact").get("issues").getAsString());
        assertEquals("client", metadata.getAsJsonArray("mixins").get(1).getAsJsonObject().get("environment").getAsString());
        assertTrue(metadata.getAsJsonArray("mixins").size() == 2);
    }

    @Test
    void translationNamespacesHaveParityAndAllEnchantments() throws IOException {
        List<String> namespaces = List.of("custom-enchants", "glowstrike");
        for (String namespace : namespaces) {
            JsonObject en = readJson("src/main/resources/assets/" + namespace + "/lang/en_us.json");
            JsonObject ru = readJson("src/main/resources/assets/" + namespace + "/lang/ru_ru.json");
            assertEquals(en.keySet(), ru.keySet(), namespace + " translation keys must match");
            assertNoDuplicateKeys(read("src/main/resources/assets/" + namespace + "/lang/en_us.json"));
            assertNoDuplicateKeys(read("src/main/resources/assets/" + namespace + "/lang/ru_ru.json"));
            for (Map.Entry<String, JsonElement> entry : en.entrySet()) {
                assertEquals(placeholders(entry.getValue().getAsString()), placeholders(ru.get(entry.getKey()).getAsString()),
                        "placeholder parity for " + entry.getKey());
            }
        }
        JsonObject en = readJson("src/main/resources/assets/custom-enchants/lang/en_us.json");
        for (String id : ENCHANTMENT_IDS) {
            assertNotNull(en.get("enchantment.custom-enchants." + id), "missing translation for " + id);
        }
        assertFalse(read("README.md").toLowerCase().contains("steady footing"));
        assertFalse(read("CHANGELOG_EN.md").toLowerCase().contains("steady footing"));
    }

    @Test
    void registrationIsExactlyTheStableNineteen() throws IOException {
        String source = read("src/main/java/com/mentality/customenchants/enchantment/ModEnchantments.java");
        Set<String> declarations = findAll(source, "public static final Enchantment ([A-Z0-9_]+)");
        Set<String> registered = findAll(source, "new ResourceLocation\\([^,]+, \"([a-z0-9_]+)\"\\)");
        assertEquals(19, declarations.size());
        assertEquals(19, registered.size());
        assertEquals(ENCHANTMENT_IDS, registered);
        assertFalse(source.contains("Example"));
        assertFalse(Files.exists(root().resolve("src/main/java/com/mentality/customenchants/mixin/ExampleMixin.java")));
    }

    @Test
    void autoSmeltDropHookCanReplaceTheVanillaReturnValue() throws IOException {
        String source = read("src/main/java/com/mentality/customenchants/mixin/AutoSmeltBlockDropsMixin.java");
        assertTrue(source.contains("at = @At(\"RETURN\"),\n            cancellable = true"));
        assertTrue(source.contains("cir.setReturnValue(transformed)"));
    }

    @Test
    void librarianTradeSourceHasUniquePositiveDefinitions() throws IOException {
        String trades = read("src/main/java/com/mentality/customenchants/trade/LibrarianEnchantTrade.java");
        String initializer = read("src/main/java/com/mentality/customenchants/CustomEnchantsMod.java");

        Pattern definition = Pattern.compile(
                "t\\(ModEnchantments\\.([A-Z0-9_]+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+)\\)");
        Matcher matcher = definition.matcher(trades);
        Set<String> uniqueBooks = new HashSet<>();
        int definitions = 0;
        while (matcher.find()) {
            definitions++;
            String enchantment = matcher.group(1);
            int bookLevel = Integer.parseInt(matcher.group(2));
            int legacyVillagerLevel = Integer.parseInt(matcher.group(3));
            int emeralds = Integer.parseInt(matcher.group(4));
            int maxUses = Integer.parseInt(matcher.group(5));
            int villagerXp = Integer.parseInt(matcher.group(6));

            assertTrue(uniqueBooks.add(enchantment + ":" + bookLevel),
                    "duplicate librarian book definition: " + enchantment + " " + bookLevel);
            assertTrue(bookLevel >= 1 && bookLevel <= 3);
            assertTrue(legacyVillagerLevel >= 1 && legacyVillagerLevel <= 5);
            assertTrue(emeralds > 0);
            assertTrue(maxUses > 0);
            assertTrue(villagerXp >= 0);
        }

        assertEquals(44, definitions);
        assertEquals(44, uniqueBooks.size());
        assertTrue(trades.contains("new ItemStack(Items.BOOK, 1)"));
        assertTrue(initializer.contains("for (int villagerLevel = 1; villagerLevel <= 5; villagerLevel++)"));
        assertTrue(initializer.contains("LibrarianEnchantTrade.random(random)"));
    }

    @Test
    void configurationDocumentationNamesEveryPublicField() throws IOException {
        String source = read("src/main/java/com/mentality/customenchants/config/ModConfig.java");
        String docs = read("CONFIGURATION.md") + read("CONFIGURATION_RU.md");
        Set<String> fields = findAll(source, "public (?:boolean|int|float) ([a-zA-Z0-9]+) =");
        for (String field : fields) {
            String levelGroup = field.replaceFirst("L[123]$", "L1/L2/L3");
            boolean documented = docs.contains(field) || docs.contains(levelGroup);
            assertTrue(documented, "undocumented config field: " + field);
        }
        assertTrue(docs.contains("`configVersion`"));
        assertTrue(source.contains("CURRENT_CONFIG_VERSION = 1"));
    }

    @Test
    void duplicateKeyDetectorDoesNotSilentlyAcceptDuplicateJson() {
        assertThrows(IllegalArgumentException.class, () -> assertNoDuplicateKeys("{\"a\":1,\"a\":2}"));
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

    private static Set<String> placeholders(String value) {
        Matcher matcher = Pattern.compile("%[-+0-9.]*[a-zA-Z]").matcher(value);
        Set<String> result = new HashSet<>();
        while (matcher.find()) result.add(matcher.group());
        return result;
    }

    private static Set<String> findAll(String source, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        Set<String> result = new LinkedHashSet<>();
        while (matcher.find()) result.add(matcher.group(1));
        return result;
    }

    private static void assertNoDuplicateKeys(String json) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(json));
        readValue(reader);
    }

    private static void readValue(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case BEGIN_OBJECT -> {
                reader.beginObject();
                Set<String> keys = new HashSet<>();
                while (reader.hasNext()) {
                    String key = reader.nextName();
                    if (!keys.add(key)) throw new IllegalArgumentException("duplicate JSON key: " + key);
                    readValue(reader);
                }
                reader.endObject();
            }
            case BEGIN_ARRAY -> {
                reader.beginArray();
                while (reader.hasNext()) readValue(reader);
                reader.endArray();
            }
            case STRING -> reader.nextString();
            case NUMBER -> reader.nextString();
            case BOOLEAN -> reader.nextBoolean();
            case NULL -> reader.nextNull();
            default -> throw new IllegalArgumentException("unexpected JSON token: " + reader.peek());
        }
    }
}
