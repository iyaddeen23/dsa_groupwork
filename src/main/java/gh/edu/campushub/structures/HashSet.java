package gh.edu.campushub.structures;

/** A set built on top of {@link HashTable} (PRD: "Set / map | built on hash table or BST"). */
public class HashSet<T> {

    private static final Object PRESENT = new Object();
    private final HashTable<T, Object> table = new HashTable<>();

    public boolean add(T value) {
        boolean isNew = !table.containsKey(value);
        table.put(value, PRESENT);
        return isNew;
    }

    public boolean contains(T value) {
        return table.containsKey(value);
    }

    public boolean remove(T value) {
        boolean existed = table.containsKey(value);
        table.remove(value);
        return existed;
    }

    public int size() {
        return table.size();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public DynamicArray<T> toArray() {
        return table.keys();
    }
}
