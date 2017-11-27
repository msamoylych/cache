package org.java.cache.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

abstract class AbstractStrategy<E extends AbstractStrategy.Entry> implements DiscardingStrategy {

    final Map<String, E> map = new HashMap<>();

    @Override
    public synchronized boolean compareAndDiscard(String key) {
        if (Objects.equals(key, toDiscard())) {
            remove(key);
            return true;
        } else {
            return false;
        }
    }

    static class Entry {
        final String key;

        Entry(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }
    }
}
