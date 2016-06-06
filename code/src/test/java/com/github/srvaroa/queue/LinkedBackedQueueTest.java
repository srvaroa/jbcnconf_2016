package com.github.srvaroa.queue;

import org.junit.Before;

public class LinkedBackedQueueTest extends QueueSingleThreadedTest {
    @Before
    public void setup() {
        rb = new LinkedBackedQueue<>(size);
    }
}
