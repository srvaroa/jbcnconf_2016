package com.github.srvaroa.queue;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.HotspotRuntimeProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork
public class QueueBenchmark {

    @Param({
        "UnsafeQueue",
        "SynchronizedQueue",
        "LinkedBackedQueue",
        "NotReallySafeQueue",
        "SafeAtomicRefQueue",
        "SafeQueue",
        "JcToolsMmpcBackedQueue"
    })
    public String impl;

    private Queue<Integer> buffer;

    @Setup
    public void setup() throws Exception {
        buffer = QueueFactory.get(impl, 10000);
    }

    private void produce() {
        for (int i = 0; i < 10; i++) {
            buffer.push(i);
        }
    }

    private void consume(Blackhole bh) {
        for (int i = 0; i < 10; i++) {
            bh.consume(buffer.take());
        }
    }

    @Benchmark
    @Group("g1")
    @GroupThreads(1)
    @OperationsPerInvocation(10)
    public void producer1() {
        produce();
    }

    @Benchmark
    @Group("g1")
    @GroupThreads(1)
    @OperationsPerInvocation(10)
    public void consumer1(Blackhole bh) {
        consume(bh);
    }

    @Benchmark
    @Group("g2")
    @GroupThreads(4)
    @OperationsPerInvocation(10)
    public void producer4() {
        produce();
    }

    @Benchmark
    @Group("g2")
    @GroupThreads(4)
    @OperationsPerInvocation(10)
    public void consumer4(Blackhole bh) {
        consume(bh);
    }

    @Benchmark
    @Group("g3")
    @GroupThreads(8)
    @OperationsPerInvocation(10)
    public void producer8() {
        produce();
    }

    @Benchmark
    @Group("g3")
    @GroupThreads(8)
    @OperationsPerInvocation(10)
    public void consumer8(Blackhole bh) {
        consume(bh);
    }

    @Benchmark
    @Group("g4")
    @GroupThreads(16)
    @OperationsPerInvocation(10)
    public void producer16() {
        produce();
    }

    @Benchmark
    @Group("g4")
    @GroupThreads(16)
    @OperationsPerInvocation(10)
    public void consumer16(Blackhole bh) {
        consume(bh);
    }

    // Run from IDE, gradle as ./gradlew jmh, or
    // java -cp ./build/libs/jbcnconf-0.1-SNAPSHOT-jmh.jar  com.github.srvaroa.queue.QueueBenchmark
    public static void main(String args[]) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(QueueBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                // Q: what are these for?
                // .verbosity(VerboseMode.EXTRA) //VERBOSE OUTPUT
                // .addProfiler(HotspotRuntimeProfiler.class)
                .build();
        new Runner(opt).run();
    }
}
