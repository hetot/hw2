package org.example.queue;

public interface Queue<T> {
    void add(T item);

    T get();

    boolean isEmpty();
}
