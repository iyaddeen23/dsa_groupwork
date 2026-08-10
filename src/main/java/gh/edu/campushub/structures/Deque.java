package gh.edu.campushub.structures;

import java.util.NoSuchElementException;

/**
 * A double-ended queue built on the from-scratch {@link LinkedList} (no java.util.ArrayDeque).
 * Used for urgent-request insertion: normal requests go to the rear, urgent ones jump the front.
 */
public class Deque<T> {

    private final LinkedList<T> data = new LinkedList<>();

    public void addFront(T value) {
        data.addFirst(value);
    }

    public void addRear(T value) {
        data.addLast(value);
    }

    public T removeFront() {
        if (isEmpty()) {
            throw new NoSuchElementException("deque is empty");
        }
        return data.removeFirst();
    }

    public T removeRear() {
        if (isEmpty()) {
            throw new NoSuchElementException("deque is empty");
        }
        return data.removeLast();
    }

    public T peekFront() {
        if (isEmpty()) {
            throw new NoSuchElementException("deque is empty");
        }
        return data.peekFirst();
    }

    public T peekRear() {
        if (isEmpty()) {
            throw new NoSuchElementException("deque is empty");
        }
        return data.peekLast();
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
