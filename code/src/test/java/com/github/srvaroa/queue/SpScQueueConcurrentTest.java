package com.github.srvaroa.queue;

public class SpScQueueConcurrentTest extends QueueConcurrentTest {
    @Override
    Queue<Long> getInstance(int capacity) {
        return new SpScQueue<>(capacity);
    }
}
