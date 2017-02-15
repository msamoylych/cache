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

    public static void writeFile(Path path, Serializable value) {
        // Сериализация
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(value);
            out.flush();
        } catch (IOException ex) {
            throw new IllegalStateException("Can't serialize value", ex);
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        byte[] bytes = bos.toByteArray();

        // Запись в файл
        try {
            Files.write(path, bytes);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't write file", ex);
        }
    }

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
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (Serializable) in.readObject();
        } catch (IOException | ClassCastException | ClassNotFoundException ex) {
            throw new IllegalStateException("Can't deserialize value", ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public static void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't delete file", ex);
        }
    }
}
