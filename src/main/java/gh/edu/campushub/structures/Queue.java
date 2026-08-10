package gh.edu.campushub.structures;

import java.util.NoSuchElementException;

/** A FIFO queue built on the from-scratch {@link LinkedList} (no java.util.Queue). */
public class Queue<T> {

    private final LinkedList<T> data = new LinkedList<>();

    public void enqueue(T value) {
        data.addLast(value);
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        return data.removeFirst();
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        return data.peekFirst();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int size() {
        return data.size();
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
