package com.github.srvaroa.queue;

public class SynchronizedQueueConcurrentTest extends QueueConcurrentTest {
    @Override
    Queue<Long> getInstance(int capacity) {
        return new SynchronizedQueue<>(capacity);
    }
}
