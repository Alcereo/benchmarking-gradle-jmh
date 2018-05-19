package com.github.alcereo;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CriticalEvent implements HistoryItem{

    @NonNull
    private final UUID cursor = UUID.randomUUID();

    @NonNull
    private final String atmId;

    @NonNull
    private final String eventId;

    @NonNull
    private final Instant timestamp;


    private String name;

    private String data;

}
