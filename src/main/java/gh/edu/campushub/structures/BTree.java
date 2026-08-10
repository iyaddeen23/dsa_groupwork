package gh.edu.campushub.structures;

/**
 * A B-tree of minimum degree {@code t}, built from scratch — the PRD's brief-approved
 * "database index simulation using B-tree pages" alternative to a full production B-tree.
 * Each node models one disk page holding up to {@code 2t-1} keys; a full page splits into
 * two half-full pages and pushes its median key up, exactly as a real DB index page would.
 */
public class BTree<K extends Comparable<K>, V> {

    private final int minDegree; // t: every non-root node has between t-1 and 2t-1 keys
    private Node<K, V> root;
    private int size;

    public BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("minDegree must be >= 2");
        }
        this.minDegree = minDegree;
        this.root = new Node<>(true);
    }

    private static class Node<K, V> {
        final DynamicArray<K> keys = new DynamicArray<>();
        final DynamicArray<V> values = new DynamicArray<>();
        final DynamicArray<Node<K, V>> children = new DynamicArray<>();
        boolean leaf;

        Node(boolean leaf) {
            this.leaf = leaf;
        }

        boolean isFull(int maxKeys) {
            return keys.size() == maxKeys;
        }
    }

    public int size() {
        return size;
    }

    private int maxKeys() {
        return 2 * minDegree - 1;
    }

    public V search(K key) {
        return search(root, key);
    }

    private V search(Node<K, V> node, K key) {
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
            i++;
        }
        if (i < node.keys.size() && key.compareTo(node.keys.get(i)) == 0) {
            return node.values.get(i);
        }
        if (node.leaf) {
            return null;
        }
        return search(node.children.get(i), key);
    }

    public boolean contains(K key) {
        return search(key) != null;
    }

    public void insert(K key, V value) {
        Node<K, V> r = root;
        if (r.isFull(maxKeys())) {
            Node<K, V> newRoot = new Node<>(false);
            newRoot.children.add(r);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, key, value);
        size++;
    }

    /** Splits the full child at {@code index} of {@code parent}, pushing its median key up a page. */
    private void splitChild(Node<K, V> parent, int index) {
        Node<K, V> full = parent.children.get(index);
        Node<K, V> sibling = new Node<>(full.leaf);

        int mid = minDegree - 1;
        K medianKey = full.keys.get(mid);
        V medianValue = full.values.get(mid);

        for (int j = mid + 1; j < full.keys.size(); j++) {
            sibling.keys.add(full.keys.get(j));
            sibling.values.add(full.values.get(j));
        }
        if (!full.leaf) {
            for (int j = mid + 1; j < full.children.size(); j++) {
                sibling.children.add(full.children.get(j));
            }
            while (full.children.size() > mid + 1) {
                full.children.remove(full.children.size() - 1);
            }
        }
        while (full.keys.size() > mid) {
            full.keys.remove(full.keys.size() - 1);
            full.values.remove(full.values.size() - 1);
        }

        parent.children.insert(index + 1, sibling);
        parent.keys.insert(index, medianKey);
        parent.values.insert(index, medianValue);
    }

    private void insertNonFull(Node<K, V> node, K key, V value) {
        int i = node.keys.size() - 1;
        if (node.leaf) {
            node.keys.add(null);
            node.values.add(null);
            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) {
                node.keys.set(i + 1, node.keys.get(i));
                node.values.set(i + 1, node.values.get(i));
                i--;
            }
            node.keys.set(i + 1, key);
            node.values.set(i + 1, value);
        } else {
            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) {
                i--;
            }
            i++;
            if (node.children.get(i).isFull(maxKeys())) {
                splitChild(node, i);
                if (key.compareTo(node.keys.get(i)) > 0) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), key, value);
        }
    }

    /** Tree height in pages (root = 0), useful for the search-trace / node-split report evidence. */
    public int height() {
        int h = 0;
        Node<K, V> cur = root;
        while (!cur.leaf) {
            h++;
            cur = cur.children.get(0);
        }
        return h;
    }
}
