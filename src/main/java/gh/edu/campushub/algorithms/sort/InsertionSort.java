package gh.edu.campushub.algorithms.sort;

import java.util.Comparator;

/**
 * Insertion sort — in-place and stable (equal-key elements only ever shift
 * right to make room, never cross each other). O(n) best case on
 * already-sorted input, O(n^2) average/worst — the near-sorted "top-up"
 * workload (adding a few new requests to an already-sorted list) is exactly
 * where this beats the other O(n^2) sorts in practice.
 */
public final class InsertionSort {

    private InsertionSort() {
    }

    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        for (int i = 1; i < array.length; i++) {
            T key = array[i];
            int j = i - 1;
            while (j >= 0 && comparator.compare(array[j], key) > 0) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }

    public static <T extends Comparable<T>> void sort(T[] array) {
        sort(array, Comparable::compareTo);
    }
}
