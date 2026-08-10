package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BTreeTest {

    /** Node-split trace: minDegree=2 means a page overflows at 3 keys; the 4th insert must force a split. */
    @Test
    void insertionBeyondPageCapacity_forcesNodeSplit() {
        BTree<Integer, String> tree = new BTree<>(2); // max 3 keys per page before splitting
        tree.insert(10, "ten");
        tree.insert(20, "twenty");
        tree.insert(30, "thirty"); // page full (3 keys)
        assertEquals(0, tree.height(), "still a single root page before the split");

        tree.insert(40, "forty"); // forces the root page to split
        assertEquals(1, tree.height(), "root split should push the tree to height 1");

        for (int key : new int[]{10, 20, 30, 40}) {
            assertNotNull(tree.search(key), "key " + key + " must still be findable after the split");
        }
    }

    @Test
    void search_missingKeyReturnsNull() {
        BTree<Integer, String> tree = new BTree<>(3);
        tree.insert(5, "five");
        assertNull(tree.search(999));
        assertFalse(tree.contains(999));
    }

    @Test
    void manyInsertions_allRemainSearchableAndHeightStaysShallow() {
        BTree<Integer, Integer> tree = new BTree<>(4);
        int n = 500;
        for (int i = 0; i < n; i++) tree.insert(i, i * 10);
        for (int i = 0; i < n; i += 37) {
            assertEquals(i * 10, tree.search(i));
        }
        assertTrue(tree.height() <= 6, "a degree-4 B-tree over 500 keys should stay very shallow, was " + tree.height());
    }

    @Test
    void emptyTree_searchReturnsNull() {
        BTree<Integer, String> tree = new BTree<>(3);
        assertNull(tree.search(1));
        assertEquals(0, tree.size());
    }
}
