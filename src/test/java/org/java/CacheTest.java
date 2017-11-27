package org.java;

import org.java.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CacheTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheTest.class);

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(8);
    private static final String CONTEXT_LOCATION = "context.xml";

    @Test
    public void test() throws Exception {
        try (ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT_LOCATION)) {
            Cache cache = context.getBean(Cache.class);
            for (;;) {
                EXECUTOR.submit(new Task(cache));
                Thread.sleep(100);
            }
        } finally {
            EXECUTOR.shutdown();
            EXECUTOR.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    private static class Task implements Runnable {
        private static final Random RND = new Random(0);
        private final String num = Integer.toString(RND.nextInt(500));

        private final Cache cache;

        public Task(Cache cache) {
            this.cache = cache;
        }

        @Override
        public void run() {
            LOGGER.info("start - '{}'", num);
            Serializable value = cache.get(num);
            if (value == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    LOGGER.warn("interrupted");
                }
                cache.put(num, "value");
            }
            LOGGER.info("end - '{}'", num);
        }
    }
}
