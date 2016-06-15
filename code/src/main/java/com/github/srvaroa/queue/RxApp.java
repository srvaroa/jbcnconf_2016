package com.github.srvaroa.queue;

import rx.subjects.PublishSubject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RxApp<T> extends SimpleApp<T> {

    /**
     * Allow our ThreadProducer to write to an Rx Subject.
     */
    static class RxQueueFacade<T> implements Queue<T> {

        PublishSubject<T> s = PublishSubject.create();

        @Override
        public T take() {
            throw new UnsupportedOperationException(); // Rx is push-based
        }

        @Override
        public boolean push(T t) {
            s.onNext(t);
            return true;
        }
    }

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        RxApp<Long> app = new RxApp<>();
        RxQueueFacade<Long> q = new RxQueueFacade<>();

        app.startProducers(3, q, latch, defaultSupplier);

        final Consumer<Long> avgReducer = new UnsafeAvgReducer();
        final Consumer<Long> moduloFilter = new UnsafeAvgReducer();

        RxQueueFacade<Long> rxQ = q;
        rxQ.s.subscribe(t -> avgReducer.accept(t));

        // You can make other subscriptions
        // rxQ.s.subscribe(t -> PRIME.accept(t));

        /* Try some RX operators */
        /*
        rxQ.s.sample(1, TimeUnit.SECONDS)
             .buffer(3)
             .subscribe(t -> System.out.println(t));
         */
        latch.countDown();
    }

}
