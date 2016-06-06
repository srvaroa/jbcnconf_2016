package com.github.srvaroa.queue;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class SafeAtomicRefQueue<T> implements Queue<T> {

    public final AtomicReference<T>[] buffer;
    public AtomicLong head = new AtomicLong(0);
    public AtomicLong tail = new AtomicLong(0);

    public SafeAtomicRefQueue(int capacity) {
        buffer = new AtomicReference[capacity];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = new AtomicReference(null);
        }
    }

    @Override
    public T take() {
        long currHead;
        do {
            currHead = head.get();
            if (tail.get() <= currHead)
                return null;
        } while(!head.compareAndSet(currHead, currHead + 1));
        // currHead is ours
        final int idx = (int)currHead % buffer.length;
        T t;
        do {
            t = buffer[idx].getAndSet(null);
        } while (t == null);
        return t;
    }

    @Override
    public boolean push(T t) {
        if (t == null)
            throw new NullPointerException("The item can't be null");

        long currTail;
        final long currHead = head.get();
        do {
            currTail = tail.get();
            if (currTail - currHead >= buffer.length)
                return false;
        } while(!tail.compareAndSet(currTail, currTail + 1));
        final int idx = (int)currTail % buffer.length;

        while(!buffer[idx].compareAndSet(null, t)); // spin

        return true;
    }

}
