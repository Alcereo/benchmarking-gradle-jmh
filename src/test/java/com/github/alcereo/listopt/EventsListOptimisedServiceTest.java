package com.github.alcereo.listopt;

import com.github.alcereo.CriticalEvent;
import com.github.alcereo.HistoryItem;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.alcereo.simple.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class EventsListOptimisedServiceTest {

    private EventsListOptimisedService service;
    private static List<HistoryItem> history = new ArrayList<>();
    private int limit;
    private int offset;
    private String atmId;

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

    @BeforeEach
    void setUp() {
        service = new EventsListOptimisedService();
        limit = random.nextInt(50);
        offset = random.nextInt(100);
        atmId = String.valueOf(random.nextInt(atmIdBound));
    }

    @DisplayName("Simple test for adding")
    @Test
    void simpleAddingTest() {

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
    @Test
    void getAllLimitOffsetTest() {

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
    @Test
    void getCriticalTest() {

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
    @Test
    void getCriticalLimitOffsetTest() {

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