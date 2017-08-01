package org.java.cache.strategy;

import org.java.cache.util.TimeLine;

import java.util.Objects;

/**
 * Стратегия вытеснения: Least recently used (Давно неиспользуемый)
 */
public class LRU implements DiscardingStrategy {

    private final TimeLine timeLine = new TimeLine();

    @Override
    public void update(String key) {
        synchronized (timeLine) {
            timeLine.setLast(key);
        }
    }

    @Override
    public String getDiscarded() {
        synchronized (timeLine) {
            return timeLine.getFirst();
        }
    }

    @Override
    public boolean compareAndRemoveDiscarded(String key) {
        synchronized (timeLine) {
            String discarded = getDiscarded();
            if (Objects.equals(discarded, key)) {
                timeLine.remove(key);
                return true;
            }
            return false;
        }
    }
}
