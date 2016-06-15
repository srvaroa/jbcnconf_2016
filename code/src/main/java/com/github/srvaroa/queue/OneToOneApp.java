package com.github.srvaroa.queue;

import com.github.srvaroa.ThreadConsumer;
import com.github.srvaroa.ThreadProducer;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static com.github.srvaroa.ThreadConsumer.AVAILABLE_CONSUMERS;
import static com.github.srvaroa.queue.Common.*;

/**
 * A simple application that pairs producer and consumer thread to minimize
 * contention.
 *
 * Use JcToolsSpcsBackedQueue here.
 */
public class OneToOneApp {

    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        final int nPairs = 8;
        final int nAvailableConsumers = AVAILABLE_CONSUMERS.length;

        ThreadConsumer[] consumers = new ThreadConsumer[nPairs];
        ThreadProducer[] producers = new ThreadProducer[nPairs];

        for (int i = 0; i < nPairs; i++) {
            Consumer<Long> consumer = (Consumer<Long>)
                    AVAILABLE_CONSUMERS[i%nAvailableConsumers].newInstance();
            Queue<Long> q = buildMeAQueue(args);
            // Queue<Long> q = new JcToolsSpscBackedQueue<>(DEFAULT_Q_SIZE);

            producers[i] = new ThreadProducer(q, i, latch, defaultSupplier);
            consumers[i] = new ThreadConsumer(q, i, latch, consumer);
            producers[i].start();
            consumers[i].start();
        }

        addShutdownHook(producers, consumers);
        latch.countDown();
    }
}
