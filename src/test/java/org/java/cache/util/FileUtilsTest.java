package org.java.cache.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Тесты {@link FileUtils}
 */
public class FileUtilsTest {

    @Test
    public void testWriteFile() throws IOException {
        Path path = Files.createTempFile(UUID.randomUUID().toString(), ".tmp");
        Files.delete(path);
        String value = "Text";

        FileUtils.writeFile(path, value);
        Assert.assertTrue(Files.exists(path));

        Assert.assertEquals(value, FileUtils.readFile(path));

        FileUtils.deleteFile(path);
        Assert.assertFalse(Files.exists(path));
    }
}
