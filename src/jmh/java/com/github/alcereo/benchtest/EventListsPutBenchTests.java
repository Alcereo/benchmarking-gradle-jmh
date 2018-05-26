package com.github.alcereo.benchtest;

import com.github.alcereo.CriticalEvent;
import com.github.alcereo.EventsService;
import com.github.alcereo.InfoEvent;
import com.github.alcereo.TransactionEvent;
import com.github.alcereo.listopt.EventsListOptimisedService;
import com.github.alcereo.sortedmap.EventsSortedMapService;
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

import static com.github.alcereo.benchtest.TestUtils.*;

@Threads(4)
@State(Scope.Benchmark)
@Fork(value = 1)
public class EventListsPutBenchTests {

    @Param({"100", "1000", "10000", "100000", "1000000"})
    private static int samplesCount;


    private static final String LIST_OPT = "list-opt";
    private static final String SET_OPT = "set-opt";

    @Param({LIST_OPT, SET_OPT})
    private static String serviceType;

    private static EventsService service;

    @Setup
    public void setup(BenchmarkParams params){
        switch (serviceType){
            case LIST_OPT:
                service = new EventsListOptimisedService(100000);
                break;
            case SET_OPT:
                service = new EventsSortedMapService(100000);
                break;
        }
    }

    @Data
    @State(Scope.Thread)
    public static class RequestData {

        private List<CriticalEvent> criticalEvents;
        private List<InfoEvent> infoEvents;
        private List<TransactionEvent> transactionEvents;

        @Setup
        public void setup(ThreadParams threads) {
            Instant now = Instant.now();

            criticalEvents = buildCriticalEvents(samplesCount / 3, now);
            infoEvents = buildInfoEvents(samplesCount / 3, now);
            transactionEvents = buildTransactionalEvents(samplesCount / 3, now);

        }

        @Setup(Level.Invocation)
        public void eraseService(){
            switch (serviceType){
                case LIST_OPT:
                    service = new EventsListOptimisedService(100000);
                    break;
                case SET_OPT:
                    service = new EventsSortedMapService(100000);
                    break;
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 3)
    @Timeout(time = 20)
    public void benchPutNewList(RequestData data) {
        service.addInfoEventsList(data.getInfoEvents());
        service.addCriticalEventsList(data.getCriticalEvents());
        service.addTransactionEventsList(data.getTransactionEvents());
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + EventListsPutBenchTests.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

}
