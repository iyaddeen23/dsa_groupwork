package gh.edu.campushub.structures;

/**
 * Simplified self-balancing tree (AVL) — the PRD's brief-approved alternative to a full
 * red-black tree. Rebalances on every insertion via single/double rotations so height
 * always stays O(log n), which is the property the report needs to demonstrate.
 */
public class AVLTree<K extends Comparable<K>, V> {

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;
        int height;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.height = 0;
        }
    }

    private Node<K, V> root;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int height() {
        return heightOf(root);
    }

    private int heightOf(Node<K, V> node) {
        return node == null ? -1 : node.height;
    }

    private int balanceFactor(Node<K, V> node) {
        return node == null ? 0 : heightOf(node.left) - heightOf(node.right);
    }

    private void recomputeHeight(Node<K, V> node) {
        node.height = 1 + Math.max(heightOf(node.left), heightOf(node.right));
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
            node.value = value;
            return node;
        }
        recomputeHeight(node);
        return rebalance(node);
    }

    private Node<K, V> rebalance(Node<K, V> node) {
        int balance = balanceFactor(node);

        if (balance > 1) { // left-heavy
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left); // left-right case
            }
            return rotateRight(node); // left-left case
        }
        if (balance < -1) { // right-heavy
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right); // right-left case
            }
            return rotateLeft(node); // right-right case
        }
        return node;
    }

    private Node<K, V> rotateRight(Node<K, V> y) {
        Node<K, V> x = y.left;
        Node<K, V> t2 = x.right;
        x.right = y;
        y.left = t2;
        recomputeHeight(y);
        recomputeHeight(x);
        return x;
    }

    private Node<K, V> rotateLeft(Node<K, V> x) {
        Node<K, V> y = x.right;
        Node<K, V> t2 = y.left;
        y.left = x;
        x.right = t2;
        recomputeHeight(x);
        recomputeHeight(y);
        return y;
    }

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
