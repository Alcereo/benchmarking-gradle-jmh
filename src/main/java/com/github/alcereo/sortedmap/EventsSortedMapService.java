package com.github.alcereo.sortedmap;


import com.github.alcereo.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventsSortedMapService implements EventsService {

    private int historySizeBound;
    private ConcurrentHashMap<String, BoundedSynchronisedSortedMapCache<HistoryItem>> allHsitoryCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, BoundedSynchronisedSortedMapCache<CriticalEvent>> criticalEventsCache = new ConcurrentHashMap<>();

    public EventsSortedMapService(int historySizeBound) {
        this.historySizeBound = historySizeBound;
    }

    @Override
    public void addCriticalEvent(CriticalEvent event) {
        allHsitoryCache.computeIfAbsent(
                event.getAtmId(),
                s -> new BoundedSynchronisedSortedMapCache<>(HistoryItem::compareTo, historySizeBound)
        ).addItem(event);

        criticalEventsCache.computeIfAbsent(
                event.getAtmId(),
                s -> new BoundedSynchronisedSortedMapCache<>(HistoryItem::compareTo, historySizeBound)
        ).addItem(event);
    }

    @Override
    public void addInfoEvent(InfoEvent event) {
        allHsitoryCache.computeIfAbsent(
                event.getAtmId(),
                s -> new BoundedSynchronisedSortedMapCache<>(HistoryItem::compareTo, historySizeBound)
        ).addItem(event);
    }

    @Override
    public void addTransactionEvent(TransactionEvent event) {
        allHsitoryCache.computeIfAbsent(
                event.getAtmId(),
                s -> new BoundedSynchronisedSortedMapCache<>(HistoryItem::compareTo, historySizeBound)
        ).addItem(event);
    }


    @Override
    public void addCriticalEventsList(List<CriticalEvent> events) {
        events.stream()
                .collect(Collectors.groupingBy(HistoryItem::getAtmId))
                .forEach(
                        (atmId, criticalEvents) -> {
                            allHsitoryCache.computeIfAbsent(
                                    atmId,
                                    s -> new BoundedSynchronisedSortedMapCache<>(HistoryItem::compareTo, historySizeBound)
                            ).addItemsList((List) criticalEvents);

                            criticalEventsCache.computeIfAbsent(
                                    atmId,
                                    s -> new BoundedSynchronisedSortedMapCache<>(HistoryItem::compareTo, historySizeBound)
                            ).addItemsList(criticalEvents);
                        }
                );
    }

    @Override
    public void addInfoEventsList(List<InfoEvent> events) {
        events.stream()
                .collect(Collectors.groupingBy(HistoryItem::getAtmId))
                .forEach(
                        (atmId, criticalEvents) -> {
                            allHsitoryCache.computeIfAbsent(
                                    atmId,
                                    s -> new BoundedSynchronisedSortedMapCache<>(HistoryItem::compareTo, historySizeBound)
                            ).addItemsList((List) criticalEvents);
                        }
                );
    }

    @Override
    public void addTransactionEventsList(List<TransactionEvent> events) {
        events.stream()
                .collect(Collectors.groupingBy(HistoryItem::getAtmId))
                .forEach(
                        (atmId, criticalEvents) -> {
                            allHsitoryCache.computeIfAbsent(
                                    atmId,
                                    s -> new BoundedSynchronisedSortedMapCache<>(HistoryItem::compareTo, historySizeBound)
                            ).addItemsList((List) criticalEvents);
                        }
                );
    }

    @Override
    public List<HistoryItem> getAllHistory(String atmId, long limit, long offset) {
        return allHsitoryCache.getOrDefault(
                atmId,
                new BoundedSynchronisedSortedMapCache<>(HistoryItem::compareTo, historySizeBound)
        ).getAll(limit, offset);
    }

    @Override
    public List<CriticalEvent> getCriticalEvents(String atmId, long limit, long offset) {
        return criticalEventsCache.getOrDefault(
                atmId,
                new BoundedSynchronisedSortedMapCache<>(HistoryItem::compareTo, historySizeBound)
        ).getAll(limit, offset);
    }

}
