package org.java.cache.util;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

public class WeightLineTest {

    private Field downField;
    private Field upField;
    private Field keyField;
    private Field weightField;
    private Field prevField;
    private Field nextField;

    @BeforeClass
    public void beforeClass() throws NoSuchFieldException, ClassNotFoundException, SecurityException {
        downField = WeightLine.class.getDeclaredField("down");
        downField.setAccessible(true);
        upField = WeightLine.class.getDeclaredField("up");
        upField.setAccessible(true);

        Class<?> entryClass = Class.forName("org.java.cache.util.WeightLine$Entry");
        keyField = entryClass.getDeclaredField("key");
        keyField.setAccessible(true);
        weightField = entryClass.getDeclaredField("weight");
        weightField.setAccessible(true);
        prevField = entryClass.getDeclaredField("prev");
        prevField.setAccessible(true);
        nextField = entryClass.getDeclaredField("next");
        nextField.setAccessible(true);
    }

    @Test
    public void testUp() throws Exception {
        WeightLine weightLine = new WeightLine();

        weightLine.up("1");
        weightLine.up("2");
        weightLine.up("3");
        weightLine.up("4");
        weightLine.up("5");

        checkWeightLine(weightLine, new String[]{"1", "2", "3", "4", "5"}, new int[]{0, 0, 0, 0, 0});

        weightLine.up("1");

        checkWeightLine(weightLine, new String[]{"2", "3", "4", "5", "1"}, new int[]{0, 0, 0, 0, 1});

        weightLine.up("3");

        checkWeightLine(weightLine, new String[]{"2", "4", "5", "1", "3"}, new int[]{0, 0, 0, 1, 1});

        weightLine.up("1");

        checkWeightLine(weightLine, new String[]{"2", "4", "5", "3", "1"}, new int[]{0, 0, 0, 1, 2});

        weightLine.up("1");

        checkWeightLine(weightLine, new String[]{"2", "4", "5", "3", "1"}, new int[]{0, 0, 0, 1, 3});

        weightLine.up("6");

        checkWeightLine(weightLine, new String[]{"2", "4", "5", "6", "3", "1"}, new int[]{0, 0, 0, 0, 1, 3});
    }

    private void checkWeightLine(WeightLine weightLine, String[] entries, int[] weights) throws IllegalAccessException {
        Object entry = downField.get(weightLine);
        for (int i = 0; i < entries.length; i++) {
            Assert.assertEquals(keyField.get(entry), entries[i]);
            Assert.assertEquals(weightField.get(entry), weights[i]);
            Object next = nextField.get(entry);
            if (next != null) {
                entry = next;
            } else {
                Assert.assertEquals(entry, upField.get(weightLine));
            }
        }

        entry = upField.get(weightLine);
        for (int i = entries.length - 1; i >= 0; i--) {
            Assert.assertEquals(keyField.get(entry), entries[i]);
            Object prev = prevField.get(entry);
            if (prev != null) {
                entry = prev;
            } else {
                Assert.assertEquals(entry, downField.get(weightLine));
            }
        }
    }
}
