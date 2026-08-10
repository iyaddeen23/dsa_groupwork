package gh.edu.campushub.structures.graph;

import gh.edu.campushub.structures.DynamicArray;
import gh.edu.campushub.structures.HashTable;
import gh.edu.campushub.structures.LinkedList;

/**
 * A weighted graph built from scratch, maintaining BOTH representations the
 * PRD asks for side by side: an adjacency list (fast neighbor iteration —
 * used by BFS/DFS/Dijkstra/Prim) and an adjacency matrix (O(1) edge lookup,
 * used to demonstrate the matrix form and for small dense-graph comparisons).
 */
public class Graph<V> {

    public static final double NO_EDGE = Double.POSITIVE_INFINITY;

    private final boolean directed;
    private final DynamicArray<V> vertices = new DynamicArray<>();
    private final HashTable<V, Integer> indexOf = new HashTable<>();
    private final DynamicArray<LinkedList<Edge<V>>> adjacencyList = new DynamicArray<>();
    private double[][] adjacencyMatrix = new double[0][0];
    private int edgeCount;

    public Graph(boolean directed) {
        this.directed = directed;
    }

    public boolean isDirected() {
        return directed;
    }

    public int vertexCount() {
        return vertices.size();
    }

    public int edgeCount() {
        return edgeCount;
    }

    public void addVertex(V v) {
        if (indexOf.containsKey(v)) {
            return;
        }
        int newIndex = vertices.size();
        vertices.add(v);
        indexOf.put(v, newIndex);
        adjacencyList.add(new LinkedList<>());
        growMatrix(newIndex + 1);
    }

    private void growMatrix(int newSize) {
        double[][] newMatrix = new double[newSize][newSize];
        for (double[] row : newMatrix) {
            java.util.Arrays.fill(row, NO_EDGE);
        }
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            System.arraycopy(adjacencyMatrix[i], 0, newMatrix[i], 0, adjacencyMatrix[i].length);
        }
        adjacencyMatrix = newMatrix;
    }

    public void addEdge(V from, V to, double weight) {
        addVertex(from);
        addVertex(to);
        int fromIndex = indexOf.get(from);
        int toIndex = indexOf.get(to);

        adjacencyList.get(fromIndex).addLast(new Edge<>(to, weight));
        adjacencyMatrix[fromIndex][toIndex] = weight;
        edgeCount++;

        if (!directed) {
            adjacencyList.get(toIndex).addLast(new Edge<>(from, weight));
            adjacencyMatrix[toIndex][fromIndex] = weight;
        }
    }

    public int indexOf(V v) {
        Integer index = indexOf.get(v);
        if (index == null) {
            throw new java.util.NoSuchElementException("vertex not found: " + v);
        }
        return index;
    }

    public boolean hasVertex(V v) {
        return indexOf.containsKey(v);
    }

    public V vertexAt(int index) {
        return vertices.get(index);
    }

    public DynamicArray<V> vertices() {
        return vertices;
    }

    public LinkedList<Edge<V>> neighbors(V v) {
        return adjacencyList.get(indexOf(v));
    }

    public LinkedList<Edge<V>> neighborsAt(int index) {
        return adjacencyList.get(index);
    }

    /** Direct O(1) lookup of the weight between two vertices, or {@link #NO_EDGE} if none exists. */
    public double weightBetween(V from, V to) {
        return adjacencyMatrix[indexOf(from)][indexOf(to)];
    }

    /** Defensive copy of the adjacency matrix for display/report purposes. */
    public double[][] adjacencyMatrixSnapshot() {
        double[][] copy = new double[adjacencyMatrix.length][];
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            copy[i] = adjacencyMatrix[i].clone();
        }
        return copy;
    }
}
