package chapter2;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

@State(Scope.Thread)
@Fork(value = 1, jvmArgs = {"-Xms256m", "-Xmx256m", "-XX:+UseZGC"})
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@Threads(4)
public class VarHandleBenchmark {

    // array option
    private final AtomicIntegerArray array = new AtomicIntegerArray(1);

    // vanilla AtomicInteger
    private final AtomicInteger counter = new AtomicInteger();

    // count field and its VarHandle
    private volatile int count;
    private static final VarHandle COUNT;

    // count2 field and its field updater
    private volatile int count2;
    private static final AtomicIntegerFieldUpdater<VarHandleBenchmark> COUNT2 ;

    static {
        try {

            COUNT = MethodHandles.lookup()
                    .findVarHandle(VarHandleBenchmark.class, "count", Integer.TYPE);
            COUNT2 = AtomicIntegerFieldUpdater.newUpdater(VarHandleBenchmark.class, "count2");
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(VarHandleBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }

    @Benchmark
    public void atomic(Blackhole bh) {
        bh.consume(counter.getAndAdd(1));
    }

    @Benchmark
    public void atomicArray(Blackhole bh) {
        bh.consume(array.getAndAdd(0, 1));
    }

    @Benchmark
    public void varhandle(Blackhole bh) {
        bh.consume((int)COUNT.getAndAdd(this, 1));
    }

    @Benchmark
    public void fieldUpdater(Blackhole bh) {
        bh.consume(COUNT2.getAndAdd(this, 1));
    }
}