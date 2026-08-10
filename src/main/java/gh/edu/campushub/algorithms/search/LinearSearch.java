package gh.edu.campushub.algorithms.search;

import java.util.Comparator;

/** O(n) search — no precondition on ordering, works on any array. */
public final class LinearSearch {

    private LinearSearch() {
    }

    /** Returns the index of the first element equal to {@code target}, or -1 if absent. */
    public static <T> int search(T[] array, T target, Comparator<? super T> comparator) {
        for (int i = 0; i < array.length; i++) {
            if (comparator.compare(array[i], target) == 0) {
                return i;
            }
        }
        return -1;
    }

    public static <T extends Comparable<T>> int search(T[] array, T target) {
        return search(array, target, Comparable::compareTo);
    }
}
