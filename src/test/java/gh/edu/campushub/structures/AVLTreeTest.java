package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    /** Before/after: inserting 1,2,3 in order would build a 3-node BST chain (height 2); AVL rebalances to height 1. */
    @Test
    void ascendingInserts_triggerLeftRotationAndStayBalanced() {
        AVLTree<Integer, String> avl = new AVLTree<>();
        avl.insert(1, "a");
        avl.insert(2, "b");
        avl.insert(3, "c"); // would unbalance a plain BST into a height-2 chain; AVL must rotate to height 1
        assertEquals(1, avl.height());
        DynamicArray<Integer> sorted = avl.inorder();
        assertEquals("[1, 2, 3]", sorted.toString());
    }

    @Test
    void descendingInserts_triggerRightRotationAndStayBalanced() {
        AVLTree<Integer, String> avl = new AVLTree<>();
        avl.insert(3, "c");
        avl.insert(2, "b");
        avl.insert(1, "a");
        assertEquals(1, avl.height());
    }

    /** Left-right case: insert 3, 1, 2 — the imbalance appears on the left child's right subtree. */
    @Test
    void leftRightCase_stillBalancesToMinimalHeight() {
        AVLTree<Integer, String> avl = new AVLTree<>();
        avl.insert(3, "c");
        avl.insert(1, "a");
        avl.insert(2, "b");
        assertEquals(1, avl.height());
        assertEquals("[1, 2, 3]", avl.inorder().toString());
    }

    @Test
    void heightStaysLogarithmic_forLargerAscendingSequence() {
        AVLTree<Integer, Integer> avl = new AVLTree<>();
        int n = 1000;
        for (int i = 0; i < n; i++) avl.insert(i, i);
        // Proven AVL bound: height < 1.44 * log2(n+2). A generous 2x log2(n) margin keeps this test non-flaky.
        double bound = 2 * (Math.log(n + 1) / Math.log(2));
        assertTrue(avl.height() <= bound, "AVL height " + avl.height() + " must stay O(log n), bound=" + bound);
    }

    @Test
    void searchAndContains_workAfterRebalancing() {
        AVLTree<Integer, String> avl = new AVLTree<>();
        for (int i = 1; i <= 7; i++) avl.insert(i, "v" + i);
        assertEquals("v4", avl.search(4));
        assertTrue(avl.contains(7));
        assertFalse(avl.contains(100));
    }
}
