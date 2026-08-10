package gh.edu.campushub.structures;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * An array-backed binary heap built from scratch (no java.util.PriorityQueue).
 * Whether it behaves as a min-heap or max-heap depends entirely on the
 * {@link Comparator} supplied — the dispatch engine (M5) uses a min-heap over
 * {@code ServiceRequest.dispatchScore(...)} so the most urgent request sits at
 * index 0 and is the next one extracted.
 */
public class BinaryHeap<T> {

    private final DynamicArray<T> data = new DynamicArray<>();
    private final Comparator<? super T> comparator;

    public BinaryHeap(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    public static <T> BinaryHeap<T> heapify(T[] items, Comparator<? super T> comparator) {
        BinaryHeap<T> heap = new BinaryHeap<>(comparator);
        for (T item : items) {
            heap.data.add(item);
        }
        // Bottom-up heapify: sift down every internal node, starting at the last parent.
        for (int i = heap.data.size() / 2 - 1; i >= 0; i--) {
            heap.siftDown(i);
        }
        return heap;
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public void insert(T value) {
        data.add(value);
        siftUp(data.size() - 1);
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
        return data.get(0);
    }

    /** Removes and returns the root (min, under a natural-order comparator). */
    public T extractRoot() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
        T root = data.get(0);
        T last = data.remove(data.size() - 1);
        if (!data.isEmpty()) {
            data.set(0, last);
            siftDown(0);
        }
        return root;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (comparator.compare(data.get(index), data.get(parent)) < 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void siftDown(int index) {
        int size = data.size();
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;
            if (left < size && comparator.compare(data.get(left), data.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < size && comparator.compare(data.get(right), data.get(smallest)) < 0) {
                smallest = right;
            }
            if (smallest == index) {
                break;
            }
            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        T tmp = data.get(i);
        data.set(i, data.get(j));
        data.set(j, tmp);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
