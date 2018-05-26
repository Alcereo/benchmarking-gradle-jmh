package com.github.alcereo.sortedmap;

import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class BoundedSynchronisedSortedMapCache<T> {

    private SortedSet<T> sortedSet;
    private int sizeBound;

    public BoundedSynchronisedSortedMapCache(@NonNull Comparator<T> comparator, int sizeBound) {
        this.sizeBound = sizeBound;
        sortedSet = new TreeSet<>(comparator);
    }

    public synchronized void addItem(T item) {
        sortedSet.add(item);

        if (sortedSet.size() > sizeBound){
            sortedSet.remove(sortedSet.last());
        }
    }

    public synchronized void addItemsList(List<T> item) {
        sortedSet.addAll(item);

        if (sortedSet.size() > sizeBound){
            sortedSet.removeAll(
                    sortedSet.stream()
                            .skip(sizeBound)
                            .collect(Collectors.toList())
            );
        }
    }

    public synchronized List<T> getAll(long limit, long offset){
        return sortedSet.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

}
