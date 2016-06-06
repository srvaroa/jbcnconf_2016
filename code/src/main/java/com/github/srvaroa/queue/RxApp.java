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

    /*
     Not totally sure this is necessary, I think the PublishSubject serializes
     notifications.
    static class SafeAvgReducer implements Consumer<Integer> {
        LongAdder sum = new LongAdder();
        AtomicLong count = new AtomicLong();
        @Override
        public void accept(Integer i) {
            long _count = count.incrementAndGet();
            if (_count > 0 && _count % BATCH_SIZE == 0) {
                int idx = (int)(_count / BATCH_SIZE);
                double val = sum.doubleValue() / _count;
                System.out.println(String.format("%d %f", idx, val));
                sum.reset();
            } else {
                sum.add(i);
            }
        }
    }
    */

    @Override
    public void startWorkers(int n, Queue<T> q, CountDownLatch latch,
                             Consumer<T> c) {
        RxQueueFacade<T> rxQ = (RxQueueFacade<T>)q;
        rxQ.s.subscribe(t -> c.accept(t));
        /* Try some RX operators */
        /*
        rxQ.s.sample(1, TimeUnit.SECONDS)
             .buffer(3)
             .subscribe(t -> System.out.println(t));
         */
    }

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        RxApp<Long> app = new RxApp<>();
        RxQueueFacade<Long> q = new RxQueueFacade<>();

        app.startProducers(3, q, latch, defaultSupplier);
        app.startWorkers(3, q, latch, new UnsafeAvgReducer());
        latch.countDown();
    }

}
