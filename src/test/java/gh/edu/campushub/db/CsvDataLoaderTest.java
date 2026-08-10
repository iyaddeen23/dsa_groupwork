package gh.edu.campushub.db;

import gh.edu.campushub.model.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CsvDataLoaderTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initialize(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void loadAll_importsEveryRowFromTheProjectDataset() {
        CsvDataLoader loader = new CsvDataLoader(connection);
        CsvDataLoader.LoadReport report = loader.loadAll(Path.of("data"));

        assertEquals(50, report.locations());
        assertEquals(110, report.roads());
        assertEquals(30, report.resources());
        assertEquals(300, report.serviceRequests());
    }

    @Test
    void loadedLocation_roundTripsCorrectlyThroughTheDatabase() {
        new CsvDataLoader(connection).loadAll(Path.of("data"));
        Location location = new LocationDao(connection).findById("L001");
        assertNotNull(location);
        assertEquals("Balme Library", location.getName());
        assertEquals("Library", location.getLocationType());
    }

    @Test
    void malformedCsvRow_isSkippedWithoutAbortingTheWholeLoad(@org.junit.jupiter.api.io.TempDir Path tempDir) throws Exception {
        Path badFile = tempDir.resolve("locations.csv");
        java.nio.file.Files.writeString(badFile, """
                location_id,name,area,location_type,x_coord,y_coord
                L001,Good Location,Area,Academic,5.6,0.2
                L002,Bad Row,MissingFields
                L003,Also Good,Area,Academic,5.7,0.3
                """);
        CsvDataLoader loader = new CsvDataLoader(connection);
        int loaded = loader.loadLocations(badFile);
        assertEquals(2, loaded, "the malformed row (wrong column count) should be skipped, not crash the load");
    }

    @Test
    void missingCsvFile_returnsZeroWithoutThrowing() {
        CsvDataLoader loader = new CsvDataLoader(connection);
        int loaded = loader.loadLocations(Path.of("data", "does_not_exist.csv"));
        assertEquals(0, loaded);
    }
}
