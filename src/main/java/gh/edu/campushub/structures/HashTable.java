package gh.edu.campushub.structures;

import gh.edu.campushub.config.TeamConfig;

import java.util.Objects;

/**
 * A hash table built from scratch using separate chaining for collision
 * handling (no java.util.HashMap). Tracks a running collision count — every
 * put() that lands on an already-occupied bucket counts as one collision —
 * so the load-factor experiment (M10) has real data to plot.
 */
public class HashTable<K, V> {

    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    private Entry<K, V>[] buckets;
    private int size;
    private long collisionCount;

    public HashTable() {
        this(TeamConfig.HASH_TABLE_DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity must be >= 1");
        }
        this.buckets = new Entry[initialCapacity];
        this.size = 0;
        this.collisionCount = 0;
    }

    private int bucketIndex(K key, int tableLength) {
        int h = Objects.hashCode(key);
        h ^= (h >>> 16); // spread high bits down to reduce clustering for small tables
        return Math.floorMod(h, tableLength);
    }

    public void put(K key, V value) {
        Objects.requireNonNull(key, "key must not be null");
        int index = bucketIndex(key, buckets.length);
        Entry<K, V> head = buckets[index];

        for (Entry<K, V> e = head; e != null; e = e.next) {
            if (Objects.equals(e.key, key)) {
                e.value = value;
                return;
            }
        }

        if (head != null) {
            collisionCount++;
        }
        buckets[index] = new Entry<>(key, value, head);
        size++;

        if (loadFactor() > LOAD_FACTOR_THRESHOLD) {
            resize(buckets.length * 2);
        }
    }

    public V get(K key) {
        int index = bucketIndex(key, buckets.length);
        for (Entry<K, V> e = buckets[index]; e != null; e = e.next) {
            if (Objects.equals(e.key, key)) {
                return e.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        int index = bucketIndex(key, buckets.length);
        for (Entry<K, V> e = buckets[index]; e != null; e = e.next) {
            if (Objects.equals(e.key, key)) {
                return true;
            }
        }
        return false;
    }

    public V remove(K key) {
        int index = bucketIndex(key, buckets.length);
        Entry<K, V> prev = null;
        for (Entry<K, V> e = buckets[index]; e != null; prev = e, e = e.next) {
            if (Objects.equals(e.key, key)) {
                if (prev == null) {
                    buckets[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                size--;
                return e.value;
            }
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int capacity() {
        return buckets.length;
    }

    public double loadFactor() {
        return (double) size / buckets.length;
    }

    public long getCollisionCount() {
        return collisionCount;
    }

    public DynamicArray<K> keys() {
        DynamicArray<K> result = new DynamicArray<>();
        for (Entry<K, V> head : buckets) {
            for (Entry<K, V> e = head; e != null; e = e.next) {
                result.add(e.key);
            }
        }
        return result;
    }

    private void resize(int newCapacity) {
        @SuppressWarnings("unchecked")
        Entry<K, V>[] newBuckets = new Entry[newCapacity];
        for (Entry<K, V> head : buckets) {
            for (Entry<K, V> e = head; e != null; ) {
                Entry<K, V> next = e.next;
                int index = bucketIndex(e.key, newCapacity);
                e.next = newBuckets[index];
                newBuckets[index] = e;
                e = next;
            }
        }
        buckets = newBuckets;
    }

    public void clear() {
        this.buckets = createBuckets(TeamConfig.HASH_TABLE_DEFAULT_CAPACITY);
        this.size = 0;
        this.collisionCount = 0;
    }

    @SuppressWarnings("unchecked")
    private Entry<K, V>[] createBuckets(int capacity) {
        return new Entry[capacity];
    }
}
