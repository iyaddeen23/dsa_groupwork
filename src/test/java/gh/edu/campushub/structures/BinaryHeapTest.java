package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class BinaryHeapTest {

    /** Dispatch-order trace: inserting out of order, extraction must come out fully sorted ascending. */
    @Test
    void extractRoot_alwaysReturnsCurrentMinimum() {
        BinaryHeap<Integer> heap = new BinaryHeap<>(Comparator.naturalOrder());
        int[] insertOrder = {5, 3, 8, 1, 9, 2};
        for (int v : insertOrder) heap.insert(v);

        int[] expected = {1, 2, 3, 5, 8, 9};
        for (int e : expected) {
            assertEquals(e, heap.extractRoot());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    void heapify_buildsValidHeapFromArbitraryArray() {
        Integer[] items = {9, 1, 7, 3, 5, 2};
        BinaryHeap<Integer> heap = BinaryHeap.heapify(items, Comparator.naturalOrder());
        assertEquals(1, heap.extractRoot());
        assertEquals(2, heap.extractRoot());
    }

    @Test
    void emptyHeap_extractThrows() {
        BinaryHeap<Integer> heap = new BinaryHeap<>(Comparator.naturalOrder());
        assertThrows(NoSuchElementException.class, heap::extractRoot);
    }

    @Test
    void maxHeap_usingReversedComparator() {
        BinaryHeap<Integer> heap = new BinaryHeap<>(Comparator.<Integer>naturalOrder().reversed());
        heap.insert(3);
        heap.insert(10);
        heap.insert(1);
        assertEquals(10, heap.extractRoot());
    }
}
