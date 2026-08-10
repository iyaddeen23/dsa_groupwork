package gh.edu.campushub.structures.graph;

/** One weighted edge in a {@link Graph}, from the owning vertex to {@code to}. */
public class Edge<V> {
    private final V to;
    private final double weight;

    public Edge(V to, double weight) {
        this.to = to;
        this.weight = weight;
    }

    public V getTo() { return to; }
    public double getWeight() { return weight; }

    @Override
    public String toString() {
        return String.format("-> %s (w=%.3f)", to, weight);
    }
}
