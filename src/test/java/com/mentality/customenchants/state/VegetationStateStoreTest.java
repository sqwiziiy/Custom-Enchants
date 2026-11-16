package com.mentality.customenchants.state;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VegetationStateStoreTest {
    private static final ResourceKey<Level> OVERWORLD = ResourceKey.create(
            Registries.DIMENSION,
            Identifier.fromNamespaceAndPath("test", "overworld"));
    private static final ResourceKey<Level> NETHER = ResourceKey.create(
            Registries.DIMENSION,
            Identifier.fromNamespaceAndPath("test", "nether"));

    @Test
    void positionIdentityIncludesDimensionAndExpires() {
        VegetationStateStore store = new VegetationStateStore();
        WorldPositionKey overworld = new WorldPositionKey(OVERWORLD, 42L);
        WorldPositionKey nether = new WorldPositionKey(NETHER, 42L);

        store.mark(overworld, 100);
        assertTrue(store.isProtected(overworld, 109));
        assertFalse(store.isProtected(nether, 109));
        assertFalse(store.isProtected(overworld, 110));
    }

    @Test
    void cacheIsBounded() {
        VegetationStateStore store = new VegetationStateStore();
        for (int i = 0; i < VegetationStateStore.MAX_ENTRIES + 50; i++) {
            store.mark(new WorldPositionKey(OVERWORLD, i), i);
        }
        assertEquals(VegetationStateStore.MAX_ENTRIES, store.size());
    }
}
