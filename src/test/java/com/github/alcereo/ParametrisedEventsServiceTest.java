package com.github.alcereo;

import com.github.alcereo.listopt.EventsListOptimisedService;
import com.github.alcereo.simple.EventsSimpleService;
import com.github.alcereo.sortedmap.EventsSortedMapService;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.alcereo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DisplayName("EvensService classes")
class ParametrisedEventsServiceTest {

    private static List<HistoryItem> history = new ArrayList<>();

    @BeforeAll
    static void setUpHistory() {

        Arrays.<Supplier<List<? extends HistoryItem>>>asList(
                () -> buildCriticalEvents(1000),
                () -> buildInfoEvents(1000),
                () -> buildTransactionalEvents(1000)
        ).parallelStream()
                .map(Supplier::get)
                .collect(Collectors.toList())
                .forEach(history::addAll);

    }

    private static Stream<Arguments> parametersFactory() {
        String atmId = String.valueOf(random.nextInt(atmIdBound));
        int limit = random.nextInt(50);
        int offset = random.nextInt(100);

        return Stream.of(
                Arguments.of(
                        new EventsSimpleService(),
                        atmId,
                        limit,
                        offset
                ),
                Arguments.of(
                        new EventsListOptimisedService(Integer.MAX_VALUE),
                        atmId,
                        limit,
                        offset
                ),
                Arguments.of(
                        new EventsSortedMapService(Integer.MAX_VALUE),
                        atmId,
                        limit,
                        offset
                )
        );
    }

    @DisplayName("Simple test for adding")
    @ParameterizedTest
    @MethodSource("parametersFactory")
    void simpleAddingTest(EventsService service,
                          String atmId,
                          Integer limit,
                          Integer offset
    ) {

        val testList = history.stream()
                .sorted()
                .filter(item -> item.getAtmId().equals(atmId))
                .limit(10)
                .collect(Collectors.toList());

        testList.forEach(service::addHistoryItem);

        assertIterableEquals(
                testList,
                service.getAllHistory(atmId, 10, 0)
        );

    }

    @DisplayName("Test getAll - limit/offset")
    @ParameterizedTest
    @MethodSource("parametersFactory")
    void getAllLimitOffsetTest(EventsService service,
                               String atmId,
                               Integer limit,
                               Integer offset
    ) {

        history.forEach(service::addHistoryItem);

        Iterable<?> expectedList = history.stream()
                .filter(item -> item.getAtmId().equals(atmId))
                .sorted()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        assertIterableEquals(
                expectedList,
                service.getAllHistory(atmId, limit, offset)
        );

    }

    @DisplayName("Test simple getCritical")
    @ParameterizedTest
    @MethodSource("parametersFactory")
    void getCriticalTest(EventsService service,
                         String atmId,
                         Integer limit,
                         Integer offset
    ) {

        limit = 10;
        offset = 0;

        history.forEach(service::addHistoryItem);

        Iterable<?> expectedList = history.stream()
                .filter(item -> item.getAtmId().equals(atmId))
                .sorted()
                .filter(item -> item instanceof CriticalEvent)
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        assertIterableEquals(
                        expectedList,
                        service.getCriticalEvents(atmId, limit, offset)
                );
    }

    @DisplayName("Test getCritical - offset/limit")
    @ParameterizedTest
    @MethodSource("parametersFactory")
    void getCriticalLimitOffsetTest(
            EventsService service,
            String atmId,
            Integer limit,
            Integer offset
    ) {

        history.forEach(service::addHistoryItem);

        Iterable<?> expectedList = history.stream()
                .filter(item -> item.getAtmId().equals(atmId))
                .filter(item -> item instanceof CriticalEvent)
                .sorted()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        assertIterableEquals(
                expectedList,
                service.getCriticalEvents(atmId, limit, offset)
        );

    }
}