package com.github.alcereo;

import java.util.List;

public interface EventsService {

    default void addHistoryItem(HistoryItem item) {
        if (item instanceof CriticalEvent)
            addCriticalEvent((CriticalEvent) item);
        else if (item instanceof InfoEvent)
            addInfoEvent((InfoEvent) item);
        else if (item instanceof TransactionEvent)
            addTransactionEvent((TransactionEvent) item);
    }

    void addCriticalEvent(CriticalEvent event);

    void addInfoEvent(InfoEvent event);

    void addTransactionEvent(TransactionEvent event);


    default void addCriticalEventsList(List<CriticalEvent> events){
        events.forEach(this::addCriticalEvent);
    }

    default void addInfoEventsList(List<InfoEvent> events){
        events.forEach(this::addInfoEvent);
    }

    default void addTransactionEventsList(List<TransactionEvent> events){
        events.forEach(this::addTransactionEvent);
    }


    List<HistoryItem> getAllHistory(String atmId, long limit, long offset);

    List<CriticalEvent> getCriticalEvents(String atmId, long limit, long offset);

}
