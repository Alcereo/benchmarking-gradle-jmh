package com.github.alcereo.redis;

import com.github.alcereo.*;
import io.lettuce.core.api.reactive.RedisReactiveCommands;

import java.util.List;

public class EventsRedisService implements EventsService {

    private final RedisReactiveCommands<String, String> redisCommands;

    public EventsRedisService(RedisReactiveCommands<String, String> redisCommands) {
        this.redisCommands = redisCommands;
    }

    @Override
    public void addCriticalEvent(CriticalEvent event) {
//        redisCommands.sortStore()
    }

    @Override
    public void addInfoEvent(InfoEvent event) {

    }

    @Override
    public void addTransactionEvent(TransactionEvent event) {

    }

    @Override
    public List<HistoryItem> getAllHistory(String atmId, long limit, long offset) {
        return null;
    }

    @Override
    public List<CriticalEvent> getCriticalEvents(String atmId, long limit, long offset) {
        return null;
    }
}
