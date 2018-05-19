package com.github.alcereo.benchtest;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.ThreadParams;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@State(Scope.Benchmark)
public class Application {

    private static int count = 1000000;

    private ConcurrentHashMap<String, String> testedMap;

    @Setup
    public void setup(BenchmarkParams params){
        testedMap = new ConcurrentHashMap<>(
                count,
                0.75f,
                1
        );
    }


    @State(Scope.Thread)
    public static class Keygen {
        private String[] keys;

        @Setup
        public void setup(ThreadParams threads) {

            keys = new String[count];
            for (int i = 0; i < count; i++) {
                System.out.printf("\r - %d", i);
                keys[i] = UUID.randomUUID().toString();
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Threads(4)
    @Warmup(
            iterations = 1
    )
    @Measurement(
            iterations = 1
    )
    public void benchTest(Keygen keys) {
        for (String key : keys.keys) {
            testedMap.remove(key);
            testedMap.put(key, key);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + Application.class.getSimpleName() + ".*")
//                .mode(Mode.All)
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();

    }

}
