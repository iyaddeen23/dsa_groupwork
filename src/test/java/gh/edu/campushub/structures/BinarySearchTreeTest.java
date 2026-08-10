package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTreeTest {

    @Test
    void inorderTraversal_alwaysProducesSortedOutput() {
        BinarySearchTree<Integer, String> bst = new BinarySearchTree<>();
        int[] keys = {5, 3, 8, 1, 4, 7, 9};
        for (int k : keys) bst.insert(k, "v" + k);

        DynamicArray<Integer> sorted = bst.inorder();
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i - 1) < sorted.get(i), "inorder walk of a BST must be strictly increasing here");
        }
    }

    /** Search-path trace: searching for 7 in this tree visits 5 -> 8 -> 7 (2 comparisons past the root). */
    @Test
    void search_findsExistingKeyAndMissesAbsentKey() {
        BinarySearchTree<Integer, String> bst = new BinarySearchTree<>();
        bst.insert(5, "five");
        bst.insert(3, "three");
        bst.insert(8, "eight");
        bst.insert(7, "seven");

        assertEquals("seven", bst.search(7));
        assertNull(bst.search(100));
    }

    @Test
    void duplicateKey_overwritesValueWithoutGrowingSize() {
        BinarySearchTree<Integer, String> bst = new BinarySearchTree<>();
        bst.insert(1, "first");
        bst.insert(1, "second");
        assertEquals(1, bst.size());
        assertEquals("second", bst.search(1));
    }

    @Test
    void emptyTree_hasHeightMinusOneAndSizeZero() {
        BinarySearchTree<Integer, String> bst = new BinarySearchTree<>();
        assertEquals(-1, bst.height());
        assertEquals(0, bst.size());
        assertTrue(bst.isEmpty());
    }

    @Test
    void skewedInsertion_heightGrowsLinearly() {
        BinarySearchTree<Integer, String> bst = new BinarySearchTree<>();
        for (int i = 0; i < 10; i++) bst.insert(i, "v"); // ascending inserts degenerate into a linked list
        assertEquals(9, bst.height());
    }
}
