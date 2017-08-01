package org.java.cache;

import org.java.cache.util.FileUtils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DiskCache extends HierarchicalCache {

    private final static int DEFAULT_SIZE = 1000;
    private final static int FILES_IN_FOLDER = 100;

    private final ConcurrentHashMap<String, DiskCacheEntry> fileNames;
    private final Path rootPath;

    public DiskCache(String rootPath, int size) {
        this(rootPath, size, null);
    }

    public DiskCache(String rootPath, int size, String strategy) {
        super(size, strategy);

        fileNames = new ConcurrentHashMap<>(size > 0 ? size : DEFAULT_SIZE);

        if (rootPath == null) {
            rootPath = "";
        }

        try {
            Path path = Paths.get(rootPath);
            if (Files.exists(path)) {
                if (!Files.isDirectory(path) || !Files.isReadable(path) || !Files.isWritable(path)) {
                    throw new IllegalArgumentException("Path " + rootPath + " is inaccessible");
                } else {
                    this.rootPath = path;
                }
            } else {
                this.rootPath = Files.createDirectories(path);
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Path " + rootPath + " is unavailable", ex);
        }
    }

    @Override
    protected void doPut(String key, Serializable value) {
        // Определение подкаталога
        Path path = Paths.get(rootPath.toString());//, Integer.toString(fileNames.size() % FILES_IN_FOLDER));
        if (!Files.exists(path)) {
            try {
                path = Files.createDirectory(path);
            } catch (IOException ex) {
                throw new IllegalStateException("Can't create subdirectory", ex);
            }
        }

        // Генерация файла
        String fileName = UUID.randomUUID().toString();
        Path file;
        try {
            file = Files.createFile(Paths.get(path.toString(), fileName));
        } catch (IOException ex) {
            throw new IllegalStateException("Can't create file", ex);
        }

        // Запись в файл
        FileUtils.writeFile(file, value);

        fileNames.put(key, new DiskCacheEntry(file.toString()));
    }

    @Override
    protected Serializable doGet(String key) {
        Path path = getFile(key, true);
        return FileUtils.readFile(path);
    }

    @Override
    protected boolean contains(String key) {
        return fileNames.containsKey(key);
    }

    @Override
    protected void update(String key, Serializable value) {
        Path path = getFile(key, true);
        FileUtils.writeFile(path, value);
    }

    @Override
    protected Serializable remove(String key) {
        Path path = getFile(key, false);
        Serializable value = FileUtils.readFile(path);
        FileUtils.deleteFile(path);
        fileNames.remove(key);
        return value;
    }

    /**
     * Поиск файла
     *
     * @param key  ключ
     * @param keep способ получение файла: {@code true} - оставить, {@code false} - удалить
     * @return {@code Path} файла
     */
    private Path getFile(String key, boolean keep) {
        // Поиск имени файла
        DiskCacheEntry entry = keep ? fileNames.get(key) : fileNames.remove(key);
        if (entry == null) {
            throw new IllegalStateException("File name for key " + key + " not found");
        }

        // Поиск файла
        String fileName = entry.fileName;
        Path file = Paths.get(fileName);
        if (!Files.exists(file)) {
            throw new IllegalStateException("File " + fileName + " doesn't exists");
        }

        return file;
    }

    private static class DiskCacheEntry {

        private String fileName;

        private DiskCacheEntry(String fileName) {
            this.fileName = fileName;
        }
    }
}
