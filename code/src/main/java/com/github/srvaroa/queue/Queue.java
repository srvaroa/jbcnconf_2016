package com.github.srvaroa.queue;

public interface Queue<T> {

    /**
     * Take an element.
     *
     * @return the element, or null if empty.
     */
    T take();

    /**
     * Add the given element.
     *
     * @return true if successful, false if full.
     */
    boolean push(T t);
}
