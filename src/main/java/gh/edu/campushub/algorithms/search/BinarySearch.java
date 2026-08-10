package gh.edu.campushub.algorithms.search;

import java.util.Comparator;

/**
 * O(log n) search.
 *
 * <p><b>Precondition:</b> {@code array} MUST already be sorted in ascending
 * order according to {@code comparator} — this is not checked at runtime
 * (checking would cost O(n) and defeat the point of an O(log n) search), so
 * calling this on unsorted input silently returns wrong/undefined results.
 * That precondition failure is one of the two required counterexamples
 * (PRD Section 8: "one invalid precondition").
 */
public final class BinarySearch {

    private BinarySearch() {
    }

    public static <T> int search(T[] array, T target, Comparator<? super T> comparator) {
        int low = 0;
        int high = array.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = comparator.compare(array[mid], target);
            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    public static <T extends Comparable<T>> int search(T[] array, T target) {
        return search(array, target, Comparable::compareTo);
    }

    /** Verifies the precondition explicitly, for callers that want a checked variant. */
    public static <T> boolean isSorted(T[] array, Comparator<? super T> comparator) {
        for (int i = 1; i < array.length; i++) {
            if (comparator.compare(array[i - 1], array[i]) > 0) {
                return false;
            }
        }
        return true;
    }
}
