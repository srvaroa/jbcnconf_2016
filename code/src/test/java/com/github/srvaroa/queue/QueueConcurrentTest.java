package com.github.srvaroa.queue;

import com.github.srvaroa.util.LatchedThread;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

public abstract class QueueConcurrentTest {

    public final int capacity = 100;

    public Queue<Long> rb;
    public CountDownLatch startPistol;
    public CountDownLatch completed;

    Producer[] producers;
    Consumer[] consumers;

    // Used by producers
    AtomicLong producerIdx = new AtomicLong(0);
    final int maxToEmit = capacity * 10;

    /**
     * All Producer instances collaborate to emit monotonically increasing
     * numbers until `maxToEmit`.
     */
    class Producer extends LatchedThread {

        public Producer(String name, CountDownLatch latch) {
            super(name, latch);
        }

        @Override
        public void run() {
            super.run();
            long next = -1;
            do {
                if (next == -1 || rb.push(next)) {
                    // load the next one to push
                    next = producerIdx.getAndIncrement();
                }
                Thread.yield();
            } while (next < maxToEmit);
            completed.countDown();
        }
    }

    /**
     * Keeps pulling elements from the buffer until told to stop.
     */
    class Consumer extends LatchedThread {
        ArrayList<Long> consumed = new ArrayList<>();
        volatile boolean running;

        public Consumer(String name, CountDownLatch latch) {
            super(name, latch);
        }

        @Override
        public void run() {
            super.run();
            running = true;
            while (running) {
                Long t = rb.take();
                if (t != null)
                    consumed.add(t);
                Thread.yield();
            }
        }
    }


    @Before
    public void setup() {
        startPistol = new CountDownLatch(1);
        rb = this.getInstance(capacity);
    }

    abstract Queue<Long> getInstance(int capacity);

    void bootstrap(int nProducers, int nConsumers) {
        completed = new CountDownLatch(nProducers);
        producers = new Producer[nProducers];
        consumers = new Consumer[nConsumers];
        for (int i = 0; i < nProducers; i++) {
            producers[i] = new Producer("producer-" + i, startPistol);
            producers[i].start();
        }
        for (int i = 0; i < nConsumers; i++) {
            consumers[i] = new Consumer("consumer-" + i, startPistol);
            consumers[i].start();
        }
        startPistol.countDown();
    }

    @Test
    public void test1Threads() throws Exception {
        bootstrap(1, 1);
        startPistol.countDown();
        assertTrue(completed.await(4000, TimeUnit.SECONDS));
        verify();
    }

    @Test
    public void test2Threads() throws Exception {
        bootstrap(2, 2);
        startPistol.countDown();
        assertTrue(completed.await(4000, TimeUnit.SECONDS));
        verify();
    }

    @Test
    public void test8Threads() throws Exception {
        bootstrap(8, 8);
        startPistol.countDown();
        assertTrue(completed.await(4000, TimeUnit.SECONDS));
        verify();
    }

    void verify() throws InterruptedException {
        ArrayList<Long> allConsumed = new ArrayList<>();
        for (Consumer c : consumers) {
            c.running = false;
            c.join();
            allConsumed.addAll(c.consumed);
        }

        Collections.sort(allConsumed);

        Long prev = null;
        do {
            Long curr = allConsumed.remove(0);
            assertNotNull(curr);
            if (prev == null) {
                assertEquals(Long.valueOf(0), curr);
            } else {
                assertEquals(Long.valueOf(prev), Long.valueOf(curr-1));
            }
            prev = curr;
        } while(!allConsumed.isEmpty());

    }

}
