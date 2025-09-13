package com.feather.cores;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class GameLoopThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    @Override
    public Thread newThread(@NotNull Runnable r) {
        String namePrefix = "GameLoop-";
        Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
        t.setDaemon(false);
        t.setPriority(Thread.NORM_PRIORITY + 1); // Slightly higher priority for game loop
        return t;
    }
}