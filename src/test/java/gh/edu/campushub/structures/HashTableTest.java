package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    @Test
    void putGetRemove_basicContract() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("a", 1);
        table.put("b", 2);
        assertEquals(1, table.get("a"));
        assertEquals(2, table.remove("b"));
        assertNull(table.get("b"));
        assertEquals(1, table.size());
    }

    @Test
    void putSameKeyTwice_overwritesWithoutGrowingSize() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("x", 1);
        table.put("x", 2);
        assertEquals(2, table.get("x"));
        assertEquals(1, table.size());
    }

    /** Collision handling: force two keys into the same bucket of a tiny table and confirm both survive via chaining. */
    @Test
    void collisionHandling_bothKeysSurviveInSameBucket() {
        HashTable<Integer, String> table = new HashTable<>(4);
        table.put(0, "zero");
        table.put(4, "four"); // 0 and 4 collide mod 4
        assertTrue(table.getCollisionCount() >= 1);
        assertEquals("zero", table.get(0));
        assertEquals("four", table.get(4));
    }

    @Test
    void loadFactorExceedsThreshold_triggersResize() {
        HashTable<Integer, Integer> table = new HashTable<>(4);
        int initialCapacity = table.capacity();
        for (int i = 0; i < 10; i++) table.put(i, i);
        assertTrue(table.capacity() > initialCapacity, "table should have grown past its initial capacity");
        for (int i = 0; i < 10; i++) assertEquals(i, table.get(i));
    }

    @Test
    void emptyTable_getAndRemoveReturnNull() {
        HashTable<String, Integer> table = new HashTable<>();
        assertNull(table.get("missing"));
        assertNull(table.remove("missing"));
        assertTrue(table.isEmpty());
    }

    @Test
    void nullKey_isRejected() {
        HashTable<String, Integer> table = new HashTable<>();
        assertThrows(NullPointerException.class, () -> table.put(null, 1));
    }
}
