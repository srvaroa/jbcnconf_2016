package com.github.srvaroa.queue;

public class UnsafeQueue<T> implements Queue<T> {

    public final Object[] buffer;
    public long head = 0;           // read pointer
    public long tail = 0;           // write pointer

    public UnsafeQueue(int capacity) {
        this.buffer = new Object[capacity];
    }

    @Override
    public T take() {
        if (tail - head <= 0)
            return null;        // empty
        long pos = head;
        head = head + 1;
        return (T)buffer[(int)pos % buffer.length];
    }

    @Override
    public boolean push(T t) {
        if (t == null)
            throw new NullPointerException("The item can't be null");
        if (tail - head >= buffer.length)
            return false;
        final int pos = (int)tail % buffer.length;
        tail = (tail + 1);
        buffer[pos] = t;
        return true;
    }

}
