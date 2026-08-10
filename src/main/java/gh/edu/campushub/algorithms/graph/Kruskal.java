package gh.edu.campushub.algorithms.graph;

import gh.edu.campushub.algorithms.sort.MergeSort;
import gh.edu.campushub.structures.DisjointSet;
import gh.edu.campushub.structures.DynamicArray;
import gh.edu.campushub.structures.graph.Edge;
import gh.edu.campushub.structures.graph.Graph;

import java.util.Comparator;

/**
 * Kruskal's minimum-spanning-tree algorithm: sort every edge by weight (using
 * the from-scratch {@link MergeSort}), then greedily accept an edge unless it
 * would close a cycle — connectivity is checked with the from-scratch
 * {@link DisjointSet} (union by rank + path compression), which is the
 * textbook near-O(1) way to answer "are these two vertices already
 * connected?" during the scan.
 */
public final class Kruskal {

    private Kruskal() {
    }

    public static <V> MstResult<V> run(Graph<V> graph) {
        DynamicArray<MstEdge<V>> allEdges = collectDistinctEdges(graph);

        @SuppressWarnings("unchecked")
        MstEdge<V>[] edgeArray = allEdges.toArray(MstEdge[]::new);
        MergeSort.sort(edgeArray, Comparator.comparingDouble(MstEdge::weight));

        DisjointSet disjointSet = new DisjointSet(graph.vertexCount());
        DynamicArray<MstEdge<V>> mstEdges = new DynamicArray<>();
        double totalCost = 0.0;

        for (MstEdge<V> edge : edgeArray) {
            int fromIndex = graph.indexOf(edge.from());
            int toIndex = graph.indexOf(edge.to());
            if (disjointSet.union(fromIndex, toIndex)) {
                mstEdges.add(edge);
                totalCost += edge.weight();
                if (mstEdges.size() == graph.vertexCount() - 1) {
                    break; // spanning tree complete
                }
            }
        }
        return new MstResult<>(mstEdges, totalCost);
    }

    /** Undirected graphs store each edge twice (once per direction); keep only the from-index &lt; to-index copy. */
    private static <V> DynamicArray<MstEdge<V>> collectDistinctEdges(Graph<V> graph) {
        DynamicArray<MstEdge<V>> edges = new DynamicArray<>();
        for (V vertex : graph.vertices()) {
            int fromIndex = graph.indexOf(vertex);
            for (Edge<V> edge : graph.neighbors(vertex)) {
                int toIndex = graph.indexOf(edge.getTo());
                if (fromIndex < toIndex) {
                    edges.add(new MstEdge<>(vertex, edge.getTo(), edge.getWeight()));
                }
            }
        }
        return edges;
    }
}
