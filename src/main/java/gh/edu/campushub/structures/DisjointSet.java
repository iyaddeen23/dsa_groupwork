package gh.edu.campushub.structures;

/**
 * Union-Find / Disjoint Set Union built from scratch, over integer element IDs
 * 0..n-1. Uses union by rank plus full path compression, giving near O(1)
 * amortized find/union — the connectivity check Kruskal's MST relies on.
 */
public class DisjointSet {

    private final int[] parent;
    private final int[] rank;
    private int setCount;

    public DisjointSet(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
        setCount = n;
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // path compression
        }
        return parent[x];
    }

    /** Merges the sets containing x and y. Returns true if they were previously disjoint (an edge was "useful"). */
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) {
            return false;
        }
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        setCount--;
        return true;
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    public int setCount() {
        return setCount;
    }

    public int size() {
        return parent.length;
    }
}
