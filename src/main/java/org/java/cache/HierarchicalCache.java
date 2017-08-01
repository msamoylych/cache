package org.java.cache;

import org.java.cache.strategy.DiscardingStrategy;
import org.java.cache.strategy.LFU;
import org.java.cache.strategy.LRU;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Абстрактный многоуровневый кэш
 */
abstract class HierarchicalCache implements Cache {

    private static final String DEFAULT_STRATEGY = "LRU";

    private final ConcurrentHashMap<String, Object> parallelLockMap = new ConcurrentHashMap<>();
    private final AtomicInteger count = new AtomicInteger(0);

    private final int size;
    private final DiscardingStrategy strategy;

    private Cache parent = new NullCache();

    protected HierarchicalCache(int size) {
        this(size, null);
    }

    protected HierarchicalCache(int size, String strategy) {
        if (strategy == null) {
            strategy = DEFAULT_STRATEGY;
        }

        this.size = size;
        this.strategy = defineStrategy(strategy);
    }

    @Override
    public void put(String key, Serializable value) {
        if (key == null) {
            throw new IllegalArgumentException("No key specified");
        }
        if (value == null || size == 0) {
            return;
        }

        synchronized (keyLock(key)) {
            boolean contains = contains(key);
            if (contains) {
                update(key, value);
            } else {
                if (size > 0) {
                    while (count.incrementAndGet() > size) {
                        count.decrementAndGet();

                        String discardedKey = strategy.getDiscarded();
                        if (discardedKey == null) {
                            continue;
                        }

                        synchronized (keyLock(discardedKey)) {
                            if (strategy.compareAndRemoveDiscarded(discardedKey)) {
                                Serializable discardedValue = remove(discardedKey);
                                parent.put(discardedKey, discardedValue);
                                count.decrementAndGet();
                            }
                        }
                    }
                } else {
                    count.incrementAndGet();
                }

                doPut(key, value);
            }
            strategy.update(key);
        }
    }

    @Override
    public Serializable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("No key specified");
        }

        synchronized (keyLock(key)) {
            Serializable value = doGet(key);
            if (value != null) {
                strategy.update(key);
            } else {
                Serializable parentValue = parent.get(key);
                if (parentValue != null) {
                    value = parentValue;
                    put(key, value);
                }
            }
            return value;
        }
    }

    protected abstract void doPut(String key, Serializable value);

    protected abstract Serializable doGet(String key);

    protected abstract boolean contains(String key);

    protected abstract void update(String key, Serializable value);

    protected abstract Serializable remove(String key);

    private Object keyLock(String key) {
        Object lock;
        Object newLock = new Object();
        lock = parallelLockMap.putIfAbsent(key, newLock);
        if (lock == null) {
            lock = newLock;
        }
        return lock;
    }

    private static DiscardingStrategy defineStrategy(String strategy) {
        switch (strategy) {
            case "LRU":
                return new LRU();
            case "LFU":
                return new LFU();
            default:
                throw new IllegalArgumentException("Unknown strategy: " + strategy);
        }
    }

    public void setParent(Cache parent) {
        if (parent == null) {
            throw new IllegalArgumentException("No parent specified");
        }
        this.parent = parent;
    }

    /**
     * Null cache: ничего не сохраняет - ничего не возвращает
     */
    private static class NullCache implements Cache {
        @Override
        public void put(String key, Serializable value) {
            // Do nothing
        }

        @Override
        public Serializable get(String key) {
            return null;
        }
    }
}
