package com.github.srvaroa.queue;

import org.jctools.queues.MpmcArrayQueue;

public class JcToolsMpmcBackedQueue<T> implements Queue<T> {

    private final MpmcArrayQueue<T> q;

    public JcToolsMpmcBackedQueue(int capacity) {
        q = new MpmcArrayQueue<>(capacity);
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
