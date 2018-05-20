package com.github.alcereo.listopt;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.alcereo.simple.TestUtils.random;

class BoundedSynchronisedSortedCacheListTest {

    private int sizeBound;

    @BeforeEach
    void setUp() {
        sizeBound = 100;
    }

    @DisplayName("Comparator sorting")
    @Test
    void testSorting() {

        val list = new BoundedSynchronisedSortedCacheList<Integer>(
                Integer::compareTo,
                sizeBound
        );

        List<Integer> history = new ArrayList<>();

        for (int i = 0; i < sizeBound; i++) {
            int item = random.nextInt(sizeBound);
            history.add(item);
            list.addItem(item);
        }

        List<Integer> expectList = history.stream()
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());

        List<Integer> actualList = list.getAll(sizeBound, 0);

        Assertions.assertIterableEquals(
                expectList,
                actualList
        );

    }

    @DisplayName("Remove oldest by bound")
    @Test
    void testRemovingByBound() {

        val list = new BoundedSynchronisedSortedCacheList<Integer>(
                Integer::compareTo,
                sizeBound
        );

        List<Integer> history = new ArrayList<>();

        for (int i = 0; i < sizeBound+20; i++) {
            int item = random.nextInt(sizeBound);
            history.add(item);
            list.addItem(item);
        }

        List<Integer> expectList = history.stream()
                .sorted(Integer::compareTo)
                .limit(sizeBound)
                .collect(Collectors.toList());

        List<Integer> actualList = list.getAll(sizeBound, 0);

        Assertions.assertIterableEquals(
                expectList,
                actualList
        );

    }

    @DisplayName("Limit/Offset on getting")
    @Test
    void testLimitOffset() {

        val list = new BoundedSynchronisedSortedCacheList<Integer>(
                Integer::compareTo,
                sizeBound
        );

        List<Integer> history = new ArrayList<>();

        for (int i = 0; i < sizeBound; i++) {
            int item = random.nextInt(sizeBound);
            history.add(item);
            list.addItem(item);
        }

        val limit = random.nextInt(sizeBound/2)+1;
        val offset = random.nextInt(sizeBound/2)+1;

        List<Integer> expectList = history.stream()
                .sorted(Integer::compareTo)
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        List<Integer> actualList = list.getAll(limit, offset);

        Assertions.assertIterableEquals(
                expectList,
                actualList
        );

    }
}