package com.github.srvaroa.queue;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork
public class JcToolsSpScQueueBenchmark {

    private Queue<Integer> buffer;

    @Setup
    public void setup() throws Exception {
        buffer = new JcToolsSpscBackedQueue<>(10000);
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

    public static void main(String args[]) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JcToolsSpScQueueBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                // Q: what are these for?
                // .verbosity(VerboseMode.EXTRA) //VERBOSE OUTPUT
                // .addProfiler(HotspotRuntimeProfiler.class)
                .build();
        new Runner(opt).run();
    }
}
