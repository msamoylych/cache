package org.java.cache.strategy;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LRUTest {

    @Test
    public void test1() {
        LRU lru = new LRU();

        lru.add("1");
        lru.add("2");
        lru.add("3");
        lru.add("4");
        lru.add("5");

        Assert.assertEquals(lru.toDiscard(), "1");

        lru.update("2");
        lru.remove("1");

        Assert.assertEquals(lru.toDiscard(), "3");

        lru.update("3");

        Assert.assertEquals(lru.toDiscard(), "4");
    }
}
