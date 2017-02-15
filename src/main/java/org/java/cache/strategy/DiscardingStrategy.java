package org.java.cache.strategy;

/**
 * Стратегия вытеснения
 */
public interface DiscardingStrategy {

    /**
     * Обновление состояния элемента при его использовании
     *
     * @param key ключ
     */
    void update(String key);

    /**
     * Получение ключа элемента для вытеснения
     *
     * @return ключ
     */
    String getDiscarded();

    /**
     * Вытеснение элемента с ключом {@code key}, если его состояние не изменилось
     *
     * @param key ключ
     * @return {@code true} если элемент удален, {@code false} - иначе
     */
    boolean compareAndRemoveDiscarded(String key);
}