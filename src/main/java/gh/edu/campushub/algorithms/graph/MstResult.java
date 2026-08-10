package gh.edu.campushub.algorithms.graph;

import gh.edu.campushub.structures.DynamicArray;

/** Common result shape for {@link Prim} and {@link Kruskal}: the chosen edges and their total cost. */
public class MstResult<V> {
    public final DynamicArray<MstEdge<V>> edges;
    public final double totalCost;

    public MstResult(DynamicArray<MstEdge<V>> edges, double totalCost) {
        this.edges = edges;
        this.totalCost = totalCost;
    }
}
