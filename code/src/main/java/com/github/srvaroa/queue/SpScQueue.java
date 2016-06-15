package com.github.srvaroa.queue;

import java.util.concurrent.atomic.AtomicLong;

public class SpScQueue<T> implements Queue<T> {

    public final Object[] buffer;
    public AtomicLong head = new AtomicLong(0);
    public AtomicLong tail = new AtomicLong(0);

    public SpScQueue(int capacity) {
        buffer = new Object[capacity];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = null;
        }
    }

    @Override
    public T take() {
        long currHead = head.get();
        if (tail.get() <= currHead)
            return null;
        // Compare with SafeAtomicRefQueue: no need here to coordinate with
        // other consumers.  Head is only accessed by a single thread.
        final int idx = (int)currHead % buffer.length;
        T t = (T)buffer[idx];
        buffer[idx] = null;

        // Question: do we need a CAS here? Any alternatives?
        head.compareAndSet(currHead, currHead + 1);
        return t;
    }

    @Override
    public boolean push(T t) {
        if (t == null)
            throw new NullPointerException("The item can't be null");

        final long currTail = tail.get();
        if (currTail - head.get() >= buffer.length)
            return false;

        // Compare with SafeAtomicRefQueue: no need here to coordinate with
        // other writers.  Tail is only accessed by a single thread.
        buffer[(int)currTail % buffer.length] = t;

        // Question: do we need a CAS here?
        tail.compareAndSet(currTail, currTail + 1);
        return true;
    }

}
