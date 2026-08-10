package gh.edu.campushub.algorithms.graph;

/** One edge in a minimum spanning tree result, shared by {@link Prim} and {@link Kruskal}. */
public record MstEdge<V>(V from, V to, double weight) {
    @Override
    public String toString() {
        return String.format("%s -- %s (w=%.3f)", from, to, weight);
    }
}
