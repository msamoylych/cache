package org.java.cache.strategy;

import org.java.cache.util.Timeline;

import java.util.Objects;

/**
 * Стратегия вытеснения: Least recently used (Давно неиспользуемый)
 */
public class LRU implements DiscardingStrategy {

    private final Timeline timeline = new Timeline();

    @Override
    public void update(String key) {
        synchronized (timeline) {
            timeline.setLast(key);
        }
    }

    @Override
    public String getDiscarded() {
        synchronized (timeline) {
            return timeline.getFirst();
        }
    }

    @Override
    public boolean compareAndRemoveDiscarded(String key) {
        synchronized (timeline) {
            String discarded = getDiscarded();
            if (Objects.equals(discarded, key)) {
                timeline.remove(key);
                return true;
            }
            return false;
        }
    }
}
