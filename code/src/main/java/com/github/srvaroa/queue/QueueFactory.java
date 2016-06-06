package com.github.srvaroa.queue;

import java.lang.reflect.InvocationTargetException;

public final class QueueFactory {

    public static <T> Queue<T> get(String name) throws
            ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException,
            InstantiationException {
        Class c = Class.forName("com.github.srvaroa.queue." + name);
        return (Queue<T>)c.getConstructor(Integer.TYPE).newInstance(1000);
    }
}
