package com.github.srvaroa.queue;

import org.junit.Before;

public class NotReallySafeQueueTest extends QueueSingleThreadedTest {
    @Before
    public void setup() {
        rb = new NotReallySafeQueue<>(size);
    }
}
