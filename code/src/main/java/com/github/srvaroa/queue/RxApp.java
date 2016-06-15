package com.github.srvaroa.queue;

import com.github.srvaroa.ThreadConsumer;
import com.github.srvaroa.ThreadConsumer.UnsafeAvgReducer;

import com.github.srvaroa.ThreadProducer;
import rx.subjects.PublishSubject;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static com.github.srvaroa.queue.Common.*;

/**
 * A simple app that has N producers inject data into an Observable.
 */
public class RxApp<T> {

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
        RxQueueFacade<Long> q = new RxQueueFacade<>();
        ThreadProducer[] producers = startProducers(3, q, latch, defaultSupplier);

        final Consumer<Long> avgReducer = new UnsafeAvgReducer();
        final RxQueueFacade<Long> rxQ = q;
        rxQ.s.subscribe(t -> avgReducer.accept(t));

        // You can make other subscriptions
        // final Consumer<Long> moduloFilter = new UnsafeAvgReducer();
        // rxQ.s.subscribe(t -> moduloFilter.accept(t));

        /* Try some RX operators */
        /*
        rxQ.s.sample(1, TimeUnit.SECONDS)
             .buffer(3)
             .subscribe(t -> System.out.println(t));
         */

        addShutdownHook(producers, new ThreadConsumer[0]);
        latch.countDown();
    }

}
