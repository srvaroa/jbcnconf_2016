package com.github.srvaroa.queue;

public class SafeQueueConcurrentTest extends QueueConcurrentTest {
    @Override
    Queue<Integer> getInstance(int capacity) {
        return new SafeQueue<>(capacity);
    }
}
