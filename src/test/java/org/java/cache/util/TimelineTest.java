package org.java.cache.util;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

/**
 * Тесты {@link Timeline}
 */
public class TimelineTest {

    private Field firstField;
    private Field lastField;
    private Field keyField;
    private Field prevField;
    private Field nextField;

    @BeforeClass
    public void beforeClass() throws NoSuchFieldException, ClassNotFoundException, SecurityException {
        firstField = Timeline.class.getDeclaredField("first");
        firstField.setAccessible(true);
        lastField = Timeline.class.getDeclaredField("last");
        lastField.setAccessible(true);

        Class<?> entryClass = Class.forName("org.java.cache.util.Timeline$Entry");
        keyField = entryClass.getDeclaredField("key");
        keyField.setAccessible(true);
        prevField = entryClass.getDeclaredField("prev");
        prevField.setAccessible(true);
        nextField = entryClass.getDeclaredField("next");
        nextField.setAccessible(true);
    }

    @Test
    public void testSetLast() throws Exception {
        Timeline timeline = new Timeline();

        timeline.setLast("1");
        checkTimeline(timeline, "1");
        timeline.setLast("1");
        checkTimeline(timeline, "1");

        timeline.setLast("2");
        checkTimeline(timeline, "1", "2");

        timeline.setLast("3");
        checkTimeline(timeline, "1", "2", "3");

        timeline.setLast("4");
        checkTimeline(timeline, "1", "2", "3", "4");

        timeline.setLast("4");
        checkTimeline(timeline, "1", "2", "3", "4");

        timeline.setLast("2");
        checkTimeline(timeline, "1", "3", "4", "2");

        timeline.setLast("1");
        checkTimeline(timeline, "3", "4", "2", "1");
    }

    @Test
    public void testGetFirst() throws Exception {
        Timeline timeline = new Timeline();

        Assert.assertNull(timeline.getFirst());

        fillTimeline(timeline, "1", "2", "3", "4");

        Assert.assertEquals("1", timeline.getFirst());
    }

    @Test
    public void testRemove() throws Exception {
        Timeline timeline = new Timeline();
        fillTimeline(timeline, "1", "2", "3", "4");

        timeline.remove("2");
        checkTimeline(timeline, "1", "3", "4");

        timeline.remove("1");
        checkTimeline(timeline, "3", "4");

        timeline.remove("4");
        checkTimeline(timeline, "3");

        timeline.remove("3");

    }

    private void checkTimeline(Timeline timeline, String... entries) throws IllegalAccessException {
        Object entry = firstField.get(timeline);
        for (int i = 0; i < entries.length; i++) {
            Assert.assertEquals(keyField.get(entry), entries[i]);
            Object next = nextField.get(entry);
            if (next != null) {
                entry = next;
            } else {
                Assert.assertEquals(entry, lastField.get(timeline));
            }
        }

        entry = lastField.get(timeline);
        for (int i = entries.length - 1; i >= 0; i--) {
            Assert.assertEquals(keyField.get(entry), entries[i]);
            Object prev = prevField.get(entry);
            if (prev != null) {
                entry = prev;
            } else {
                Assert.assertEquals(entry, firstField.get(timeline));
            }
        }
    }

    private void fillTimeline(Timeline timeline, String... entries) {
        for (String entry : entries) {
            timeline.setLast(entry);
        }
    }
}
