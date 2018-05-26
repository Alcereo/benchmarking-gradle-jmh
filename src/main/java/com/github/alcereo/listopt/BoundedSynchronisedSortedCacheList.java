package com.github.alcereo.listopt;

import lombok.NonNull;

import java.util.Collection;
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

        list.addFirst(item);
        list = list.stream()
                .sorted(comparator)
                .limit(sizeBound)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public synchronized void addItemList(Collection<T> items) {
        // We can keep the list sorted for example if we put each event
        // in the right position in the list when adding

        list.addAll(0, items);
        list = list.stream()
                .sorted(comparator)
                .limit(sizeBound)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public synchronized List<T> getAll(long limit, long offset){
        return list.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

}
