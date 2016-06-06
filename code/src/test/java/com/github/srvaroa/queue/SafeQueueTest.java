package com.github.srvaroa.queue;

import org.junit.Before;

public class SafeQueueTest extends QueueSingleThreadedTest {
    @Before
    public void setup() {
        rb = new SafeQueue<>(size);
    }
}
