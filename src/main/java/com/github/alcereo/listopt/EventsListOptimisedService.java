package com.github.alcereo.listopt;

import com.github.alcereo.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventsListOptimisedService implements EventsService {

    private int historySizeBound;
    private ConcurrentHashMap<String, BoundedSynchronisedSortedCacheList<HistoryItem>> allHsitoryCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, BoundedSynchronisedSortedCacheList<CriticalEvent>> criticalEventsCache = new ConcurrentHashMap<>();

    public EventsListOptimisedService(int historySizeBound) {
        this.historySizeBound = historySizeBound;
    }

    @Override
    public void addCriticalEvent(CriticalEvent event) {
        allHsitoryCache.computeIfAbsent(
                event.getAtmId(),
                s -> new BoundedSynchronisedSortedCacheList<>(HistoryItem::compareTo, historySizeBound)
        ).addItem(event);

        criticalEventsCache.computeIfAbsent(
                event.getAtmId(),
                s -> new BoundedSynchronisedSortedCacheList<>(HistoryItem::compareTo, historySizeBound)
        ).addItem(event);
    }

    @Override
    public void addInfoEvent(InfoEvent event) {
        allHsitoryCache.computeIfAbsent(
                event.getAtmId(),
                s -> new BoundedSynchronisedSortedCacheList<>(HistoryItem::compareTo, historySizeBound)
        ).addItem(event);
    }

    @Override
    public void addTransactionEvent(TransactionEvent event) {
        allHsitoryCache.computeIfAbsent(
                event.getAtmId(),
                s -> new BoundedSynchronisedSortedCacheList<>(HistoryItem::compareTo, historySizeBound)
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
                                    s -> new BoundedSynchronisedSortedCacheList<>(HistoryItem::compareTo, historySizeBound)
                            ).addItemList((List) criticalEvents);

                            criticalEventsCache.computeIfAbsent(
                                    atmId,
                                    s -> new BoundedSynchronisedSortedCacheList<>(HistoryItem::compareTo, historySizeBound)
                            ).addItemList(criticalEvents);
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
                                    s -> new BoundedSynchronisedSortedCacheList<>(HistoryItem::compareTo, historySizeBound)
                            ).addItemList((List) criticalEvents);
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
                                    s -> new BoundedSynchronisedSortedCacheList<>(HistoryItem::compareTo, historySizeBound)
                            ).addItemList((List) criticalEvents);
                        }
                );
    }

    @Override
    public List<HistoryItem> getAllHistory(String atmId, long limit, long offset) {
        return allHsitoryCache.getOrDefault(
                atmId,
                new BoundedSynchronisedSortedCacheList<>(HistoryItem::compareTo, historySizeBound)
        ).getAll(limit, offset);
    }

    @Override
    public List<CriticalEvent> getCriticalEvents(String atmId, long limit, long offset) {
        return criticalEventsCache.getOrDefault(
                atmId,
                new BoundedSynchronisedSortedCacheList<>(HistoryItem::compareTo, historySizeBound)
        ).getAll(limit, offset);
    }
}
