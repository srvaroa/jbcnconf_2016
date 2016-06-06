package com.github.srvaroa.queue;

public class SafeAtomicRefQueueConcurrentTest extends QueueConcurrentTest {
    @Override
    Queue<Integer> getInstance(int capacity) {
        return new SafeAtomicRefQueue<>(capacity);
    }
}
