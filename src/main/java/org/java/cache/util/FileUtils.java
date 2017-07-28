package org.java.cache.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Методы для работы с файлами
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Запись объекта в файл
     * @param path путь к файлу
     * @param value объект
     */
    public static void writeFile(Path path, Serializable value) {
        // Сериализация
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(value);
            out.flush();
        } catch (IOException ex) {
            throw new IllegalStateException("Can't serialize value", ex);
        }

        byte[] bytes = bos.toByteArray();

        // Запись в файл
        try {
            Files.write(path, bytes);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't write file", ex);
        }
    }

    /**
     * Чтение объекта из файла
     * @param path путь к файлу
     * @return объект
     */
    public static Serializable readFile(Path path) {
        // Чтение файла
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't read file", ex);
        }

        // Десериализация
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            return (Serializable) in.readObject();
        } catch (IOException | ClassCastException | ClassNotFoundException ex) {
            throw new IllegalStateException("Can't deserialize value", ex);
        }
    }

    /**
     * Удаление файла
     * @param path путь к файлу
     */
    public static void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't delete file", ex);
        }
    }
}
