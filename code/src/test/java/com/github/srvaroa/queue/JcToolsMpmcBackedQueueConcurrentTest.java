package com.github.srvaroa.queue;

/**
 * Expected failure in fullBuffer.  Why?
 */
public class JcToolsMpmcBackedQueueConcurrentTest extends QueueConcurrentTest {
    @Override
    Queue<Integer> getInstance(int capacity) {
        return new JcToolsMpmcBackedQueue<>(capacity);
    }
}
