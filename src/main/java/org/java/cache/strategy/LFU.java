package org.java.cache.strategy;

/**
 * Стратегия вытеснения: Least-Frequently Used (Наименее часто используемый)
 */
public class LFU implements DiscardingStrategy {

    @Override
    public void update(String key) {

    }

    @Override
    public String getDiscarded() {
        return null;
    }

    @Override
    public boolean compareAndRemoveDiscarded(String key) {
        return false;
    }
}
