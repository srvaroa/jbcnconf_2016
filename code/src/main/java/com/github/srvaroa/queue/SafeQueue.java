package com.github.srvaroa.queue;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.atomic.AtomicLong;

/**
 * WIP: see TODO below
 */
public class SafeQueue<T> implements Queue<T> {

    public static final Unsafe unsafe;
    static {
        try {
            unsafe = AccessController.doPrivileged(
                (PrivilegedExceptionAction<Unsafe>) () -> {
                    final Field f = Unsafe.class.getDeclaredField("theUnsafe");
                    f.setAccessible(true);
                    return (Unsafe)f.get(null);
                });
        } catch (PrivilegedActionException e) {
            throw new RuntimeException("Unable to get Unsafe", e);
        }
    }

    public final Object[] buffer;
    public AtomicLong head = new AtomicLong(0);
    public AtomicLong tail = new AtomicLong(0);

    public SafeQueue(int capacity) {
        buffer = new Object[capacity];
        unsafe.fullFence();
    }

    @Override
    public T take() {
        long currHead;
        final long currTail = tail.get();
        do {
            currHead = head.get();
            if (currTail <= currHead)
                return null;
        } while(!head.compareAndSet(currHead, currHead + 1));
        // currHead is ours
        final int idx = (int)currHead % buffer.length;
        T t;
        do {
            unsafe.loadFence();
            t = (T)buffer[idx];
        } while (t == null);
        buffer[idx] = null;
        unsafe.storeFence();
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
        buffer[idx] = t;
        unsafe.storeFence();
        return true;
    }

}
