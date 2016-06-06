package com.github.srvaroa.queue;

import org.junit.Before;

public class SafeAtomicRefQueueTest extends QueueSingleThreadedTest {
    @Before
    public void setup() {
        rb = new SafeAtomicRefQueue<>(size);
    }
}
