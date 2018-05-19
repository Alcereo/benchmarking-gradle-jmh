package com.github.alcereo.benchtest;

import com.github.alcereo.*;
import com.github.alcereo.listopt.EventsListOptimisedService;
import lombok.Data;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.ThreadParams;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Random;

import static com.github.alcereo.benchtest.TestUtils.*;

@Threads(4)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
@Fork(value = 1)
public class EventsListOptimisedServiceBenchmark {

    @Param({"100", "1000", "100000", "1000000"})
    public int samplesCount;

    private EventsService service;

    @Setup
    public void setup(BenchmarkParams params){

        service = new EventsListOptimisedService();

        Instant now = Instant.now();

        List<CriticalEvent> criticalEvents = buildCriticalEvents(samplesCount / 3, now);
        List<InfoEvent> infoEvents = buildInfoEvents(samplesCount / 3, now);
        List<TransactionEvent> transactionEvents = buildTransactionalEvents(samplesCount / 3, now);

        System.out.println("Start fulling service");
        for (int i = 0; i < samplesCount / 3; i++) {
            service.addHistoryItem(criticalEvents.get(i));
            service.addHistoryItem(infoEvents.get(i));
            service.addHistoryItem(transactionEvents.get(i));
            System.out.print("\rsample = " + i);
        }
        System.out.println();

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
                .include(".*" + EventsListOptimisedServiceBenchmark.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

}
