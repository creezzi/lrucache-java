package io.github.creezzi.lrucache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class LruCacheTest {
    @Test
    void evictsLeastRecentlyUsed() {
        LruCache<String, Integer> cache = new LruCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.get("a");
        cache.put("c", 3);
        assertTrue(cache.get("a").isPresent());
        assertTrue(cache.get("b").isEmpty());
        assertTrue(cache.get("c").isPresent());
        assertEquals(2, cache.size());
    }

    @Test
    void getOrComputeLoadsOnlyOnMiss() {
        LruCache<String, Integer> cache = new LruCache<>(2);
        AtomicInteger loads = new AtomicInteger();
        assertEquals(7, cache.getOrCompute("x", k -> { loads.incrementAndGet(); return 7; }));
        assertEquals(7, cache.getOrCompute("x", k -> { loads.incrementAndGet(); return 8; }));
        assertEquals(1, loads.get());
    }

    @Test
    void removeReportsPresence() {
        LruCache<String, Integer> cache = new LruCache<>(2);
        cache.put("a", 1);
        assertTrue(cache.remove("a"));
        assertFalse(cache.remove("a"));
    }

    @Test
    void rejectsBadArguments() {
        assertThrows(IllegalArgumentException.class, () -> new LruCache<String, String>(0));
        assertThrows(NullPointerException.class, () -> new LruCache<String, String>(1).put("k", null));
    }

    @Test
    void survivesConcurrentUse() throws Exception {
        LruCache<Integer, Integer> cache = new LruCache<>(50);
        Thread[] threads = new Thread[8];
        for (int t = 0; t < threads.length; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < 5000; i++) {
                    cache.put(i % 200, i);
                    cache.get(i % 100);
                }
            });
            threads[t].start();
        }
        for (Thread th : threads) {
            th.join();
        }
        assertTrue(cache.size() <= 50);
    }
}
