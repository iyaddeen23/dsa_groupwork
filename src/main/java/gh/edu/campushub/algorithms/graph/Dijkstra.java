package gh.edu.campushub.algorithms.graph;

import gh.edu.campushub.structures.BinaryHeap;
import gh.edu.campushub.structures.DynamicArray;
import gh.edu.campushub.structures.HashSet;
import gh.edu.campushub.structures.HashTable;
import gh.edu.campushub.structures.graph.Edge;
import gh.edu.campushub.structures.graph.Graph;

import java.util.Comparator;

/**
 * Dijkstra's shortest-path algorithm using the from-scratch {@link BinaryHeap}
 * as the priority queue (lazy-deletion variant: stale, already-improved
 * entries are just skipped when popped rather than decrease-keyed in place).
 *
 * <p>Precondition: all edge weights must be non-negative — {@link Graph}
 * weights here come from {@code Road.routeCost()}, which is always positive.
 */
public final class Dijkstra {

    private Dijkstra() {
    }

    private record HeapEntry<V>(V vertex, double distance) {
    }

    public static class Result<V> {
        public final HashTable<V, Double> distances;
        public final HashTable<V, V> predecessors;

        Result(HashTable<V, Double> distances, HashTable<V, V> predecessors) {
            this.distances = distances;
            this.predecessors = predecessors;
        }

        /** Reconstructs the shortest path from the Dijkstra source to {@code target}, or empty if unreachable. */
        public DynamicArray<V> pathTo(V target) {
            DynamicArray<V> path = new DynamicArray<>();
            if (!distances.containsKey(target)) {
                return path;
            }
            V current = target;
            while (current != null) {
                path.insert(0, current);
                current = predecessors.get(current);
            }
            return path;
        }
    }

    public static <V> Result<V> run(Graph<V> graph, V source) {
        HashTable<V, Double> distances = new HashTable<>();
        HashTable<V, V> predecessors = new HashTable<>();
        HashSet<V> finalized = new HashSet<>();

        Comparator<HeapEntry<V>> byDistance = Comparator.comparingDouble(HeapEntry::distance);
        BinaryHeap<HeapEntry<V>> frontier = new BinaryHeap<>(byDistance);

        distances.put(source, 0.0);
        frontier.insert(new HeapEntry<>(source, 0.0));

        while (!frontier.isEmpty()) {
            HeapEntry<V> entry = frontier.extractRoot();
            if (finalized.contains(entry.vertex())) {
                continue; // stale entry from an earlier, worse relaxation
            }
            finalized.add(entry.vertex());

            for (Edge<V> edge : graph.neighbors(entry.vertex())) {
                double candidate = entry.distance() + edge.getWeight();
                Double known = distances.get(edge.getTo());
                if (known == null || candidate < known) {
                    distances.put(edge.getTo(), candidate);
                    predecessors.put(edge.getTo(), entry.vertex());
                    frontier.insert(new HeapEntry<>(edge.getTo(), candidate));
                }
            }
        }
        return new Result<>(distances, predecessors);
    }
}
