package com.github.srvaroa.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.srvaroa.akka.AkkaRun;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.SampleTime)
@Warmup(iterations = 1)
@Fork(5)
public class AkkaBenchmark {
    @Benchmark
    @OperationsPerInvocation(1)
    public void akka(Blackhole bh) {
        AkkaRun instance = new AkkaRun();
        instance.run();
    }
}
