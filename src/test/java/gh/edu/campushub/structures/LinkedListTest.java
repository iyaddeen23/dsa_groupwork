package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {

    @Test
    void addFirstAndAddLastBuildExpectedOrder() {
        LinkedList<Integer> list = new LinkedList<>();
        list.addLast(2);
        list.addFirst(1);
        list.addLast(3);
        assertEquals("[1, 2, 3]", list.toString());
    }

    @Test
    void insertAfterPlacesElementImmediatelyAfterTarget() {
        LinkedList<Integer> list = new LinkedList<>();
        list.addLast(1);
        list.addLast(3);
        list.insertAfter(1, 2);
        assertEquals("[1, 2, 3]", list.toString());
    }

    @Test
    void insertAfterMissingTargetThrows() {
        LinkedList<Integer> list = new LinkedList<>();
        list.addLast(1);
        assertThrows(NoSuchElementException.class, () -> list.insertAfter(99, 2));
    }

    @Test
    void removeDeletesFirstMatchOnly() {
        LinkedList<Integer> list = new LinkedList<>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(2);
        assertTrue(list.remove(2));
        assertEquals("[1, 2]", list.toString());
    }

    @Test
    void emptyList_removeFirstThrows() {
        LinkedList<Integer> list = new LinkedList<>();
        assertTrue(list.isEmpty());
        assertThrows(NoSuchElementException.class, list::removeFirst);
    }

    @Test
    void iteratorDemo_traversesFrontToBack() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("x");
        list.addLast("y");
        list.addLast("z");
        Iterator<String> it = list.iterator();
        assertEquals("x", it.next());
        assertEquals("y", it.next());
        assertEquals("z", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void singleElement_removeLastLeavesListEmpty() {
        LinkedList<Integer> list = new LinkedList<>();
        list.addLast(7);
        assertEquals(7, list.removeLast());
        assertTrue(list.isEmpty());
        assertThrows(NoSuchElementException.class, list::peekFirst);
    }
}
