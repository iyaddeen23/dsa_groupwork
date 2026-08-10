package gh.edu.campushub.algorithms.graph;

import gh.edu.campushub.structures.DynamicArray;
import gh.edu.campushub.structures.HashSet;
import gh.edu.campushub.structures.Stack;
import gh.edu.campushub.structures.graph.Edge;
import gh.edu.campushub.structures.graph.Graph;

/**
 * Depth-first search — plunges down one path as far as possible before
 * backtracking, using the from-scratch {@link Stack} (iterative, so it
 * can't blow the call stack on a large graph). Marks a vertex visited only
 * when it's popped, matching classic DFS discovery order.
 */
public final class DFS {

    private DFS() {
    }

    public static <V> DynamicArray<V> traverse(Graph<V> graph, V source) {
        DynamicArray<V> order = new DynamicArray<>();
        HashSet<V> visited = new HashSet<>();
        Stack<V> stack = new Stack<>();

        stack.push(source);
        while (!stack.isEmpty()) {
            V current = stack.pop();
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);
            order.add(current);
            for (Edge<V> edge : graph.neighbors(current)) {
                if (!visited.contains(edge.getTo())) {
                    stack.push(edge.getTo());
                }
            }
        }
        return order;
    }
}
