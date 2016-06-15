package com.github.srvaroa.queue;

import org.junit.Before;

public class SpScQueueTest extends QueueSingleThreadedTest {
    @Before
    public void setup() {
        rb = new SpScQueue<>(size);
    }
}
