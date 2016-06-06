package com.github.srvaroa.queue;

public class SynchronizedQueueConcurrentTest extends QueueConcurrentTest {
    @Override
    Queue<Integer> getInstance(int capacity) {
        return new SynchronizedQueue<>(capacity);
    }
}
