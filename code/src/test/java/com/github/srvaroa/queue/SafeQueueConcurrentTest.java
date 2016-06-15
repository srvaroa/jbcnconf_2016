package com.github.srvaroa.queue;

public class SafeQueueConcurrentTest extends QueueConcurrentTest {
    @Override
    Queue<Long> getInstance(int capacity) {
        return new SafeQueue<>(capacity);
    }
}
