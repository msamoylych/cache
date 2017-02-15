package org.java.cache.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для сортировки элементов по времени последнего использования
 */
@SuppressWarnings("unchecked")
public class Timeline {

    private static final int DEFAULT_CAPACITY = 1 << 10;
    
    private Map<String, Entry> map = new HashMap<>(DEFAULT_CAPACITY);
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
        if (entry != null) {
            entry.touch();
        } else {
            entry = new Entry(key);
            map.put(key, entry);
        }

        if (first == null && last == null) {
            first = entry;
            last = entry;
            return;
        }

        if (entry == last) {
            return;
        }

        if (entry.next != null) {
            if (entry.prev== null) {
                first = entry.next;
                first.prev = null;
            } else {
                Entry previous = entry.prev;
                previous.next = entry.next;
                entry.next.prev = entry.prev;
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
            first = null;
            last = null;
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
     * Интерфейс для поддержки сортировки
     */
    private static class Entry {
        final String key;
        
        Entry prev;
        Entry next;
        
        long createTime;
        long lastAccessTime;

        Entry(String key) {
            this.key = key;
            
            long currentTime = System.currentTimeMillis();
            createTime = currentTime;
            lastAccessTime = currentTime;
        }
        
        void touch() {
            lastAccessTime = System.currentTimeMillis();
        }
    }
}
