package com.github.alcereo.simple;

import com.github.alcereo.CriticalEvent;
import com.github.alcereo.InfoEvent;
import com.github.alcereo.TransactionEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestUtils {

    public static Random random = new Random(System.currentTimeMillis());
    public static int atmIdBound = 3;

    public static List<CriticalEvent> buildCriticalEvents(int count) {

        Random random = new Random(System.currentTimeMillis());

        List<CriticalEvent> resultList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            atmIdBound = 10;
            resultList.add(
                    CriticalEvent.builder()
                            .atmId(String.valueOf(random.nextInt(atmIdBound)))
                            .eventId(String.valueOf(random.nextInt(4)))
                            .timestamp(Instant.now().plusSeconds(i+random.nextInt(3)))
                            .data(UUID.randomUUID().toString())
                            .build()
            );
        }

        return resultList;
    }

    public static List<InfoEvent> buildInfoEvents(int count) {

        Random random = new Random(System.currentTimeMillis());

        List<InfoEvent> resultList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            resultList.add(
                    InfoEvent.builder()
                            .atmId(String.valueOf(random.nextInt(atmIdBound)))
                            .eventId(String.valueOf(random.nextInt(4)))
                            .timestamp(Instant.now().plusSeconds(i+random.nextInt(3)))
                            .data(UUID.randomUUID().toString())
                            .build()
            );
        }

        return resultList;
    }

    public static List<TransactionEvent> buildTransactionalEvents(int count) {

        Random random = new Random(System.currentTimeMillis());

        List<TransactionEvent> resultList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            resultList.add(
                    TransactionEvent.builder()
                            .atmId(String.valueOf(random.nextInt(atmIdBound)))
                            .eventId(String.valueOf(random.nextInt(4)))
                            .timestamp(Instant.now().plusSeconds(i+random.nextInt(3)))
                            .data(UUID.randomUUID().toString())
                            .build()
            );
        }

        return resultList;
    }

}
