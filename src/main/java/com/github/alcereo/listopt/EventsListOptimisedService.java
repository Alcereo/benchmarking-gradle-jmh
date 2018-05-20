package com.github.alcereo.listopt;

import com.github.alcereo.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
