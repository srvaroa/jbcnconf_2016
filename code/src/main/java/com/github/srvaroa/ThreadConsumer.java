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
}
