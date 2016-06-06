package com.github.srvaroa.queue;

import org.junit.Before;

public class UnsafeQueueTest extends QueueSingleThreadedTest {
    @Before
    public void setup() {
        rb = new UnsafeQueue<>(size);
    }
}
