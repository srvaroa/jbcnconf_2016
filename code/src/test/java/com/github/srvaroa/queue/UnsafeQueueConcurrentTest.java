package com.github.srvaroa.queue;

public class UnsafeQueueConcurrentTest extends QueueConcurrentTest {
    @Override
    Queue<Long> getInstance(int capacity) {
        return new UnsafeQueue<>(capacity);
    }
}
