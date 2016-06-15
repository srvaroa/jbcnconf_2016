package com.github.srvaroa;

import com.github.srvaroa.log.LoggingFacade;
import com.github.srvaroa.queue.Queue;
import com.github.srvaroa.util.LatchedThread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;

public class ThreadProducer<T> extends LatchedThread {

    private final static LoggingFacade.ILogger LOG = LoggingFacade.get();

    private final Queue q;
    private final Supplier<T> supplier;
    public long produced = 0;

    public ThreadProducer(
            Queue q, int id, CountDownLatch latch, Supplier<T> supplier) {
        super("producer-" + id, latch);
        this.q = q;
        this.supplier = supplier;
    }

    @Override
    public final void run() {
        LOG.log(getName() + " starts");
        int failed = 0;
        super.run();
        while (true) {
            if (q.push(supplier.get())) {
                produced++;
            } else {
                failed++;
            }
            if (failed > 50) {
                failed = 0;
                LockSupport.parkNanos(1000);
            }
        }
    }
}
