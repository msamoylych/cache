package org.java.cache.util;

import java.io.*;
import java.nio.file.DirectoryStream;
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
     *
     * @param path  путь к файлу
     * @param value объект
     */
    public static void writeFile(Path path, Serializable value) {
        try (ObjectOutput out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))) {
            out.writeObject(value);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't write file: " + path, ex);
        }
    }

    /**
     * Чтение объекта из файла
     *
     * @param path путь к файлу
     * @return объект
     */
    public static Serializable readFile(Path path) {
        try (ObjectInput in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
            return (Serializable) in.readObject();
        } catch (IOException | ClassCastException | ClassNotFoundException ex) {
            throw new IllegalStateException("Can't read file: " + path, ex);
        }
    }

    /**
     * Удаление файла
     *
     * @param path путь к файлу
     */
    public static void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't delete file", ex);
        }
    }

    /**
     * Очистка директории
     *
     * @param path путь к директории
     */
    public static void clearDirectory(Path path) {
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {
            for (Path p : paths) {
                if (Files.isDirectory(p)) {
                    clearDirectory(p);
                }
                deleteFile(p);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Can't clear directory", ex);
        }
    }
}
