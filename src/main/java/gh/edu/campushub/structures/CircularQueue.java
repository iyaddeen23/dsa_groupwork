package gh.edu.campushub.structures;

import java.util.NoSuchElementException;

/**
 * An array-backed circular (ring-buffer) queue built from scratch.
 * {@code front} and {@code rear} wrap around the backing array using modulo
 * arithmetic instead of shifting elements on dequeue. Grows (doubles, then
 * re-lays the elements out contiguously from index 0) when full rather than
 * rejecting inserts, so it stays usable as the live dispatch queue.
 */
public class CircularQueue<T> {

    private static final int DEFAULT_CAPACITY = 8;

    private Object[] data;
    private int front; // index of the current head element
    private int size;

    public CircularQueue() {
        this(DEFAULT_CAPACITY);
    }

    public CircularQueue(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity must be >= 1");
        }
        this.data = new Object[initialCapacity];
        this.front = 0;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == data.length;
    }

    /** Index of the next free slot, wrapping past the end of the backing array. */
    private int rearIndex() {
        return (front + size) % data.length;
    }

    public void enqueue(T value) {
        if (isFull()) {
            grow();
        }
        data[rearIndex()] = value;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        T value = (T) data[front];
        data[front] = null;
        front = (front + 1) % data.length;
        size--;
        return value;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        return (T) data[front];
    }

    private void grow() {
        Object[] newData = new Object[data.length * 2];
        for (int i = 0; i < size; i++) {
            newData[i] = data[(front + i) % data.length];
        }
        data = newData;
        front = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[(front + i) % data.length]);
            if (i < size - 1) sb.append(", ");
        }
        return sb.append(']').toString();
    }
}
