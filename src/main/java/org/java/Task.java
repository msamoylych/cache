package org.java;

import org.java.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

public class Task implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

    private static final int TASK_TYPE_COUNT = 10;
    private static final Random RND = new Random(1234567890);

    @Autowired
    private Cache cache;

    private String name;

    public Task() {
        name = "Task" + RND.nextInt(TASK_TYPE_COUNT);
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        Boolean value = (Boolean) cache.get(name);
        if (value == null) {
            calculate();
            cache.put(name, Boolean.TRUE);
        }
        long end = System.nanoTime();
        long duration = end - start;
        LOGGER.info("Task \"{}\" duration: {}", name, duration);
    }

    private void calculate() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {
        }
    }
}
