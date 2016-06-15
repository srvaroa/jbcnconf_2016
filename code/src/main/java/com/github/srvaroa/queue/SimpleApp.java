package com.github.srvaroa.queue;

import com.github.srvaroa.ThreadConsumer;
import com.github.srvaroa.ThreadProducer;
import com.github.srvaroa.log.LoggingFacade;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleApp<T> {

    protected final static LoggingFacade.ILogger LOG = LoggingFacade.get();

    static final int BATCH_SIZE = 1000000;

    static class DoNothingConsumer implements Consumer<Long> {
        @Override
        public void accept(Long aLong) {
        }
    }

    /**
     * Batches BATCH_SIZE elements consumed, outputs average, resets.
     */
    static class UnsafeAvgReducer implements Consumer<Long> {
        long sum = 0;
        long count = 0;
        @Override
        public void accept(Long i) {
            long idx = count % BATCH_SIZE;
            if (count++ > 0 && idx == 0) {
                LOG.log(String.format("%s iteration %d, last %s averaged %.2f ",
                        Thread.currentThread().getName(),
                        count / BATCH_SIZE,
                        BATCH_SIZE,
                        (double)sum / count));
                sum = 0;
            } else {
                sum += i;
            }
        }
    }

    /**
     * Filters elements multiple of PRIME.
     */
    static class ModuloFilter implements Consumer<Long> {
        static final int PRIME = 39;
        long read = 0;
        long filtered = 0;
        @Override
        public void accept(Long i) {
            read++;
            if (i % PRIME == 0) {
                filtered++;
            }
            if (filtered > 0 && filtered % 100000 == 0) {
                LOG.log(String.format("%s %d more multiples of %d found",
                        Thread.currentThread().getName(), filtered, PRIME));
                filtered = 0;
            }
        }
    }

    static final Class[] AVAILABLE_CONSUMERS = new Class[] {
        UnsafeAvgReducer.class,
        DoNothingConsumer.class, // just counts
        ModuloFilter.class,
    };

    public ThreadProducer[] startProducers(int n, Queue<T> q, CountDownLatch l,
                               Supplier<T> s) {
        s = new Supplier<T>() {
            Long l = new Long(0);
            @Override
            public T get() {
                return (T)l++;
            }
        };
        ThreadProducer[] threads = new ThreadProducer[n];
        for (int i = 0; i < n; i++) {
            threads[i] = new ThreadProducer<>(q, i, l, s);
            threads[i].start();
        }
        return threads;
    }

    public ThreadConsumer[] startWorkers(int n, Queue<T> queue,
        CountDownLatch latch) throws Exception {
        ThreadConsumer[] threads = new ThreadConsumer[n];
        for (int i = 0; i < n; i++) {
            Class usedConsumer = AVAILABLE_CONSUMERS[i%3];
            threads[i] = new ThreadConsumer<>(queue, i, latch,
                    (Consumer<Long>)usedConsumer.newInstance());
            threads[i].start();;
        }
        return threads;
    }


    final static Random r = new Random();
    final static Supplier<Long> randomSupplier = () -> r.nextLong();

    final static Supplier<Long> defaultSupplier = randomSupplier;

    public static void main(String[] args) throws Exception {

        SimpleApp app = new SimpleApp();

        CountDownLatch latch = new CountDownLatch(1);
        Queue<Integer> q = new SynchronizedQueue<>(1024 * 1024);

        int nThreads = 6;
        ThreadProducer[] producers = app.startProducers(nThreads, q, latch,
                                                        defaultSupplier);

        ThreadConsumer[] consumers = app.startWorkers(nThreads, q, latch);

        final long tStart = System.currentTimeMillis();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                final long tEnd = System.currentTimeMillis();
                final long totalSecs = (tEnd - tStart) / 1000;
                System.out.println("Time: " + totalSecs + "s");
                for (ThreadProducer<Long> p : producers) {
                    double perSec = p.produced / totalSecs;
                    System.out.println(String.format("%s emitted\t%d\t%.2f" +
                            " per second", p.getName(), p.produced, perSec));
                }
                for (ThreadConsumer<Long> c : consumers) {
                    double perSec = c.consumed / totalSecs;
                    System.out.println(String.format("%s consumed\t%d\t%.2f" +
                            " per second", c.getName(), c.consumed, perSec));
                }
            }
        });

        latch.countDown();

    }

}
