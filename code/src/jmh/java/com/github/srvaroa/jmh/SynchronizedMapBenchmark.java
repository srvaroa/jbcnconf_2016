package com.github.srvaroa.jmh;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.HotspotRuntimeProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SynchronizedMapBenchmark {

    public static final int MAX = 5000000;

    public Map<Long, Long> map;

    @Param({"10", "100", "10000", "100000"})
    public int capacity;

    @Param({"SYNC", "NO_SYNC"})
    public String implementation;

    @Setup
    public void setup(BenchmarkParams params) {
        map = ("SYNC".equals(implementation))
                ? Collections.synchronizedMap(new HashMap<>(capacity, 0.75f))
                : new ConcurrentHashMap<>(capacity, 0.75f, params.getThreads());
        for  (long i = 0; i < MAX / 2; i++) {
            long n = (long)(Math.random() * MAX);
            map.put(i, n);
        }
    }

    private void produce() {
        long n = (long)(Math.random() * MAX);
        map.put(n, n+1);
    }

    private void consume(Blackhole bh) {
        long n = (long)(Math.random() * MAX);
        bh.consume(map.remove(n));
    }

    @Benchmark
    @Group("g1")
    @GroupThreads(1)
    public void producer1() {
        produce();
    }

    @Benchmark
    @Group("g1")
    @GroupThreads(1)
    public void consumer1(Blackhole bh) {
        consume(bh);
    }

    @Benchmark
    @Group("g2")
    @GroupThreads(4)
    public void producer4() {
        produce();
    }

    @Benchmark
    @Group("g2")
    @GroupThreads(4)
    public void consumer4(Blackhole bh) {
        consume(bh);
    }

    @Benchmark
    @Group("g3")
    @GroupThreads(8)
    public void producer8() {
        produce();
    }

    @Benchmark
    @Group("g3")
    @GroupThreads(8)
    public void consumer8(Blackhole bh) {
        consume(bh);
    }

    @Benchmark
    @Group("g4")
    @GroupThreads(16)
    public void producer16() {
        produce();
    }

    @Benchmark
    @Group("g4")
    @GroupThreads(16)
    public void consumer16(Blackhole bh) {
        consume(bh);
    }

    public static final void main(String args[]) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SynchronizedMapBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .verbosity(VerboseMode.EXTRA) //VERBOSE OUTPUT
                .addProfiler(HotspotRuntimeProfiler.class)
                .build();
        new Runner(opt).run();
    }
}
