package com.github.srvaroa.queue;

import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBackedQueue<T> implements Queue<T> {

    private final LinkedBlockingQueue<T> q;

    public LinkedBackedQueue(int capacity) {
        q = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public synchronized T take() {
        return q.poll();
    }

    @Override
    public synchronized boolean push(T t) {
        return q.offer(t);
    }

}
