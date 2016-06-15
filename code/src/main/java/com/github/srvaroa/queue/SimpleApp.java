package com.github.srvaroa.queue;

import com.github.srvaroa.ThreadConsumer;
import com.github.srvaroa.ThreadProducer;

import java.util.concurrent.CountDownLatch;

import static com.github.srvaroa.queue.Common.*;

public class SimpleApp {

    public static void main(String[] args) throws Exception {

        CountDownLatch latch = new CountDownLatch(1);
        Queue<Long> q = buildMeAQueue(args);
        int nThreads = 3;
        ThreadProducer[] producers = startProducers(3, q, latch, defaultSupplier);
        ThreadConsumer[] consumers = startWorkers(nThreads, q, latch);
        Common.addShutdownHook(producers, consumers);
        latch.countDown();

    }

}

