package com.github.srvaroa.queue;

import org.jctools.queues.SpscArrayQueue;

public class JcToolsSpscBackedQueue<T> implements Queue<T> {

    private final SpscArrayQueue<T> q;

    public JcToolsSpscBackedQueue(int capacity) {
        q = new SpscArrayQueue<>(capacity);
    }

    @Override
    public T take() {
        return q.poll();
    }

    @Override
    public boolean push(T t) {
        return q.offer(t);
    }

}
