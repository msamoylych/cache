package org.java.cache;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCache extends HierarchicalCache {

    private final static int DEFAULT_SIZE = 100;

    private final ConcurrentHashMap<String, Serializable> cache;

    public MemoryCache(int size) {
        this(size, null);
    }

    public MemoryCache(int size, String strategy) {
        super(size, strategy);

        cache = new ConcurrentHashMap<>(size > 0 ? size : DEFAULT_SIZE);
    }

    @Override
    protected void _put(String key, Serializable value) {
        cache.put(key, value);
    }

    @Override
    protected Serializable _get(String key) {
        return cache.get(key);
    }

    @Override
    protected boolean _contains(String key) {
        return cache.containsKey(key);
    }

    @Override
    protected void _update(String key, Serializable value) {
        cache.put(key, value);
    }

    @Override
    protected Serializable _remove(String key) {
        return cache.remove(key);
    }
}
