package com.github.srvaroa.util;

import java.util.concurrent.CountDownLatch;

public class LatchedThread extends Thread {
    private CountDownLatch latch;

    public LatchedThread(String name, CountDownLatch latch) {
        super(name);
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            latch.await();
        } catch(InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}


