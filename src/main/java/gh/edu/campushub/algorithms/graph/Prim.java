package gh.edu.campushub.algorithms.graph;

import gh.edu.campushub.structures.BinaryHeap;
import gh.edu.campushub.structures.DynamicArray;
import gh.edu.campushub.structures.HashSet;
import gh.edu.campushub.structures.graph.Edge;
import gh.edu.campushub.structures.graph.Graph;

import java.util.Comparator;

/**
 * Prim's minimum-spanning-tree algorithm: grows a single tree outward,
 * repeatedly adding the cheapest edge that crosses the visited/unvisited
 * boundary, using the from-scratch {@link BinaryHeap} as the frontier
 * (lazy-deletion: stale edges pointing at an already-visited vertex are
 * skipped rather than removed from the heap).
 *
 * <p>Assumes an undirected, connected graph — the campus road network is
 * built connected by construction (see the dataset generator's spanning-tree
 * pass), so every vertex is reachable from {@code start}.
 */
public final class Prim {

    private Prim() {
    }

    private record FrontierEdge<V>(V from, V to, double weight) {
    }

    public static <V> MstResult<V> run(Graph<V> graph, V start) {
        DynamicArray<MstEdge<V>> mstEdges = new DynamicArray<>();
        HashSet<V> visited = new HashSet<>();
        double totalCost = 0.0;

        Comparator<FrontierEdge<V>> byWeight = Comparator.comparingDouble(FrontierEdge::weight);
        BinaryHeap<FrontierEdge<V>> frontier = new BinaryHeap<>(byWeight);

        visited.add(start);
        addFrontierEdges(graph, start, visited, frontier);

        while (!frontier.isEmpty() && visited.size() < graph.vertexCount()) {
            FrontierEdge<V> next = frontier.extractRoot();
            if (visited.contains(next.to())) {
                continue; // stale: both endpoints already in the tree
            }
            visited.add(next.to());
            mstEdges.add(new MstEdge<>(next.from(), next.to(), next.weight()));
            totalCost += next.weight();
            addFrontierEdges(graph, next.to(), visited, frontier);
        }
        return new MstResult<>(mstEdges, totalCost);
    }

    private static <V> void addFrontierEdges(Graph<V> graph, V from, HashSet<V> visited, BinaryHeap<FrontierEdge<V>> frontier) {
        for (Edge<V> edge : graph.neighbors(from)) {
            if (!visited.contains(edge.getTo())) {
                frontier.insert(new FrontierEdge<>(from, edge.getTo(), edge.getWeight()));
            }
        }
    }
}
