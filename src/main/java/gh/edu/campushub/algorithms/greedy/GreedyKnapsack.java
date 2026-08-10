package gh.edu.campushub.algorithms.greedy;

import gh.edu.campushub.algorithms.dp.KnapsackItem;
import gh.edu.campushub.algorithms.sort.MergeSort;
import gh.edu.campushub.structures.DynamicArray;

import java.util.Comparator;

/**
 * Greedy request-selection under a resource-hour budget: sort items by
 * value-to-weight ratio (descending, via {@link MergeSort}) and take each
 * one that still fits. O(n log n), much cheaper than the O(n * capacity)
 * DP table in {@link gh.edu.campushub.algorithms.dp.Knapsack}.
 *
 * <p><b>This is the required greedy failure case (PRD Section 8).</b> The
 * classic counterexample — {@link #counterexample()} — has 3 items and a
 * budget of 50:
 * <pre>
 *   item A: weight=10, value=60   (ratio 6.0)
 *   item B: weight=20, value=100  (ratio 5.0)
 *   item C: weight=30, value=120  (ratio 4.0)
 * </pre>
 * Greedy takes A then B (weights 10+20=30 &le; 50, value 160), then can't
 * fit C (30 more would need 60 total). Final greedy value: <b>160</b>.
 * <p>
 * The optimal 0/1 selection is B+C (weights 20+30=50 exactly, value
 * 100+120=<b>220</b>) — 37.5% better than greedy, and it doesn't even use
 * item A. Greedy locks in the best ratio first and never revisits that
 * choice, so it can't see that giving up A frees just enough budget for the
 * much more valuable C. This is exactly why the project needs the DP
 * solution for real budget-constrained request selection, not just the
 * greedy heuristic.
 */
public final class GreedyKnapsack {

    private GreedyKnapsack() {
    }

    public record Result(DynamicArray<KnapsackItem> selected, int totalValue) {
    }

    public static Result solve(KnapsackItem[] items, int capacity) {
        KnapsackItem[] byRatioDesc = items.clone();
        MergeSort.sort(byRatioDesc, Comparator.comparingDouble(KnapsackItem::ratio).reversed());

        DynamicArray<KnapsackItem> selected = new DynamicArray<>();
        int remaining = capacity;
        int totalValue = 0;
        for (KnapsackItem item : byRatioDesc) {
            if (item.weight() <= remaining) {
                selected.add(item);
                remaining -= item.weight();
                totalValue += item.value();
            }
        }
        return new Result(selected, totalValue);
    }

    /** The exact items/capacity used in this class's Javadoc counterexample — greedy=160, DP-optimal=220. */
    public static KnapsackItem[] counterexample() {
        return new KnapsackItem[]{
                new KnapsackItem("A", 10, 60),
                new KnapsackItem("B", 20, 100),
                new KnapsackItem("C", 30, 120)
        };
    }

    public static final int COUNTEREXAMPLE_CAPACITY = 50;
}
