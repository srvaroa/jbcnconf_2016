package com.github.srvaroa.queue;

import org.junit.Test;

import static org.junit.Assert.*;

public abstract class QueueSingleThreadedTest {

    int size = 10;
    Queue<Integer> rb;

    @Test
    public void empty() {
        assertNull(rb.take());
    }

    @Test(expected = NullPointerException.class)
    public void nullElement() {
        rb.push(null);
    }

    @Test
    public void happyCase() {
        assertTrue(rb.push(1));
        assertTrue(rb.push(2));
        assertEquals(Integer.valueOf(1), rb.take());
        assertEquals(Integer.valueOf(2), rb.take());
        assertNull(rb.take());
        assertNull(rb.take());
        assertTrue(rb.push(3));
        assertEquals(Integer.valueOf(3), rb.take());
        assertNull(rb.take());
    }

    @Test
    public void fullBuffer() {
        for (int i = 0; i < size; i++) {
            assertTrue(rb.push(i));
        }
        // Question: this fails on JcToolsMpmcBackedQueue. Why?
        assertFalse(rb.push(size + 1));
        assertFalse(rb.push(size + 1));
        for (int i = 0; i < size; i++) {
            assertEquals(Integer.valueOf(i), rb.take());
        }
    }

}

