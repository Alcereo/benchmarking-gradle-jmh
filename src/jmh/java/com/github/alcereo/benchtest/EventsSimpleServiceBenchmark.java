package com.github.alcereo.benchtest;

import com.github.alcereo.EventsService;
import com.github.alcereo.HistoryItem;
import com.github.alcereo.simple.EventsSimpleService;
import lombok.Data;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.ThreadParams;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.alcereo.benchtest.TestUtils.*;

@Threads(4)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
@Fork(value = 1)
public class EventsSimpleServiceBenchmark {

    @Param({"100", "1000", "100000", "1000000"})
    public int samplesCount;

    private EventsService service;

    @Setup
    public void setup(BenchmarkParams params){
        service = new EventsSimpleService();

        Arrays.<Supplier<List<? extends HistoryItem>>>asList(
                () -> buildCriticalEvents(samplesCount/3),
                () -> buildInfoEvents(samplesCount/3),
                () -> buildTransactionalEvents(samplesCount/3)
        ).parallelStream()
                .map(Supplier::get)
                .collect(Collectors.toList())
                .forEach(historyItems -> historyItems.forEach(service::addHistoryItem));

    }

    @Data
    @State(Scope.Thread)
    public static class RequestData {
        private int limit;
        private int offset;
        private String atmId;

        @Setup
        public void setup(ThreadParams threads) {
            Random random = new Random(System.currentTimeMillis());
            limit = random.nextInt(100);
            offset = random.nextInt(100);
            atmId = String.valueOf(random.nextInt(atmIdBound));
        }

        @Setup(Level.Invocation)
        public void setup(){
            limit = random.nextInt(100);
            offset = random.nextInt(100);
            atmId = String.valueOf(random.nextInt(atmIdBound));
        }

    }

    @Benchmark
    public void benchService(RequestData data) {
        List<HistoryItem> allHistory = service.getAllHistory(
                data.getAtmId(),
                data.getLimit(),
                data.getOffset()
        );
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + EventsSimpleServiceBenchmark.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

}
