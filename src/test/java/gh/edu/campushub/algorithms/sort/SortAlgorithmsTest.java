package gh.edu.campushub.algorithms.sort;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SortAlgorithmsTest {

    private static final Integer[] UNSORTED = {5, 2, 9, 1, 5, 6, 3};
    private static final Integer[] SORTED = {1, 2, 3, 5, 5, 6, 9};

    @Test
    void selectionSort_producesSortedOutput() {
        Integer[] data = UNSORTED.clone();
        SelectionSort.sort(data);
        assertArrayEquals(SORTED, data);
    }

    @Test
    void insertionSort_producesSortedOutput() {
        Integer[] data = UNSORTED.clone();
        InsertionSort.sort(data);
        assertArrayEquals(SORTED, data);
    }

    @Test
    void mergeSort_producesSortedOutput() {
        Integer[] data = UNSORTED.clone();
        MergeSort.sort(data);
        assertArrayEquals(SORTED, data);
    }

    @Test
    void quickSort_producesSortedOutput() {
        Integer[] data = UNSORTED.clone();
        QuickSort.sort(data);
        assertArrayEquals(SORTED, data);
    }

    @Test
    void emptyAndSingleElementArrays_areNoOpsNotCrashes() {
        Integer[] empty = {};
        Integer[] single = {1};
        SelectionSort.sort(empty);
        InsertionSort.sort(single);
        MergeSort.sort(empty);
        QuickSort.sort(single);
        assertArrayEquals(new Integer[]{}, empty);
        assertArrayEquals(new Integer[]{1}, single);
    }

    @Test
    void alreadySortedInput_staysCorrect() {
        Integer[] data = SORTED.clone();
        QuickSort.sort(data); // this is quicksort's worst-case shape (see class Javadoc) — must still be correct
        assertArrayEquals(SORTED, data);
    }

    @Test
    void reverseSortedInput_staysCorrect() {
        Integer[] reversed = {9, 6, 5, 5, 3, 2, 1};
        MergeSort.sort(reversed);
        assertArrayEquals(SORTED, reversed);
    }

    /** Stability: insertion sort must never swap two equal-key elements past each other. */
    @Test
    void insertionSort_isStable() {
        record Tagged(int key, String tag) {
        }
        Tagged[] data = {new Tagged(1, "first"), new Tagged(1, "second"), new Tagged(0, "zero")};
        InsertionSort.sort(data, Comparator.comparingInt(Tagged::key));
        assertEquals("first", data[1].tag());
        assertEquals("second", data[2].tag());
    }

    /** Not-stable-by-design: selection sort's long-distance swap CAN cross two equal-key elements. */
    @Test
    void selectionSort_isNotGuaranteedStable() {
        record Tagged(int key, String tag) {
        }
        // Classic instability trigger for selection sort's swap-based approach.
        Tagged[] data = {new Tagged(3, "a"), new Tagged(3, "b"), new Tagged(1, "c")};
        SelectionSort.sort(data, Comparator.comparingInt(Tagged::key));
        assertEquals(1, data[0].key());
        // Both 3-key elements are present after sorting, regardless of their relative order.
        assertTrue((data[1].tag().equals("a") && data[2].tag().equals("b"))
                || (data[1].tag().equals("b") && data[2].tag().equals("a")));
    }

    @Test
    void allFourAlgorithms_agreeOnRandomData() {
        Random random = new Random(12345);
        Integer[] data = new Integer[200];
        for (int i = 0; i < data.length; i++) data[i] = random.nextInt(1000);

        Integer[] a = data.clone(), b = data.clone(), c = data.clone(), d = data.clone();
        SelectionSort.sort(a);
        InsertionSort.sort(b);
        MergeSort.sort(c);
        QuickSort.sort(d);

        assertArrayEquals(a, b);
        assertArrayEquals(b, c);
        assertArrayEquals(c, d);
    }
}
