package org.java.cache.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для сортировки элементов по времени последнего использования
 */
public class WeightLine {

    private final Map<String, Entry> map = new HashMap<>();

    private Entry down;
    private Entry up;

    /**
     * Увеличение счётчика использования
     */
    public void up(String key) {
        if (key == null) {
            throw new IllegalArgumentException("No key specified");
        }

        Entry entry = map.get(key);
        if (entry != null) {
            entry.weight++;

            if (entry.next != null && entry.next.weight <= entry.weight) {
                entry.next.prev = entry.prev;
                if (entry.prev != null) {
                    entry.prev.next = entry.next;
                } else {
                    down = entry.next;
                }

                Entry prev = entry.next;
                while (prev.next != null && prev.next.weight <= entry.weight) {
                    prev = prev.next;
                }

                if (prev.next != null) {
                    prev.next.prev = entry;
                    entry.next = prev.next;
                } else {
                    up = entry;
                }
                prev.next = entry;
                entry.prev = prev;
            }
        } else {
            entry = new Entry(key);
            map.put(key, entry);

            if (down == null) {
                down = up = entry;
                return;
            }

            if (down.weight > entry.weight) {
                entry.next = down;
                down = entry;
            } else {
                Entry prev = down;
                while (prev.next != null && prev.next.weight >= entry.weight) {
                    prev = prev.next;
                }

                if (prev.next != null) {
                    prev.next.prev = entry;
                    entry.next = prev.next;
                } else {
                    up = entry;
                }
                prev.next = entry;
                entry.prev = prev;
            }
        }
    }

    /**
     * Получение нижнего элемента
     */
    public String getDown() {
        return down != null ? down.key : null;
    }

    /**
     * Удаление элемента
     */
    public void remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("No key specified");
        }

        Entry entry = map.remove(key);
        if (entry == null) {
            throw new IllegalStateException("Entry not found");
        }
    }

    /**
     * Интерфейс для поддержки сортировки
     */
    private static class Entry {
        private final String key;

        private Entry prev;
        private Entry next;

        private int weight;

        private Entry(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }
    }
}
