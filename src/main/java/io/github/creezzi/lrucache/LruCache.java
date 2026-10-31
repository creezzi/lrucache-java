package io.github.creezzi.lrucache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/** Least-recently-used cache. All operations are synchronized. */
public final class LruCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> map;

    public LruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
        this.map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LruCache.this.capacity;
            }
        };
    }

    public synchronized Optional<V> get(K key) {
        return Optional.ofNullable(map.get(key));
    }

    public synchronized void put(K key, V value) {
        map.put(key, java.util.Objects.requireNonNull(value, "value"));
    }

    /** Returns the cached value, computing and storing it on a miss. */
    public synchronized V getOrCompute(K key, Function<? super K, ? extends V> loader) {
        V value = map.get(key);
        if (value == null) {
            value = java.util.Objects.requireNonNull(loader.apply(key), "loader returned null");
            map.put(key, value);
        }
        return value;
    }

    public synchronized boolean remove(K key) {
        return map.remove(key) != null;
    }

    public synchronized int size() {
        return map.size();
    }
}
