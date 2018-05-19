package com.github.alcereo;

import java.util.List;

public interface EventsService {

    void addHistoryItem(HistoryItem item);

    void addCriticalEvent(CriticalEvent event);

    void addInfoEvent(InfoEvent event);

    void addTransactionEvent(TransactionEvent event);


    List<HistoryItem> getAllHistory(String atmId, long limit, long offset);

    List<CriticalEvent> getCriticalEvents(String atmId, long limit, long offset);

}
