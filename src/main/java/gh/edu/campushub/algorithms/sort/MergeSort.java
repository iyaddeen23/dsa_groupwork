package gh.edu.campushub.algorithms.sort;

import java.util.Comparator;

/**
 * Merge sort — divide and conquer: split in half, sort each half recursively,
 * merge the two sorted halves. Stable (the merge step always takes from the
 * left run on ties). Always O(n log n): T(n) = 2T(n/2) + O(n), which solves
 * to O(n log n) in the best, average AND worst case — no data-dependent
 * degradation, unlike quicksort. Costs O(n) auxiliary space per merge level.
 */
public final class MergeSort {

    private MergeSort() {
    }

    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        if (array.length < 2) {
            return;
        }
        T[] buffer = array.clone();
        sort(array, buffer, 0, array.length - 1, comparator);
    }

    public static <T extends Comparable<T>> void sort(T[] array) {
        sort(array, Comparable::compareTo);
    }

    private static <T> void sort(T[] array, T[] buffer, int low, int high, Comparator<? super T> comparator) {
        if (low >= high) {
            return;
        }
        int mid = low + (high - low) / 2;
        sort(array, buffer, low, mid, comparator);
        sort(array, buffer, mid + 1, high, comparator);
        merge(array, buffer, low, mid, high, comparator);
    }

    private static <T> void merge(T[] array, T[] buffer, int low, int mid, int high, Comparator<? super T> comparator) {
        System.arraycopy(array, low, buffer, low, high - low + 1);

        int left = low;
        int right = mid + 1;
        int dest = low;

        while (left <= mid && right <= high) {
            if (comparator.compare(buffer[left], buffer[right]) <= 0) {
                array[dest++] = buffer[left++];
            } else {
                array[dest++] = buffer[right++];
            }
        }
        while (left <= mid) {
            array[dest++] = buffer[left++];
        }
        while (right <= high) {
            array[dest++] = buffer[right++];
        }
    }
}
