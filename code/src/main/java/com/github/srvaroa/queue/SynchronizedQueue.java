package com.github.srvaroa.queue;

public class SynchronizedQueue<T> implements Queue<T> {

    public final Object[] buffer;
    public long head = 0;           // read pointer
    public long tail = 0;           // write pointer

    public SynchronizedQueue(int capacity) {
        this.buffer = new Object[capacity];
    }

    @Override
    public synchronized T take() {
        if (tail - head <= 0)
            return null;        // empty
        long pos = head;
        head = head + 1;
        return (T)buffer[(int)(pos % buffer.length)];
    }

    @Override
    public synchronized boolean push(T t) {
        if (t == null)
            throw new NullPointerException("The item can't be null");
        if (tail - head >= buffer.length)
            return false;
        final long pos = tail % buffer.length;
        tail = (tail + 1);
        buffer[(int)pos] = t;
        return true;
    }

}
