package gh.edu.campushub.structures;

/** An unbalanced binary search tree built from scratch, keyed by a Comparable key. */
public class BinarySearchTree<K extends Comparable<K>, V> {

    protected static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    protected Node<K, V> root;
    protected int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void insert(K key, V value) {
        root = insert(root, key, value);
    }

    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, key, value);
        } else {
            node.value = value; // overwrite on duplicate key
        }
        return node;
    }

    /** Returns the value for {@code key}, or null if absent. Also usable as a search-path trace point. */
    public V search(K key) {
        Node<K, V> cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) {
                return cur.value;
            }
            cur = cmp < 0 ? cur.left : cur.right;
        }
        return null;
    }

    public boolean contains(K key) {
        return search(key) != null;
    }

    public int height() {
        return height(root);
    }

    protected int height(Node<K, V> node) {
        if (node == null) {
            return -1;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    /** Sorted (in-order) key list — proves BST correctness: an in-order walk of a BST is always sorted. */
    public DynamicArray<K> inorder() {
        DynamicArray<K> result = new DynamicArray<>();
        inorder(root, result);
        return result;
    }

    private void inorder(Node<K, V> node, DynamicArray<K> result) {
        if (node == null) {
            return;
        }
        inorder(node.left, result);
        result.add(node.key);
        inorder(node.right, result);
    }
}
