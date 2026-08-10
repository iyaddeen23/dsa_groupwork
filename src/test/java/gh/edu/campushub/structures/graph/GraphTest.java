package gh.edu.campushub.structures.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void undirectedEdge_appearsBothDirectionsInAdjacencyListAndMatrix() {
        Graph<String> g = new Graph<>(false);
        g.addEdge("A", "B", 2.5);

        assertEquals(2.5, g.weightBetween("A", "B"));
        assertEquals(2.5, g.weightBetween("B", "A"));

        boolean foundAtoB = false;
        for (Edge<String> e : g.neighbors("A")) {
            if (e.getTo().equals("B")) foundAtoB = true;
        }
        assertTrue(foundAtoB);
    }

    @Test
    void directedEdge_onlyAppearsOneDirection() {
        Graph<String> g = new Graph<>(true);
        g.addEdge("A", "B", 1.0);
        assertEquals(1.0, g.weightBetween("A", "B"));
        assertEquals(Graph.NO_EDGE, g.weightBetween("B", "A"));
    }

    @Test
    void adjacencyMatrixGrowsAsVerticesAreAdded() {
        Graph<String> g = new Graph<>(false);
        g.addVertex("A");
        g.addVertex("B");
        g.addVertex("C");
        double[][] matrix = g.adjacencyMatrixSnapshot();
        assertEquals(3, matrix.length);
        assertEquals(3, matrix[0].length);
    }

    @Test
    void disconnectedGraph_hasNoEdgeBetweenComponents() {
        Graph<String> g = new Graph<>(false);
        g.addEdge("A", "B", 1.0);
        g.addVertex("Z"); // isolated vertex, no edges at all
        assertEquals(Graph.NO_EDGE, g.weightBetween("A", "Z"));
        assertTrue(g.neighborsAt(g.indexOf("Z")).isEmpty());
    }

    @Test
    void unknownVertex_indexOfThrows() {
        Graph<String> g = new Graph<>(false);
        g.addVertex("A");
        assertThrows(java.util.NoSuchElementException.class, () -> g.indexOf("does-not-exist"));
    }
}
