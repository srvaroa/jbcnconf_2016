package com.github.srvaroa.queue;

public class SafeAtomicRefQueueConcurrentTest extends QueueConcurrentTest {
    @Override
    Queue<Long> getInstance(int capacity) {
        return new SafeAtomicRefQueue<>(capacity);
    }
}
