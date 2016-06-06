package com.github.srvaroa.queue;

import com.github.srvaroa.util.LatchedThread;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class QueueConcurrentTest {

    public final int capacity = 10000;

    public Queue<Integer> rb;
    public CountDownLatch startPistol;
    public CountDownLatch completed;

    Producer[] producers;
    Consumer[] consumers;

    class Consumer extends LatchedThread {
        ArrayList<Integer> consumed = new ArrayList<>();

        public Consumer(String name, CountDownLatch latch) {
            super(name, latch);
        }

        @Override
        public void run() {
            super.run();
            for (int i = 0; i < capacity; i++) {
                Integer t = rb.take();
                if (t != null)
                    consumed.add(t);
                Thread.yield();
            }
            completed.countDown();
        }
    }

    class Producer extends LatchedThread {

        ArrayList<Integer> inserted = new ArrayList<>();

        volatile boolean running = true;

        public Producer(String name, CountDownLatch latch) {
            super(name, latch);
        }

        @Override
        public void run() {
            super.run();
            running = true;
            int i = 0;
            while (running) {
                if (rb.push(i) && running) {
                    inserted.add(i);
                }
                Thread.yield();
            }
        }
    }

    @Before
    public void setup() {
        startPistol = new CountDownLatch(1);
        rb = this.getInstance(capacity);
    }

    abstract Queue<Integer> getInstance(int capacity);

    void bootstrap(int nProducers, int nConsumers) {
        completed = new CountDownLatch(nConsumers);
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

    void verify() {
        ArrayList<Integer> allProduced = new ArrayList<>();
        for (Producer p : producers) {
            p.running = false;
            allProduced.addAll(p.inserted);
        }
        ArrayList<Integer> allConsumed = new ArrayList<>();
        for (Consumer c : consumers)
            allConsumed.addAll(c.consumed);

        Collections.sort(allProduced);
        Collections.sort(allConsumed);

        for (Integer c : allConsumed) {
            Integer p = allProduced.remove(0);
            if (!p.equals(c)) {
                System.err.println(allProduced);
            }
            assertEquals(c, p);
        }

    }

}
