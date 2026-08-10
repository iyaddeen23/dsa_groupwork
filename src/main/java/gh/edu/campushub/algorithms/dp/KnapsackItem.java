package gh.edu.campushub.algorithms.dp;

/**
 * One candidate for budget-constrained selection: a service request (or
 * resource allocation) costing {@code weight} resource-hours and worth
 * {@code value} priority points if served.
 */
public record KnapsackItem(String id, int weight, int value) {
    public double ratio() {
        return (double) value / weight;
    }
}
