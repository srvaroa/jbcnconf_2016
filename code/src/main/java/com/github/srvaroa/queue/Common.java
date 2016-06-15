package com.github.srvaroa.queue;

import com.github.srvaroa.ThreadConsumer;
import com.github.srvaroa.ThreadProducer;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.github.srvaroa.ThreadConsumer.AVAILABLE_CONSUMERS;

/**
 * Some utility methods shared by all sample apps.
 */
public class Common {

    private final static Random r = new Random();
    private final static Supplier<Long> randomSupplier = () -> r.nextLong();

    // Size of our queues
    final static int DEFAULT_Q_SIZE = 1024 * 1024;

    // Switch supplier implementation here
    final static Supplier<Long> defaultSupplier = randomSupplier;

    /**
     * No need to touch, returns a queue implementation based on arg[0], or
     * the default (SynchronizedQueue).
     */
    static Queue<Long> buildMeAQueue(String[] args) throws Exception {
        String qImpl = SynchronizedQueue.class.getSimpleName();
        if (args.length > 0) {
            qImpl = args[0];
        }
        System.out.println("Using queue: " + qImpl);
        return QueueFactory.get(qImpl, DEFAULT_Q_SIZE);
    }

    /**
     * No need to touch.  Prints a summary of the producers after execution.
     */
    static void summarize(ThreadProducer[] ps, long totalSecs) {
        for (ThreadProducer<Long> p : ps) {
            double perSec = p.produced / totalSecs;
            System.out.println(String.format("%s emitted\t%d\t%.2f" +
                    " per second", p.getName(), p.produced, perSec));
        }
    }

    /**
     * No need to touch.  Prints a summary of the consumers after execution.
     */
    static void summarize(ThreadConsumer[] cs, long totalSecs) {
        for (ThreadConsumer<Long> c : cs) {
            double perSec = c.consumed / totalSecs;
            System.out.println(String.format("%s consumed\t%d\t%.2f" +
                    " per second", c.getName(), c.consumed, perSec));
        }
    }

    /**
     * Creates n identical producers emitting to the given queue.  All
     * producers will wait for the latch opens before starting.
     */
    static <T> ThreadProducer[] startProducers(int n, Queue<T> q,
                                               CountDownLatch l,
                                               Supplier<T> s) {
        ThreadProducer[] threads = new ThreadProducer[n];
        for (int i = 0; i < n; i++) {
            threads[i] = new ThreadProducer<>(q, i, l, s);
            threads[i].start();
        }
        return threads;
    }

    /**
     * Starts n workers competing to read from the given queue.  All will
     * wait until the latch is released.
     */
    static <T> ThreadConsumer[] startWorkers(int n, Queue<T> queue,
                                             CountDownLatch latch)
            throws Exception {
        final int nAvailableConsumers = AVAILABLE_CONSUMERS.length;
        ThreadConsumer[] threads = new ThreadConsumer[n];
        for (int i = 0; i < n; i++) {
            Consumer<Long> consumer = (Consumer<Long>)
                    AVAILABLE_CONSUMERS[i%nAvailableConsumers].newInstance();
            threads[i] = new ThreadConsumer<>(queue, i, latch, consumer);
            threads[i].start();;
        }
        return threads;
    }

    /**
     * Adds a shutdown hook that dumps the summary of the execution.  Gradle
     * doesn't like it, so better to run the java command directly.
     */
    static void addShutdownHook(ThreadProducer[] producers,
                                ThreadConsumer[] consumers) {
        final long tStart = System.currentTimeMillis();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                final long tEnd = System.currentTimeMillis();
                final long totalSecs = (tEnd - tStart) / 1000;
                System.out.println("Time: " + totalSecs + "s");
                summarize(producers, totalSecs);
                summarize(consumers, totalSecs);
            }
        });
    }
}
