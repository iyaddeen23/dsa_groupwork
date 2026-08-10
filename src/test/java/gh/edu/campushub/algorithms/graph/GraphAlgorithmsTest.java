package gh.edu.campushub.algorithms.graph;

import gh.edu.campushub.structures.DynamicArray;
import gh.edu.campushub.structures.graph.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphAlgorithmsTest {

    private Graph<String> sampleGraph() {
        Graph<String> g = new Graph<>(false);
        g.addEdge("A", "B", 1);
        g.addEdge("A", "C", 4);
        g.addEdge("B", "C", 2);
        g.addEdge("B", "D", 5);
        g.addEdge("C", "D", 1);
        return g;
    }

    @Test
    void bfs_visitsEveryReachableVertexExactlyOnce() {
        DynamicArray<String> order = BFS.traverse(sampleGraph(), "A");
        assertEquals(4, order.size());
        assertEquals("A", order.get(0));
    }

    @Test
    void dfs_visitsEveryReachableVertexExactlyOnce() {
        DynamicArray<String> order = DFS.traverse(sampleGraph(), "A");
        assertEquals(4, order.size());
        assertEquals("A", order.get(0));
    }

    /** Disconnected graph edge case: BFS/DFS from A must not "see" an isolated vertex Z. */
    @Test
    void disconnectedGraph_reachabilityStopsAtComponentBoundary() {
        Graph<String> g = sampleGraph();
        g.addVertex("Z"); // isolated — no edges to/from it
        assertEquals(4, BFS.traverse(g, "A").size());
        assertFalse(BFS.reachableSet(g, "A").contains("Z"));
    }

    @Test
    void dijkstra_findsShortestKnownPathAndCost() {
        Dijkstra.Result<String> result = Dijkstra.run(sampleGraph(), "A");
        // A->B->C->D = 1+2+1 = 4, cheaper than A->C->D = 4+1=5 or A->B->D = 1+5=6
        assertEquals(4.0, result.distances.get("D"), 1e-9);
        DynamicArray<String> path = result.pathTo("D");
        assertEquals("A", path.get(0));
        assertEquals("D", path.get(path.size() - 1));
    }

    /** Unreachable-path edge case: Dijkstra from A must report no distance to an isolated vertex. */
    @Test
    void dijkstra_unreachableVertexHasNoDistance() {
        Graph<String> g = sampleGraph();
        g.addVertex("Z");
        Dijkstra.Result<String> result = Dijkstra.run(g, "A");
        assertNull(result.distances.get("Z"));
        assertTrue(result.pathTo("Z").isEmpty());
    }

    @Test
    void primAndKruskal_agreeOnTotalMstCost() {
        Graph<String> g = sampleGraph();
        MstResult<String> prim = Prim.run(g, "A");
        MstResult<String> kruskal = Kruskal.run(g);

        assertEquals(g.vertexCount() - 1, prim.edges.size());
        assertEquals(g.vertexCount() - 1, kruskal.edges.size());
        assertEquals(prim.totalCost, kruskal.totalCost, 1e-9,
                "Prim and Kruskal must agree on total MST cost even if they pick different edge sets on ties");
    }

    @Test
    void mstCost_isCorrectForKnownGraph() {
        // Minimum spanning tree here is A-B(1), B-C(2), C-D(1) = total 4.
        MstResult<String> result = Kruskal.run(sampleGraph());
        assertEquals(4.0, result.totalCost, 1e-9);
    }
}
