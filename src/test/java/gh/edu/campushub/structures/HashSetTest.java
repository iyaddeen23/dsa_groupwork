package gh.edu.campushub.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashSetTest {

    @Test
    void addReturnsFalseForDuplicates() {
        HashSet<String> set = new HashSet<>();
        assertTrue(set.add("L001"));
        assertFalse(set.add("L001"));
        assertEquals(1, set.size());
    }

    @Test
    void membershipLookup_reflectsAddsAndRemoves() {
        HashSet<String> set = new HashSet<>();
        set.add("L001");
        set.add("L002");
        assertTrue(set.contains("L001"));
        set.remove("L001");
        assertFalse(set.contains("L001"));
        assertTrue(set.contains("L002"));
    }

    @Test
    void emptySet_isEmptyAndHasNoMembers() {
        HashSet<String> set = new HashSet<>();
        assertTrue(set.isEmpty());
        assertFalse(set.contains("anything"));
    }
}
