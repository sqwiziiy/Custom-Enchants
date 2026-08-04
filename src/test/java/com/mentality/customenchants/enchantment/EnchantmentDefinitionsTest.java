package com.mentality.customenchants.enchantment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract for the data-driven 1.21.1 enchantment layer: the 19 {@link ResourceKey} identities and
 * their JSON definitions must stay in lockstep, with valid vanilla-schema fields and no stale
 * 1.20.1 constructs.
 */
class EnchantmentDefinitionsTest {

    private static final Set<String> IDS = Set.of(
            "glow_strike", "double_jump", "drill", "poison_blade", "lumberjack", "shadow_blade",
            "magnet", "auto_smelt", "vegetation", "rebound", "feedback", "second_wind",
            "guardians_grace", "vulnerability", "tether_master", "sky_rage", "xp_syphon",
            "kinetic_discharge", "sculk_bloom");

    private static final Set<Integer> VALID_WEIGHTS = Set.of(1, 2, 5);

    @Test
    void exactlyNineteenResourceKeysWithStableNamespaceAndPaths() {
        assertEquals(19, ModEnchantments.ALL.size());
        Set<String> paths = ModEnchantments.ALL.stream()
                .map(k -> k.identifier().getPath()).collect(Collectors.toSet());
        assertEquals(IDS, paths, "resource key paths must be exactly the stable nineteen");
        assertEquals(19, paths.size(), "no duplicate resource keys");
        for (ResourceKey<Enchantment> key : ModEnchantments.ALL) {
            assertEquals("custom-enchants", key.identifier().getNamespace());
        }
    }

    @Test
    void everyKeyHasAValidDataDrivenDefinition() throws IOException {
        Path dir = root().resolve("src/main/resources/data/custom-enchants/enchantment");
        for (String id : IDS) {
            Path json = dir.resolve(id + ".json");
            assertTrue(Files.isRegularFile(json), "missing definition JSON for " + id);
            JsonObject d = JsonParser.parseString(Files.readString(json)).getAsJsonObject();
            assertEquals("enchantment.custom-enchants." + id,
                    d.getAsJsonObject("description").get("translate").getAsString());
            for (String field : List.of("weight", "max_level", "anvil_cost", "min_cost", "max_cost",
                    "slots", "supported_items")) {
                assertTrue(d.has(field), id + " definition missing required field " + field);
            }
            int maxLevel = d.get("max_level").getAsInt();
            assertTrue(maxLevel >= 1 && maxLevel <= 3, id + " max_level out of range");
            assertTrue(VALID_WEIGHTS.contains(d.get("weight").getAsInt()), id + " weight not a vanilla rarity weight");
            assertTrue(d.get("anvil_cost").getAsInt() > 0, id + " anvil_cost must be positive");
            assertTrue(d.getAsJsonObject("min_cost").get("base").getAsInt() > 0, id + " min_cost.base positive");
            // No stale 1.20.1 constructs leaked into the JSON.
            String raw = Files.readString(json);
            assertFalse(raw.contains("EnchantmentCategory"), id + " must not mention EnchantmentCategory");
            assertFalse(raw.contains("Rarity"), id + " must not mention Rarity");
        }
    }

    @Test
    void enchantmentDirectoryContainsExactlyTheNineteenDefinitions() throws IOException {
        Path dir = root().resolve("src/main/resources/data/custom-enchants/enchantment");
        try (Stream<Path> files = Files.list(dir)) {
            Set<String> present = files.filter(p -> p.toString().endsWith(".json"))
                    .map(p -> p.getFileName().toString().replace(".json", ""))
                    .collect(Collectors.toSet());
            assertEquals(IDS, present, "enchantment directory must hold exactly the nineteen definitions");
        }
    }

    @Test
    void behaviourTagsReferenceEveryEnchantment() throws IOException {
        Path tags = root().resolve("src/main/resources/data/minecraft/tags/enchantment");
        for (String tag : List.of("non_treasure", "on_random_loot", "tradeable")) {
            String raw = Files.readString(tags.resolve(tag + ".json"));
            for (String id : IDS) {
                assertTrue(raw.contains("custom-enchants:" + id), tag + " tag must include " + id);
            }
        }
    }

    private static Path root() {
        return Path.of(System.getProperty("projectDir", ".")).toAbsolutePath().normalize();
    }
}
