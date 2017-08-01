package org.java.cache.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для сортировки элементов по времени последнего использования
 */
public class TimeLine {

    private final Map<String, Entry> map = new HashMap<>();
    private Entry first;
    private Entry last;

    /**
     * Установка последнего элемента
     */
    public void setLast(String key) {
        if (key == null) {
            throw new IllegalArgumentException("No key specified");
        }

        Entry entry = map.get(key);
        if (entry == null) {
            entry = new Entry(key);
            map.put(key, entry);
        }

        if (first == null) {
            first = last = entry;
            return;
        }

        if (entry == last) {
            return;
        }

        if (entry.next != null) {
            if (entry.prev != null) {
                entry.prev.next = entry.next;
                entry.next.prev = entry.prev;
            } else {
                first = entry.next;
                first.prev = null;
            }
            entry.next = null;
        }

        entry.prev = last;
        last.next = entry;
        last = entry;
    }

    /**
     * Получение первого элемента
     */
    public String getFirst() {
        return first != null ? first.key : null;
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
            entry.next.prev = entry.prev;
            entry.prev.next = entry.next;
        }
    }

    /**
     * Класс для поддержки сортировки
     */
    private static class Entry {
        private final String key;

        private Entry prev;
        private Entry next;

        private Entry(String key) {
            this.key = key;
        }
    }
}
