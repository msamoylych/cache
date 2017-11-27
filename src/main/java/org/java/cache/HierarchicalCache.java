package org.java.cache;

import org.java.cache.strategy.DiscardingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Абстрактный многоуровневый кэш
 */
abstract class HierarchicalCache extends AbstractCache implements ApplicationContextAware {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String STRATEGY = "strategy";
    private static final String NAME = "name";
    private static final String SIZE = "size";
    private static final String DEFAULT_SIZE = "100";

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock workLock = lock.readLock();
    private final Lock clearLock = lock.writeLock();
    private final ConcurrentMap<String, Object> lockMap = new ConcurrentHashMap<>();
    private final long lockMapCleanTimeout = Long.getLong("lockMapCleanTimeout", -1);
    private final int lockMapCleanThreshold = Integer.getInteger("lockMapCleanThreshold", 1000);
    private final AbstractCache parent;

    private final String strategyId;
    private DiscardingStrategy strategy;

    private final AtomicInteger count = new AtomicInteger(0);

    final String name;
    final int size;

    HierarchicalCache(AbstractCache parent, Properties properties) {
        this.parent = parent != null ? parent : NullCache.INSTANCE;
        this.strategyId = properties.getProperty(STRATEGY);
        this.name = System.getProperty(NAME, getClass().getSimpleName());
        this.size = Integer.parseInt(properties.getProperty(SIZE, DEFAULT_SIZE));

        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }

        if (lockMapCleanTimeout > 0) {
            startLockMapCleaner();
        }
    }

    @Override
    public final void put(String key, Serializable value) {
        if (key == null) {
            throw new IllegalArgumentException("No key specified");
        }

        if (value == null) {
            remove(key);
            return;
        }

        LOGGER.trace("put - '{}'", key);

        workLock.lock();
        try {
            synchronized (lock(key)) {
                if (contains(key)) {
                    strategy.update(key);
                } else {
                    while (count.incrementAndGet() > size) {
                        count.decrementAndGet();

                        String discardedKey = strategy.toDiscard();
                        if (discardedKey == null) {
                            continue;
                        }

                        synchronized (lock(discardedKey)) {
                            if (strategy.compareAndDiscard(discardedKey)) {
                                Serializable discardedValue = doRemove(discardedKey);
                                count.decrementAndGet();
                                parent.put(discardedKey, discardedValue);
                            }
                        }
                    }
                    strategy.add(key);
                }

                doPut(key, value);
            }
        } finally {
            workLock.unlock();
        }
    }

    @Override
    public final Serializable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("No key specified");
        }

        LOGGER.trace("get - '{}'", key);

        workLock.lock();
        try {
            synchronized (lock(key)) {
                Serializable value = doGet(key);
                if (value != null) {
                    strategy.update(key);
                } else {
                    Serializable parentValue = parent.remove(key);
                    if (parentValue != null) {
                        value = parentValue;
                        put(key, value);
                    }
                }
                return value;
            }
        } finally {
            workLock.unlock();
        }
    }

    @Override
    final Serializable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("No key specified");
        }

        LOGGER.trace("remove - '{}'", key);

        workLock.lock();
        try {
            synchronized (lock(key)) {
                Serializable value = doRemove(key);
                if (value != null) {
                    strategy.remove(key);
                    count.decrementAndGet();
                }
                return value;
            }
        } finally {
            workLock.unlock();
        }
    }

    abstract void doPut(String key, Serializable value);

    abstract Serializable doGet(String key);

    abstract Serializable doRemove(String key);

    abstract boolean contains(String key);

    private Object lock(String key) {
        return lockMap.computeIfAbsent(key, k -> new Object());
    }

    private void startLockMapCleaner() {
        Thread cleanThread = new Thread(() -> {
            try {
                for (; ; ) {
                    Thread.sleep(lockMapCleanTimeout);

                    if (lockMap.size() <= lockMapCleanThreshold) {
                        continue;
                    }

                    clearLock.lock();
                    try {
                        lockMap.clear();
                    } finally {
                        clearLock.unlock();
                    }
                }
            } catch (InterruptedException ex) {
                LOGGER.warn("Thread interrupted");
            }
        });
        cleanThread.setName(name + "MapCleaner");
        cleanThread.setDaemon(true);
        cleanThread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (strategyId != null) {
            strategy = applicationContext.getBean(strategyId, DiscardingStrategy.class);
        } else {
            strategy = applicationContext.getBean(DiscardingStrategy.class);
        }
    }

    /*
         * Null cache: ничего не сохраняет - ничего не возвращает
         */
    private static class NullCache extends AbstractCache {
        private static final AbstractCache INSTANCE = new NullCache();

        @Override
        public void put(String key, Serializable value) {
            // Do nothing
        }

        @Override
        public Serializable get(String key) {
            return null;
        }

        @Override
        Serializable remove(String key) {
            return null;
        }
    }
}
