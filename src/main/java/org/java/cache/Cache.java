package org.java.cache;

import java.io.Serializable;

/**
 * Интерфейс кэша
 */
public interface Cache {

    /**
     * Запись значения в кэш
     *
     * @param key   ключ
     * @param value значение
     */
    void put(String key, Serializable value);

    /**
     * Чтение значения из кэша
     *
     * @param key ключ
     * @return найденное значение, либо {@code null}, если {@code key} отсутствует в кэше
     */
    Serializable get(String key);
}
