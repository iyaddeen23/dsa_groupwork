package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class DequeTest {

    /** Urgent-request insertion example: normal requests join the rear, an urgent one jumps to the front. */
    @Test
    void urgentRequestInsertion_jumpsToFront() {
        Deque<String> deque = new Deque<>();
        deque.addRear("normal-1");
        deque.addRear("normal-2");
        deque.addFront("URGENT-1");

        assertEquals("URGENT-1", deque.peekFront());
        assertEquals("normal-2", deque.peekRear());
        assertEquals(3, deque.size());
    }

    @Test
    void removeFrontAndRemoveRearBothWork() {
        Deque<Integer> deque = new Deque<>();
        deque.addRear(1);
        deque.addRear(2);
        deque.addRear(3);
        assertEquals(1, deque.removeFront());
        assertEquals(3, deque.removeRear());
        assertEquals(1, deque.size());
    }

    @Test
    void emptyDeque_removeThrows() {
        Deque<Integer> deque = new Deque<>();
        assertThrows(NoSuchElementException.class, deque::removeFront);
        assertThrows(NoSuchElementException.class, deque::removeRear);
    }
}
