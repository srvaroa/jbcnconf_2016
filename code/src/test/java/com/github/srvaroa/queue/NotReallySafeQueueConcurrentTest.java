package com.github.srvaroa.queue;

public class NotReallySafeQueueConcurrentTest extends QueueConcurrentTest {
    @Override
    Queue<Integer> getInstance(int capacity) {
        return new NotReallySafeQueue<>(capacity);
    }
}
