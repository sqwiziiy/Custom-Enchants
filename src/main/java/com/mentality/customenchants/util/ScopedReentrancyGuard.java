package com.mentality.customenchants.util;

import java.util.HashSet;
import java.util.Set;

/** A server-thread scoped guard keyed by the actor that owns the current chain. */
public final class ScopedReentrancyGuard<K> {
    private final Set<K> activeKeys = new HashSet<>();

    public Scope tryEnter(K key) {
        if (key == null || !activeKeys.add(key)) return null;
        return new Scope(key);
    }

    public boolean isActive(K key) {
        return activeKeys.contains(key);
    }

    public final class Scope implements AutoCloseable {
        private final K key;
        private boolean closed;

        private Scope(K key) {
            this.key = key;
        }

        @Override
        public void close() {
            if (!closed) {
                closed = true;
                activeKeys.remove(key);
            }
        }
    }
}
