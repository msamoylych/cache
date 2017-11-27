package org.java.cache.strategy;

class LFU extends AbstractStrategy<LFU.Entry> {

    @Override
    public synchronized void add(String key) {
        Entry entry = new Entry(key);
        map.put(key, entry);
    }

    @Override
    public synchronized void update(String key) {
        Entry entry = map.get(key);
        if (entry == null) {
            throw new IllegalStateException("Entry already removed");
        }
        entry.useCount++;
    }

    @Override
    public synchronized void remove(String key) {
        map.remove(key);
    }

    @Override
    public synchronized String toDiscard() {
        String key = null;

        double min = Double.MAX_VALUE;
        for (Entry entry : map.values()) {
            double frequency = entry.frequency();
            if (frequency < min) {
                min = frequency;
                key = entry.key;
            }
        }

        return key;
    }

    static class Entry extends AbstractStrategy.Entry {
        private long addTime = System.currentTimeMillis();
        private double useCount = 1;

        private Entry(String key) {
            super(key);
        }

        private double frequency() {
            return useCount / (System.currentTimeMillis() - addTime);
        }
    }
}
