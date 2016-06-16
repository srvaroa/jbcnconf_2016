package com.github.srvaroa.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 4)
@Fork(5)
public class IterableBenchmark {

    private List<Integer> list;
    private final int capacity = 1000000;

    @Setup
    public void setup() {
        list = new ArrayList<>(capacity);
        int i = 0;
        while (i < capacity) {
            list.add(i);
            i++;
        }
    }

    @Benchmark
    @OperationsPerInvocation(100000)
    public void iterator(Blackhole bh) {
        long sum = 0;
        for (Integer n : list) {
            sum += n;
        }
        bh.consume(sum);
    }

    @Benchmark
    @OperationsPerInvocation(100000)
    public void walk(Blackhole bh) {
        long sum = 0;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            sum += list.get(i);
        }
        bh.consume(sum);
    }

}
