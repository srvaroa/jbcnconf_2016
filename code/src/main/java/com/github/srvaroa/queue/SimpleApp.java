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

    public void startProducers(int n, Queue<T> q, CountDownLatch l,
                               Supplier<T> s) {
        s = new Supplier<T>() {
            Long l = new Long(0);
            @Override
            public T get() {
                return (T)l++;
            }
        };
        for (int i = 0; i < n; i++) {
            new ThreadProducer<>(q, i, l, s).start();
        }
    }

    public void startWorkers(int n, Queue<T> q, CountDownLatch l,
                             Consumer<T> c) {
        for (int i = 0; i < n; i++) {
            new ThreadConsumer<>(q, i, l, c).start();
        }
    }

    static class UnsafeAvgReducer implements Consumer<Long> {
        long sum = 0;
        long count = 0;
        @Override
        public void accept(Long i) {
            long idx = count % BATCH_SIZE;
            if (count++ > 0 && idx == 0) {
                LOG.log(String.format("%d %f ",
                        count / BATCH_SIZE,
                        (double)sum / count));
                sum = 0;
            } else {
                sum += i;
            }
        }
    }

    final static Random r = new Random();
    final static Supplier<Long> randomSupplier = () -> r.nextLong();

    final static Supplier<Long> defaultSupplier = randomSupplier;

    public static void main(String[] args) {
        int nThreads = 3;
        CountDownLatch latch = new CountDownLatch(1);
        SimpleApp app = new SimpleApp();
        Queue<Integer> q = new SafeQueue<>(1024 * 1024);
        app.startProducers(nThreads, q, latch, defaultSupplier);
        app.startWorkers(nThreads, q, latch, new UnsafeAvgReducer());
        latch.countDown();
    }

}
