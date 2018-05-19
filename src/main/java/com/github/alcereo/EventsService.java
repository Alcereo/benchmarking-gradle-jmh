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


    List<HistoryItem> getAllHistory(String atmId, long limit, long offset);

    List<CriticalEvent> getCriticalEvents(String atmId, long limit, long offset);

}
