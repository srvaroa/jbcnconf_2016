package com.github.srvaroa.queue;

import org.junit.Before;

public class JcToolsMpmcBackedQueueTest extends QueueSingleThreadedTest {
    @Before
    public void setup() {
        rb = new JcToolsMpmcBackedQueue<>(size);
    }
}
