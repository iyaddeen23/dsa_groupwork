package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {

    @Test
    void addAndGetPreserveInsertionOrder() {
        DynamicArray<String> array = new DynamicArray<>();
        array.add("a");
        array.add("b");
        array.add("c");
        assertEquals(3, array.size());
        assertEquals("a", array.get(0));
        assertEquals("c", array.get(2));
    }

    @Test
    void setOverwritesInPlace() {
        DynamicArray<Integer> array = new DynamicArray<>();
        array.add(1);
        array.set(0, 99);
        assertEquals(99, array.get(0));
        assertEquals(1, array.size());
    }

    @Test
    void insertShiftsSubsequentElementsRight() {
        DynamicArray<Integer> array = new DynamicArray<>();
        array.add(1);
        array.add(3);
        array.insert(1, 2);
        assertEquals(1, array.get(0));
        assertEquals(2, array.get(1));
        assertEquals(3, array.get(2));
    }

    @Test
    void removeShiftsSubsequentElementsLeft() {
        DynamicArray<Integer> array = new DynamicArray<>();
        array.add(1);
        array.add(2);
        array.add(3);
        int removed = array.remove(1);
        assertEquals(2, removed);
        assertEquals(2, array.size());
        assertEquals(3, array.get(1));
    }

    @Test
    void resizeTrace_capacityDoublesAcrossDefaultCapacityBoundary() {
        DynamicArray<Integer> array = new DynamicArray<>(2);
        assertEquals(2, array.capacity());
        array.add(1);
        array.add(2);
        assertEquals(2, array.capacity(), "should still fit exactly at capacity");
        array.add(3); // triggers growth
        assertEquals(4, array.capacity(), "capacity should double from 2 to 4");
        assertEquals(3, array.size());
    }

    @Test
    void emptyArray_getThrows() {
        DynamicArray<Integer> array = new DynamicArray<>();
        assertTrue(array.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
    }

    @Test
    void singleElement_removeLeavesEmpty() {
        DynamicArray<Integer> array = new DynamicArray<>();
        array.add(42);
        assertEquals(42, array.remove(0));
        assertTrue(array.isEmpty());
    }

    @Test
    void iteratorVisitsEveryElementInOrder() {
        DynamicArray<Integer> array = new DynamicArray<>();
        for (int i = 0; i < 5; i++) array.add(i);
        int expected = 0;
        for (int value : array) {
            assertEquals(expected++, value);
        }
        assertEquals(5, expected);
    }
}
