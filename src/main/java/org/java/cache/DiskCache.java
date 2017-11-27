package org.java.cache;

import org.java.cache.util.FileUtils;
import org.java.cache.util.HashUtils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DiskCache extends HierarchicalCache {

    private final static String ROOT_PATH = "rootPath";
    private final static String DEFAULT_ROOT_PATH = "cache";
    private final static String NUMBER_OF_FOLDERS = "numberOfFolders";
    private final static String DEFAULT_NUMBER_OF_FOLDERS = "10";
    private final static String HASH_LENGTH = "hashLength";
    private final static String DEFAULT_HASH_LENGTH = "8";

    private final Path rootPath;
    private final int numberOfFolders;
    private final int hashLength;

    public DiskCache(Properties properties) {
        this(null, properties);
    }

    public DiskCache(AbstractCache parent, Properties properties) {
        super(parent, properties);

        rootPath = Paths.get(properties.getProperty(ROOT_PATH, DEFAULT_ROOT_PATH));
        numberOfFolders = Integer.parseInt(properties.getProperty(NUMBER_OF_FOLDERS, DEFAULT_NUMBER_OF_FOLDERS));
        hashLength = Integer.parseInt(properties.getProperty(HASH_LENGTH, DEFAULT_HASH_LENGTH));

        if (numberOfFolders <= 0) {
            throw new IllegalArgumentException("Property numberOfFolders must be greater than 0");
        }
        if (hashLength <= 0 || hashLength > 40) {
            throw new IllegalArgumentException("Property hashLength must be within 0 and 40");
        }

        initDirectories();
    }

    @Override
    protected void doPut(String key, Serializable value) {
        Path path = getFilePath(key);
        FileUtils.writeFile(path, value);
    }

    @Override
    protected Serializable doGet(String key) {
        Path path = getFilePath(key);
        return Files.exists(path) ? FileUtils.readFile(path) : null;
    }

    @Override
    protected Serializable doRemove(String key) {
        Path path = getFilePath(key);
        if (Files.exists(path)) {
            Serializable value = FileUtils.readFile(path);
            FileUtils.deleteFile(path);
            return value;
        } else {
            return null;
        }
    }

    @Override
    protected boolean contains(String key) {
        Path path = getFilePath(key);
        return Files.exists(path);
    }

    private void initDirectories() {
        try {
            // Создание каталога кэша
            if (Files.exists(rootPath)) {
                if (!Files.isDirectory(rootPath) || !Files.isReadable(rootPath) || !Files.isWritable(rootPath)) {
                    throw new IllegalArgumentException("Path " + rootPath + " is inaccessible");
                }
                FileUtils.clearDirectory(rootPath);
            } else {
                Files.createDirectories(rootPath);
            }

            // Создание подкатологов
            for (int i = 0; i < numberOfFolders; i++) {
                Path dir = rootPath.resolve(Integer.toHexString(i));
                Files.createDirectory(dir);
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Path " + rootPath + " is unavailable", ex);
        }
    }

    // Определение пути к файлу
    private Path getFilePath(String key) {
        // Определение каталога
        String dir = Integer.toHexString(key.hashCode() % numberOfFolders);
        // Определение имени файла
        String name = HashUtils.sha1Hex(key).substring(0, hashLength);
        return rootPath.resolve(dir).resolve(name);
    }
}
