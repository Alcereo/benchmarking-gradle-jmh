package com.github.alcereo.listopt;

import com.github.alcereo.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventsListOptimisedService implements EventsService {

    private ConcurrentHashMap<String, LinkedList<HistoryItem>> allHsitoryCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, LinkedList<CriticalEvent>> criticalEventsCache = new ConcurrentHashMap<>();

    @Override
    public void addCriticalEvent(CriticalEvent event) {
        final LinkedList<HistoryItem> historyItems = allHsitoryCache.computeIfAbsent(event.getAtmId(), s -> new LinkedList<>());
        final LinkedList<CriticalEvent> criticalHistoryItems = criticalEventsCache.computeIfAbsent(event.getAtmId(), s -> new LinkedList<>());

        synchronized (historyItems){
            addEventToHistory(historyItems, event);
        }

        synchronized (criticalHistoryItems){
            addEventToHistory(criticalHistoryItems,event);
        }
    }

    private static <T extends HistoryItem> void addEventToHistory(LinkedList<T> historyItems, T event) {
        // We can keep the list sorted for example if we put each event
        // in the right position in the list when adding


        if (historyItems.isEmpty() || historyItems.getFirst().compareTo(event) > 0) {
            historyItems.addFirst(event);
            return;
        }

        for (int index = 0; index < historyItems.size(); index++) {
            if (historyItems.get(index).compareTo(event) > 0) {
                historyItems.add(index, event);
                return;
            }
        }

        historyItems.addLast(event);

    }

    @Override
    public void addInfoEvent(InfoEvent event) {
        final LinkedList<HistoryItem> historyItems = allHsitoryCache.computeIfAbsent(event.getAtmId(), s -> new LinkedList<>());
        synchronized (historyItems){
            addEventToHistory(historyItems, event);
        }
    }

    @Override
    public void addTransactionEvent(TransactionEvent event) {
        final LinkedList<HistoryItem> historyItems = allHsitoryCache.computeIfAbsent(event.getAtmId(), s -> new LinkedList<>());
        synchronized (historyItems){
            addEventToHistory(historyItems, event);
        }
    }

    @Override
    public List<HistoryItem> getAllHistory(String atmId, long limit, long offset) {
        List<HistoryItem> result;

        Collection<HistoryItem> orDefault = allHsitoryCache.getOrDefault(atmId, new LinkedList<>());

        synchronized (orDefault){
            result = orDefault.stream()
                    .skip(offset)
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    public List<CriticalEvent> getCriticalEvents(String atmId, long limit, long offset) {
        List<CriticalEvent> result;

        Collection<CriticalEvent> orDefault = criticalEventsCache.getOrDefault(atmId, new LinkedList<>());

        synchronized (orDefault){
            result = orDefault.stream()
                    .skip(offset)
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return result;
    }
}
