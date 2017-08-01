package org.java.cache.util;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

/**
 * Тесты {@link TimeLine}
 */
public class TimeLineTest {

    private Field firstField;
    private Field lastField;
    private Field keyField;
    private Field prevField;
    private Field nextField;

    @BeforeClass
    public void beforeClass() throws NoSuchFieldException, ClassNotFoundException, SecurityException {
        firstField = TimeLine.class.getDeclaredField("first");
        firstField.setAccessible(true);
        lastField = TimeLine.class.getDeclaredField("last");
        lastField.setAccessible(true);

        Class<?> entryClass = Class.forName("org.java.cache.util.TimeLine$Entry");
        keyField = entryClass.getDeclaredField("key");
        keyField.setAccessible(true);
        prevField = entryClass.getDeclaredField("prev");
        prevField.setAccessible(true);
        nextField = entryClass.getDeclaredField("next");
        nextField.setAccessible(true);
    }

    @Test
    public void testSetLast() throws Exception {
        TimeLine timeLine = new TimeLine();

        timeLine.setLast("1");
        checkTimeLine(timeLine, "1");
        timeLine.setLast("1");
        checkTimeLine(timeLine, "1");

        timeLine.setLast("2");
        checkTimeLine(timeLine, "1", "2");

        timeLine.setLast("3");
        checkTimeLine(timeLine, "1", "2", "3");

        timeLine.setLast("4");
        checkTimeLine(timeLine, "1", "2", "3", "4");

        timeLine.setLast("4");
        checkTimeLine(timeLine, "1", "2", "3", "4");

        timeLine.setLast("2");
        checkTimeLine(timeLine, "1", "3", "4", "2");

        timeLine.setLast("1");
        checkTimeLine(timeLine, "3", "4", "2", "1");
    }

    @Test
    public void testGetFirst() throws Exception {
        TimeLine timeLine = new TimeLine();

        Assert.assertNull(timeLine.getFirst());

        fillTimeLine(timeLine, "1", "2", "3", "4");

        Assert.assertEquals("1", timeLine.getFirst());
    }

    @Test
    public void testRemove() throws Exception {
        TimeLine timeLine = new TimeLine();
        fillTimeLine(timeLine, "1", "2", "3", "4");

        timeLine.remove("2");
        checkTimeLine(timeLine, "1", "3", "4");

        timeLine.remove("1");
        checkTimeLine(timeLine, "3", "4");

        timeLine.remove("4");
        checkTimeLine(timeLine, "3");

        timeLine.remove("3");
    }

    private void checkTimeLine(TimeLine timeLine, String... entries) throws IllegalAccessException {
        Object entry = firstField.get(timeLine);
        for (int i = 0; i < entries.length; i++) {
            Assert.assertEquals(keyField.get(entry), entries[i]);
            Object next = nextField.get(entry);
            if (next != null) {
                entry = next;
            } else {
                Assert.assertEquals(entry, lastField.get(timeLine));
            }
        }

        entry = lastField.get(timeLine);
        for (int i = entries.length - 1; i >= 0; i--) {
            Assert.assertEquals(keyField.get(entry), entries[i]);
            Object prev = prevField.get(entry);
            if (prev != null) {
                entry = prev;
            } else {
                Assert.assertEquals(entry, firstField.get(timeLine));
            }
        }
    }

    private void fillTimeLine(TimeLine TimeLine, String... entries) {
        for (String entry : entries) {
            TimeLine.setLast(entry);
        }
    }
}
