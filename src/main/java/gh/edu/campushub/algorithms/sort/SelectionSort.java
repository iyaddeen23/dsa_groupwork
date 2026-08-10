package gh.edu.campushub.algorithms.sort;

import java.util.Comparator;

/**
 * Selection sort — in-place, NOT stable (a long-distance swap can jump an
 * equal-key element past another equal-key element). O(n^2) comparisons in
 * every case (best/average/worst), but only O(n) swaps, which is why it can
 * beat insertion sort when writes are expensive.
 */
public final class SelectionSort {

    private SelectionSort() {
    }

    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (comparator.compare(array[j], array[minIndex]) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                swap(array, i, minIndex);
            }
        }
    }

    public static <T extends Comparable<T>> void sort(T[] array) {
        sort(array, Comparable::compareTo);
    }

    private static <T> void swap(T[] array, int i, int j) {
        T tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
}
