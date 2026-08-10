package gh.edu.campushub.algorithms.sort;

import java.util.Comparator;

/**
 * Quicksort — divide and conquer using Lomuto partitioning with the last
 * element as pivot (deterministic, so trace tables are reproducible). NOT
 * stable (the partition swap can cross two equal-key elements).
 *
 * <p>Recurrence: average case T(n) = 2T(n/2) + O(n) → O(n log n), because a
 * random input tends to split near the middle. Worst case T(n) = T(n-1) +
 * O(n) → O(n^2), which happens whenever the pivot is always the
 * smallest/largest remaining element — i.e. on already-sorted or
 * reverse-sorted input with this last-element pivot rule. That's the
 * concrete case the efficiency report should benchmark to show the O(n^2)
 * degradation predicted by theory.
 */
public final class QuickSort {

    private QuickSort() {
    }

    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        if (array.length < 2) {
            return;
        }
        sort(array, 0, array.length - 1, comparator);
    }

    public static <T extends Comparable<T>> void sort(T[] array) {
        sort(array, Comparable::compareTo);
    }

    private static <T> void sort(T[] array, int low, int high, Comparator<? super T> comparator) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(array, low, high, comparator);
        sort(array, low, pivotIndex - 1, comparator);
        sort(array, pivotIndex + 1, high, comparator);
    }

    private static <T> int partition(T[] array, int low, int high, Comparator<? super T> comparator) {
        T pivot = array[high];
        int boundary = low - 1;
        for (int i = low; i < high; i++) {
            if (comparator.compare(array[i], pivot) <= 0) {
                boundary++;
                swap(array, boundary, i);
            }
        }
        swap(array, boundary + 1, high);
        return boundary + 1;
    }

    private static <T> void swap(T[] array, int i, int j) {
        T tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
}
