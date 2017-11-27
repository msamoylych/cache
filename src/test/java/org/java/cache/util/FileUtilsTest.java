package org.java.cache.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Тесты {@link FileUtils}
 */
public class FileUtilsTest {

    @Test
    public void testWriteFile() throws IOException {
        Path path = Paths.get(UUID.randomUUID().toString());
        String value = "Text";

        FileUtils.writeFile(path, value);
        Assert.assertTrue(Files.exists(path));

        Assert.assertEquals(value, FileUtils.readFile(path));

        FileUtils.deleteFile(path);
        Assert.assertFalse(Files.exists(path));
    }
}
