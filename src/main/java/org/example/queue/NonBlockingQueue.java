package org.example.queue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class NonBlockingQueue<T> implements Queue<T> {

    private final AtomicReference<Node<T>> head, tail;
    private final AtomicInteger size;

    public NonBlockingQueue() {
        head = new AtomicReference<>(null);
        tail = new AtomicReference<>(null);
        size = new AtomicInteger();
        size.set(0);
    }

    @Override
    public void add(T element) {
        if (element == null) {
            throw new NullPointerException();
        }

        Node<T> node = new Node<>(element);
        Node<T> currentTail;
        do {
            currentTail = tail.get();
            node.prev = currentTail;
        } while (!tail.compareAndSet(currentTail, node));

        if (node.prev != null) {
            node.prev.next = node;
        }

        head.compareAndSet(null, node);
        size.incrementAndGet();
    }

    @Override
    public T get() {
        if (head.get() == null) {
            return null;
        }

        Node<T> currentHead;
        Node<T> nextNode;

        do {
            currentHead = head.get();
            nextNode = currentHead.next;
        } while (!head.compareAndSet(currentHead, nextNode));

        size.decrementAndGet();
        return currentHead.value;
    }

    @Override
    public boolean isEmpty() {
        return size.get() == 0;
    }

    private static class Node<T> {
        private final T value;
        private volatile Node<T> next;
        private volatile Node<T> prev;

        public Node(T item) {
            this.value = item;
            this.next = null;
        }
    }

}

