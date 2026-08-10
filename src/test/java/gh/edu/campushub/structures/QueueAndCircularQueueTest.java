package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class QueueAndCircularQueueTest {

    @Test
    void queue_isFirstInFirstOut() {
        Queue<Integer> queue = new Queue<>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
    }

    @Test
    void queue_emptyDequeueThrows() {
        Queue<Integer> queue = new Queue<>();
        assertThrows(NoSuchElementException.class, queue::dequeue);
    }

    /** Trace: capacity=4, enqueue 0..3 fills it (front=0,rear wraps to 0), then dequeue+enqueue proves wrap-around. */
    @Test
    void circularQueue_frontAndRearWrapAroundBackingArray() {
        CircularQueue<Integer> cq = new CircularQueue<>(4);
        cq.enqueue(10);
        cq.enqueue(20);
        cq.enqueue(30);
        cq.enqueue(40);
        assertTrue(cq.isFull());

        assertEquals(10, cq.dequeue()); // front moves from index 0 to index 1
        assertEquals(20, cq.dequeue()); // front moves to index 2

        cq.enqueue(50); // rear wraps around to index 0 (was full at index 4 % 4 == 0)
        cq.enqueue(60); // rear wraps to index 1

        assertEquals(30, cq.dequeue());
        assertEquals(40, cq.dequeue());
        assertEquals(50, cq.dequeue());
        assertEquals(60, cq.dequeue());
        assertTrue(cq.isEmpty());
    }

    @Test
    void circularQueue_growsWhenFullInsteadOfRejecting() {
        CircularQueue<Integer> cq = new CircularQueue<>(2);
        cq.enqueue(1);
        cq.enqueue(2);
        assertTrue(cq.isFull());
        cq.enqueue(3); // should trigger growth, not throw
        assertEquals(4, cq.capacity());
        assertEquals(3, cq.size());
    }

    @Test
    void circularQueue_emptyDequeueThrows() {
        CircularQueue<Integer> cq = new CircularQueue<>();
        assertThrows(NoSuchElementException.class, cq::dequeue);
    }
}
