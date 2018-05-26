package com.github.alcereo.redis;

import io.lettuce.core.Limit;
import io.lettuce.core.Range;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SortArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@ExtendWith(EmbeddedRedis.class)
@Ignore
public class RedisTest {

    private static RedisCommands<String, String> syncCommands;
    private static StatefulRedisConnection<String, String> connection;
    private static RedisClient redisClient;

    @BeforeAll
    private static void setup(@RedisPort String redisPort){
        redisClient = RedisClient.create("redis://localhost:"+redisPort+"/0");
        connection = redisClient.connect();
        syncCommands = connection.sync();
    }

    @AfterAll
    private static void after(){
        connection.close();
        redisClient.shutdown();
    }

    @Test
    void name() {

        Instant now = Instant.now();
        List<EventData> testDataList = Arrays.asList(
                new EventData(1, "event-1", now),
                new EventData(2, "event-2", now.plusSeconds(2)),
                new EventData(4, "event-4", now.plusSeconds(3)),
                new EventData(3, "event-3", now.plusSeconds(4)),
                new EventData(5, "event-5", now.plusSeconds(5)),
                new EventData(6, "event-6", now.plusSeconds(6))
        );

        testDataList.forEach(
                eventData -> {
                    syncCommands.set("event:critical:"+eventData.index, eventData.data);
                    syncCommands.zadd("event:critical:indexes", (double) eventData.timestamp.toEpochMilli(), String.valueOf(eventData.index));
                }
        );

        System.out.println("==== get critical 5");
        System.out.println(syncCommands.get("event:critical:5"));

        System.out.println("==== range to 3 seconds");
        Range<? extends Number> range = Range.create((double)now.toEpochMilli(), (double)now.plusSeconds(3).toEpochMilli());
        System.out.println(syncCommands.zrangebyscore("event:critical:indexes", range));

        System.out.println("==== get all set");
        System.out.println(syncCommands.zrange("event:critical:indexes", 0,-1));


        System.out.println("===== get sort indexes");

        List<String> sort = syncCommands.sort(
                "event:critical:indexes",
                SortArgs.Builder.by("event:critical:indexes")
                        .get("event:critical:*")
                        .get("#")
                .limit(Limit.create(1,3))
        );

        sort.forEach(System.out::println);

//        System.out.println("===== get range indexes");
//
//        syncCommands.zra(
//                "event:critical:indexes",
//                get("event:critical:*").get("#")
//        )
//
//        sort.forEach(System.out::println);

    }


    @Test
    @Ignore
    void name2() {

        Instant now = Instant.now();
        List<EventData> testDataList = Arrays.asList(
                new EventData(1, "event-1", now),
                new EventData(2, "event-2", now.plusSeconds(2)),
                new EventData(4, "event-4", now.plusSeconds(3)),
                new EventData(3, "event-3", now.plusSeconds(4)),
                new EventData(5, "event-5", now.plusSeconds(5)),
                new EventData(6, "event-6", now.plusSeconds(6))
        );

        testDataList.forEach(
                eventData -> {
                    syncCommands.set("event:critical:"+eventData.index, eventData.data);
                    syncCommands.zadd("event:critical:indexes", (double) eventData.timestamp.toEpochMilli(), eventData.data);
                }
        );

        System.out.println("==== get critical 5");
        System.out.println(syncCommands.get("event:critical:5"));

        System.out.println("==== range to 3 seconds");
        Range<? extends Number> range = Range.create((double)now.toEpochMilli(), (double)now.plusSeconds(3).toEpochMilli());
        System.out.println(syncCommands.zrangebyscore("event:critical:indexes", range));

        System.out.println("==== get all set");
        System.out.println(syncCommands.zrange("event:critical:indexes", 0,-1));


    }

    @Value
    @AllArgsConstructor
    private static class EventData {
        int index;
        String data;
        Instant timestamp;
    }
}
