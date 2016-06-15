package com.github.srvaroa;

import com.github.srvaroa.log.LoggingFacade;
import com.github.srvaroa.queue.Queue;
import com.github.srvaroa.util.LatchedThread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

public class ThreadConsumer<T> extends LatchedThread {

    private final static LoggingFacade.ILogger LOG = LoggingFacade.get();

    private final Queue<T> q;
    private final Consumer c;
    public long consumed = 0;

    public ThreadConsumer(Queue<T> q, int id, CountDownLatch l, Consumer c) {
        super("consumer-" + id, l);
        this.q = q;
        this.c = c;
    }

    @Override
    public final void run() {
        LOG.log(getName() + " starts");
        T i;
        super.run();
        while(true) {
            i = q.take();
            if (i == null) {
                LockSupport.parkNanos(1000);
            } else {
                consumed++;
                c.accept(i);
            }
        }
    }

    // Question: what do you think about the multiple Consumer implementations?

    public static class DoNothingConsumer implements Consumer<Long> {
        @Override
        public void accept(Long aLong) {
        }
    }

    public static final int BATCH_SIZE = 1000000;
    public static final Class[] AVAILABLE_CONSUMERS = new Class[] {
            UnsafeAvgReducer.class,
            DoNothingConsumer.class, // just counts
            ModuloFilter.class,
    };


    /**
     * Batches BATCH_SIZE elements consumed, outputs average, resets.
     */
    public static class UnsafeAvgReducer implements Consumer<Long> {
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
    public static class ModuloFilter implements Consumer<Long> {
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

}
