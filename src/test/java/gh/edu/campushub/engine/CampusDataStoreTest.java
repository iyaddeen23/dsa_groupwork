package gh.edu.campushub.engine;

import gh.edu.campushub.algorithms.graph.BFS;
import gh.edu.campushub.db.CsvDataLoader;
import gh.edu.campushub.db.SchemaInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** End-to-end check: DB -> CampusDataStore -> every custom structure is populated and internally consistent. */
class CampusDataStoreTest {

    private Connection connection;
    private CampusDataStore store;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initialize(connection);
        new CsvDataLoader(connection).loadAll(Path.of("data"));
        store = new CampusDataStore(connection);
        store.loadFromDatabase();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void allFourEntityListsAreFullyLoaded() {
        assertEquals(50, store.locations().size());
        assertEquals(110, store.roads().size());
        assertEquals(30, store.resources().size());
        assertEquals(300, store.requests().size());
    }

    @Test
    void hashIndexAndAvlIndexAgreeWithEachOther() {
        var byId = store.locationById().get("L001");
        var byName = store.locationByName().search(byId.getName());
        assertEquals(byId.getLocationId(), byName.getLocationId());
    }

    @Test
    void roadNetworkGraph_isFullyConnected() {
        String anyLocation = store.locations().get(0).getLocationId();
        var reachable = BFS.reachableSet(store.roadNetwork(), anyLocation);
        assertEquals(store.locations().size(), reachable.size(),
                "the dataset generator builds a spanning tree first, so every location must be reachable");
    }

    @Test
    void dispatchHeap_ordersActiveRequestsByUrgencyPressure() {
        var heap = store.buildDispatchHeap(LocalDateTime.now());
        assertTrue(heap.size() > 0);
        double first = heap.extractRoot().dispatchScore(LocalDateTime.now());
        if (!heap.isEmpty()) {
            double second = heap.extractRoot().dispatchScore(LocalDateTime.now());
            assertTrue(first <= second, "heap must extract in non-decreasing dispatch-score order");
        }
    }
}
