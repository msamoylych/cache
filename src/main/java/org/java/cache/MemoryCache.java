package org.java.cache;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MemoryCache extends HierarchicalCache {

    private final ConcurrentMap<String, Serializable> cache;

    public MemoryCache(Properties properties) {
        this(null, properties);
    }

    public MemoryCache(AbstractCache parent, Properties properties) {
        super(parent, properties);

        cache = new ConcurrentHashMap<>(size, 1.0f);
    }

    @Override
    protected void doPut(String key, Serializable value) {
        cache.put(key, value);
    }

    @Override
    protected Serializable doGet(String key) {
        return cache.get(key);
    }

    @Override
    protected Serializable doRemove(String key) {
        return cache.remove(key);
    }

    @Override
    protected boolean contains(String key) {
        return cache.containsKey(key);
    }
}
