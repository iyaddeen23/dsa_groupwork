package gh.edu.campushub.structures;

import java.util.EmptyStackException;

/**
 * An array-backed LIFO stack built from scratch (no java.util.Stack).
 * Backs the audit/undo log (M3 evidence) and recursion-simulation demos.
 */
public class Stack<T> {

    private final DynamicArray<T> data = new DynamicArray<>();

    public void push(T value) {
        data.add(value);
    }

    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return data.remove(data.size() - 1);
    }

    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return data.get(data.size() - 1);
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
