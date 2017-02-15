package org.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(8);
    private static final String CONTEXT_LOCATION = "org/java/context.xml";
    private static final int TASK_COUNT = 100;

    public static void main(String[] args) {
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT_LOCATION);
            for (int i = 0; i < TASK_COUNT; i++) {
                Task task = context.getBean(Task.class);
                EXECUTOR.execute(task);
            }
            EXECUTOR.shutdown();
            EXECUTOR.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
