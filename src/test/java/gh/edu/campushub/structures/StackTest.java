package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.*;

class StackTest {

    @Test
    void pushThenPop_isLastInFirstOut() {
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void peekDoesNotRemove() {
        Stack<String> stack = new Stack<>();
        stack.push("only");
        assertEquals("only", stack.peek());
        assertEquals(1, stack.size());
    }

    @Test
    void emptyStack_popThrows() {
        Stack<Integer> stack = new Stack<>();
        assertThrows(EmptyStackException.class, stack::pop);
    }

    /** Recursion-simulation demo: an iterative factorial via an explicit stack mirrors the call-stack unwind order. */
    @Test
    void recursionSimulation_iterativeFactorialMatchesRecursive() {
        int n = 6;
        Stack<Integer> callStack = new Stack<>();
        int cursor = n;
        while (cursor > 1) {
            callStack.push(cursor);
            cursor--;
        }
        int result = 1;
        while (!callStack.isEmpty()) {
            result *= callStack.pop();
        }
        assertEquals(720, result); // 6! = 720, and 720 == recursive factorial(6)
    }
}
