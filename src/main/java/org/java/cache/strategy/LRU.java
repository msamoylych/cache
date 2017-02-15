package org.java.cache.strategy;

import org.java.cache.util.Timeline;

import java.util.Objects;

/**
 * Стратегия вытеснения: Least recently used (Давно неиспользуемый)
 */
public class LRU implements DiscardingStrategy {

    private Timeline timeline = new Timeline();

    @Override
    public synchronized void update(String key) {
        timeline.setLast(key);
    }

    @Override
    public synchronized String getDiscarded() {
        return timeline.getFirst();
    }

    @Override
    public synchronized boolean compareAndRemoveDiscarded(String key) {
        String discarded = getDiscarded();
        if (Objects.equals(discarded, key)) {
            timeline.remove(key);
            return true;
        }
        return false;
    }
}
