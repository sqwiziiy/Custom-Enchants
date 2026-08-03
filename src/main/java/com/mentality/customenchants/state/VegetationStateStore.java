package com.mentality.customenchants.state;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** Bounded, dimension-aware transient protection state for Vegetation. */
public final class VegetationStateStore {
    public static final int PROTECTION_TICKS = 10;
    public static final int MAX_ENTRIES = 4096;

    private final Map<WorldPositionKey, Long> recentlyPlanted = new HashMap<>();

    public boolean isProtected(WorldPositionKey key, long currentGameTime) {
        Long plantedAt = recentlyPlanted.get(key);
        if (plantedAt == null) return false;
        if (currentGameTime - plantedAt >= PROTECTION_TICKS) {
            recentlyPlanted.remove(key);
            return false;
        }
        return true;
    }

    public void mark(WorldPositionKey key, long gameTime) {
        if (recentlyPlanted.size() >= MAX_ENTRIES && !recentlyPlanted.containsKey(key)) {
            recentlyPlanted.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .ifPresent(entry -> recentlyPlanted.remove(entry.getKey()));
        }
        recentlyPlanted.put(key, gameTime);
    }

    public void cleanup(MinecraftServer server) {
        Iterator<Map.Entry<WorldPositionKey, Long>> iterator = recentlyPlanted.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<WorldPositionKey, Long> entry = iterator.next();
            ServerLevel level = server.getLevel(entry.getKey().dimension());
            if (level == null || level.getGameTime() - entry.getValue() >= PROTECTION_TICKS) {
                iterator.remove();
            }
        }
    }

    public void clear() {
        recentlyPlanted.clear();
    }

    public int size() {
        return recentlyPlanted.size();
    }
}
