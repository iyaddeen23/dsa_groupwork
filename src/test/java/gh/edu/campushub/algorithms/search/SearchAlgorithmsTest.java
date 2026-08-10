package gh.edu.campushub.algorithms.search;

import gh.edu.campushub.algorithms.sort.MergeSort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchAlgorithmsTest {

    @Test
    void linearSearch_findsElementRegardlessOfOrder() {
        Integer[] unsorted = {5, 1, 4, 2, 3};
        assertEquals(2, LinearSearch.search(unsorted, 4));
        assertEquals(-1, LinearSearch.search(unsorted, 99));
    }

    @Test
    void linearSearch_emptyArrayReturnsNotFound() {
        Integer[] empty = {};
        assertEquals(-1, LinearSearch.search(empty, 1));
    }

    @Test
    void binarySearch_findsElementInSortedArray() {
        Integer[] sorted = {1, 2, 3, 4, 5};
        assertEquals(0, BinarySearch.search(sorted, 1));
        assertEquals(4, BinarySearch.search(sorted, 5));
        assertEquals(2, BinarySearch.search(sorted, 3));
        assertEquals(-1, BinarySearch.search(sorted, 99));
    }

    @Test
    void binarySearch_singleElementArray() {
        Integer[] single = {42};
        assertEquals(0, BinarySearch.search(single, 42));
        assertEquals(-1, BinarySearch.search(single, 1));
    }

    /**
     * Counterexample #2 (PRD Section 8: "invalid precondition, e.g. unsorted binary search input").
     * Binary search's precondition is that its input is sorted; violating it produces a WRONG
     * answer rather than an exception, because the algorithm has no way to detect the violation.
     */
    @Test
    void binarySearch_onUnsortedInput_canGiveWrongAnswer() {
        Integer[] unsorted = {5, 1, 9, 2, 8, 3}; // NOT sorted — precondition violated on purpose
        assertFalse(BinarySearch.isSorted(unsorted, Integer::compareTo), "sanity check: input really is unsorted");

        // 8 is genuinely present at index 4, but the trace is: mid=2 (9>8, search left), mid=0 (5<8, search
        // right), mid=1 (1<8, search right) -> low crosses high having never looked at index 4. False negative.
        int result = BinarySearch.search(unsorted, 8);
        assertEquals(-1, result, "binary search wrongly reports 8 as absent because the array isn't sorted");
        assertEquals(4, LinearSearch.search(unsorted, 8), "8 really is in the array — linear search finds it fine");
    }

    @Test
    void mergeSortThenBinarySearch_isTheCorrectPattern() {
        Integer[] data = {5, 1, 9, 2, 8, 3};
        MergeSort.sort(data);
        assertTrue(BinarySearch.isSorted(data, Integer::compareTo));
        assertEquals(data.length - 1, BinarySearch.search(data, 9));
    }
}
