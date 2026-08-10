package gh.edu.campushub.algorithms.dp;

import gh.edu.campushub.structures.DynamicArray;

/**
 * 0/1 knapsack via bottom-up dynamic programming (tabulation) — selects the
 * subset of {@link KnapsackItem}s maximizing total value without exceeding
 * {@code capacity} resource-hours. Models "which service requests do we
 * commit to this shift, given a fixed resource-hour budget."
 *
 * <p>Recurrence: table[i][c] = max value achievable using the first i items
 * within capacity c.
 * <pre>
 *   table[i][c] = table[i-1][c]                                     if items[i-1].weight() > c
 *   table[i][c] = max(table[i-1][c], table[i-1][c-w] + v)            otherwise
 * </pre>
 * Base case: table[0][c] = 0 for all c (no items chosen). This is optimal —
 * unlike the ratio-greedy approach in {@link GreedyKnapsack} — because the
 * table considers BOTH "skip item i" and "take item i" at every cell, so no
 * reachable (item-count, capacity) state is ever discarded before it's
 * compared against the alternative.
 */
public final class Knapsack {

    private Knapsack() {
    }

    public record Result(DynamicArray<KnapsackItem> selected, int totalValue, int[][] table) {
    }

    public static Result solve(KnapsackItem[] items, int capacity) {
        int n = items.length;
        int[][] table = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            KnapsackItem item = items[i - 1];
            for (int c = 0; c <= capacity; c++) {
                table[i][c] = table[i - 1][c];
                if (item.weight() <= c) {
                    int withItem = table[i - 1][c - item.weight()] + item.value();
                    if (withItem > table[i][c]) {
                        table[i][c] = withItem;
                    }
                }
            }
        }

        DynamicArray<KnapsackItem> selected = reconstruct(items, table, capacity);
        return new Result(selected, table[n][capacity], table);
    }

    /** Backtracks through the table: item i was taken iff the value changed from row i-1 to row i at that capacity. */
    private static DynamicArray<KnapsackItem> reconstruct(KnapsackItem[] items, int[][] table, int capacity) {
        DynamicArray<KnapsackItem> selected = new DynamicArray<>();
        int c = capacity;
        for (int i = items.length; i > 0; i--) {
            if (table[i][c] != table[i - 1][c]) {
                KnapsackItem item = items[i - 1];
                selected.insert(0, item);
                c -= item.weight();
            }
        }
        return selected;
    }
}
