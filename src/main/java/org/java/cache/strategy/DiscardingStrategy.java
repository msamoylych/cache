package org.java.cache.strategy;

public interface DiscardingStrategy {

    void add(String key);

    void update(String key);

    void remove(String key);

    String toDiscard();

    boolean compareAndDiscard(String key);
}
