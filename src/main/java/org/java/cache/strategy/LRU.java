package org.java.cache.strategy;

class LRU extends AbstractStrategy<LRU.Entry> {

    private Entry first;
    private Entry last;

    @Override
    public synchronized void add(String key) {
        Entry entry = new Entry(key);
        map.put(key, entry);

        if (first == null) {
            first = last = entry;
            return;
        }

        last.next = entry;
        entry.prev = last;
        last = entry;
    }

    @Override
    public synchronized void update(String key) {
        Entry entry = map.get(key);
        if (entry == null) {
            throw new IllegalStateException("Entry already removed");
        }

        if (entry == last) {
            return;
        } else if (entry == first) {
            first = first.next;
            first.prev = null;
        } else {
            entry.prev.next = entry.next;
            entry.next.prev = entry.prev;
            entry.next = null;
        }

        last.next = entry;
        entry.prev = last;
        last = entry;
    }

    @Override
    public synchronized void remove(String key) {
        Entry entry = map.remove(key);
        if (entry == null) {
            throw new IllegalStateException("Entry already removed");
        }

        if (entry == first && entry == last) {
            first = last = null;
        } else if (entry == first) {
            first = first.next;
            first.prev = null;
        } else if (entry == last) {
            last = entry.prev;
            last.next = null;
        } else if (entry.prev != null && entry.next != null) {
            entry.prev.next = entry.next;
            entry.next.prev = entry.prev;
        }
    }

    @Override
    public synchronized String toDiscard() {
        return first != null ? first.key : null;
    }

    static class Entry extends AbstractStrategy.Entry {
        private Entry prev;
        private Entry next;

        private Entry(String key) {
            super(key);
        }
    }
}
