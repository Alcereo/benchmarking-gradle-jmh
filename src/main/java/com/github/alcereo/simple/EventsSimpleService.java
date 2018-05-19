package com.github.alcereo.simple;

import com.github.alcereo.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventsSimpleService implements EventsService {

    private ConcurrentHashMap<String, Collection<HistoryItem>> allHsitoryCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Collection<CriticalEvent>> criticalEventsCache = new ConcurrentHashMap<>();

    @Override
    public void addCriticalEvent(CriticalEvent event) {
        final Collection<HistoryItem> historyItems = allHsitoryCache.computeIfAbsent(event.getAtmId(), s -> new PriorityQueue<>());
        final Collection<CriticalEvent> criticalHistoryItems = criticalEventsCache.computeIfAbsent(event.getAtmId(), s -> new PriorityQueue<>());

        synchronized (historyItems){
            historyItems.add(event);
        }

        synchronized (criticalHistoryItems){
            criticalHistoryItems.add(event);
        }
    }

    @Override
    public void addInfoEvent(InfoEvent event) {
        final Collection<HistoryItem> historyItems = allHsitoryCache.computeIfAbsent(event.getAtmId(), s -> new PriorityQueue<>());
        synchronized (historyItems){
            historyItems.add(event);
        }
    }

    @Override
    public void addTransactionEvent(TransactionEvent event) {
        final Collection<HistoryItem> historyItems = allHsitoryCache.computeIfAbsent(event.getAtmId(), s -> new PriorityQueue<>());
        synchronized (historyItems){
            historyItems.add(event);
        }
    }

    @Override
    public List<HistoryItem> getAllHistory(String atmId, long limit, long offset) {
        List<HistoryItem> result;

        Collection<HistoryItem> orDefault = allHsitoryCache.getOrDefault(atmId, new ArrayList<>());

        synchronized (orDefault){
            result = orDefault.stream()
                    .sorted()
                    .skip(offset)
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    public List<CriticalEvent> getCriticalEvents(String atmId, long limit, long offset) {
        List<CriticalEvent> result;

        Collection<CriticalEvent> orDefault = criticalEventsCache.getOrDefault(atmId, new ArrayList<>());

        synchronized (orDefault){
            result = orDefault.stream()
                    .sorted()
                    .skip(offset)
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return result;
    }
}
