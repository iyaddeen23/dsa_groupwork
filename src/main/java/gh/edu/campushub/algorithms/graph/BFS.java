package gh.edu.campushub.algorithms.graph;

import gh.edu.campushub.structures.DynamicArray;
import gh.edu.campushub.structures.HashSet;
import gh.edu.campushub.structures.Queue;
import gh.edu.campushub.structures.graph.Edge;
import gh.edu.campushub.structures.graph.Graph;

/**
 * Breadth-first search — visits vertices in non-decreasing distance (edge
 * count) from the source, using the from-scratch {@link Queue}. Answers
 * "which locations are reachable, and in what order by hop count."
 */
public final class BFS {

    private BFS() {
    }

    public static <V> DynamicArray<V> traverse(Graph<V> graph, V source) {
        DynamicArray<V> order = new DynamicArray<>();
        HashSet<V> visited = new HashSet<>();
        Queue<V> frontier = new Queue<>();

        visited.add(source);
        frontier.enqueue(source);

        while (!frontier.isEmpty()) {
            V current = frontier.dequeue();
            order.add(current);
            for (Edge<V> edge : graph.neighbors(current)) {
                if (!visited.contains(edge.getTo())) {
                    visited.add(edge.getTo());
                    frontier.enqueue(edge.getTo());
                }
            }
        }
        return order;
    }

    /** The set of every vertex reachable from {@code source}, including itself. */
    public static <V> HashSet<V> reachableSet(Graph<V> graph, V source) {
        HashSet<V> visited = new HashSet<>();
        for (V v : traverse(graph, source)) {
            visited.add(v);
        }
        return visited;
    }
}
