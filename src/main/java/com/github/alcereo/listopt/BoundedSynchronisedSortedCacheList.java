package com.github.alcereo.listopt;

import lombok.NonNull;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BoundedSynchronisedSortedCacheList<T>{

    private LinkedList<T> list = new LinkedList<>();
    private Comparator<T> comparator;
    private int sizeBound;

    public BoundedSynchronisedSortedCacheList(@NonNull Comparator<T> comparator, int sizeBound) {
        this.comparator = comparator;
        this.sizeBound = sizeBound;
    }

    public synchronized void addItem(T item) {
        // We can keep the list sorted for example if we put each event
        // in the right position in the list when adding

        if (list.size()>sizeBound)
            list.removeLast();

        if (list.isEmpty() || comparator.compare(list.getFirst(), item) > 0) {
            list.addFirst(item);
            return;
        }

        for (int index = 0; index < list.size(); index++) {
            if (comparator.compare(list.get(index), item) > 0) {
                list.add(index, item);
                return;
            }
        }

        list.addLast(item);
    }

    public synchronized List<T> getAll(long limit, long offset){
        return list.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

}
