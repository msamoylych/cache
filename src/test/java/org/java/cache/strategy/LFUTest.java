package org.java.cache.strategy;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LFUTest {

    @Test
    public void test1() throws Exception {
        LFU lfu = new LFU();

        lfu.add("1");
        Thread.sleep(1);
        lfu.add("2");
        Thread.sleep(1);
        lfu.add("3");
        Thread.sleep(1);
        lfu.add("4");
        Thread.sleep(1);
        lfu.add("5");

        Assert.assertEquals(lfu.toDiscard(), "1");

        lfu.update("1");

        Assert.assertEquals(lfu.toDiscard(), "2");
    }

    @Test
    public void test2() throws Exception {
        LFU lfu = new LFU();

        lfu.add("1");
        lfu.update("1");
        lfu.add("2");
        lfu.update("2");
        lfu.add("3");
        lfu.add("4");
        lfu.update("4");
        lfu.add("5");
        lfu.update("5");

        Thread.sleep(1);

        Assert.assertEquals(lfu.toDiscard(), "3");
    }
}
