package com.github.srvaroa.queue;

import java.util.concurrent.atomic.AtomicLong;

public class NotReallySafeQueue<T> implements Queue<T> {

    public final Object[] buffer;
    public AtomicLong head = new AtomicLong(0);
    public AtomicLong tail = new AtomicLong(0);

    public NotReallySafeQueue(int capacity) {
        this.buffer = new Object[capacity];
    }

    @Override
    public T take() {
        long currTail;
        long currHead;
        do {
            currTail = tail.get();
            currHead = head.get();
            if (currTail - currHead <= 0)
                return null;
        } while(!head.compareAndSet(currHead, currHead + 1));
        final int idx = (int)currHead % buffer.length;
        T t = (T)buffer[idx];
        buffer[idx] = null;           // leaky otherwise
        return t;
    }

    @Override
    public boolean push(T t) {
        if (t == null)
            throw new NullPointerException("The item can't be null");

        final long currHead = head.get();
        long currTail;
        do {
            currTail = tail.get();
            if (currTail - currHead >= buffer.length)
                return false;
        } while(!tail.compareAndSet(currTail, currTail + 1));
        buffer[(int)currTail % buffer.length] = t;
        return true;
    }

}
