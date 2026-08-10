package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DisjointSetTest {

    @Test
    void initially_everyElementIsItsOwnSet() {
        DisjointSet ds = new DisjointSet(5);
        assertEquals(5, ds.setCount());
        for (int i = 0; i < 5; i++) {
            assertEquals(i, ds.find(i));
        }
    }

    /** Kruskal connectivity trace: union(0,1), union(1,2) must merge {0,1,2} into one set without merging {3,4}. */
    @Test
    void unionMergesSets_pathCompressionKeepsFindConsistent() {
        DisjointSet ds = new DisjointSet(5);
        assertTrue(ds.union(0, 1));
        assertTrue(ds.union(1, 2));
        assertTrue(ds.connected(0, 2));
        assertFalse(ds.connected(0, 3));
        assertEquals(3, ds.setCount()); // {0,1,2}, {3}, {4}
    }

    @Test
    void unionOfAlreadyConnected_returnsFalseAndDoesNotChangeCount() {
        DisjointSet ds = new DisjointSet(3);
        ds.union(0, 1);
        int countBefore = ds.setCount();
        assertFalse(ds.union(0, 1));
        assertEquals(countBefore, ds.setCount());
    }

    @Test
    void fullyConnected_endsAsSingleSet() {
        DisjointSet ds = new DisjointSet(4);
        ds.union(0, 1);
        ds.union(2, 3);
        ds.union(1, 2);
        assertEquals(1, ds.setCount());
        assertTrue(ds.connected(0, 3));
    }
}
