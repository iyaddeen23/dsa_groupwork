package gh.edu.campushub.db;

import gh.edu.campushub.model.AvailabilityStatus;
import gh.edu.campushub.model.Location;
import gh.edu.campushub.model.RequestStatus;
import gh.edu.campushub.model.Resource;
import gh.edu.campushub.model.Road;
import gh.edu.campushub.model.ServiceRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDateTime;

/**
 * Reads the CSV seed files (M2: "Import CSV data, validate records") and writes
 * them into the SQLite database through the DAOs. A malformed row is skipped
 * with a warning rather than aborting the whole load — one bad record shouldn't
 * cost the other 479.
 */
public class CsvDataLoader {

    private final LocationDao locationDao;
    private final RoadDao roadDao;
    private final ResourceDao resourceDao;
    private final ServiceRequestDao serviceRequestDao;

    public CsvDataLoader(Connection connection) {
        this.locationDao = new LocationDao(connection);
        this.roadDao = new RoadDao(connection);
        this.resourceDao = new ResourceDao(connection);
        this.serviceRequestDao = new ServiceRequestDao(connection);
    }

    /** Loads all four seed CSVs from {@code dataDir}, in FK-safe order (locations before roads/resources/requests). */
    public LoadReport loadAll(Path dataDir) {
        int locations = loadLocations(dataDir.resolve("locations.csv"));
        int roads = loadRoads(dataDir.resolve("roads.csv"));
        int resources = loadResources(dataDir.resolve("resources.csv"));
        int requests = loadServiceRequests(dataDir.resolve("service_requests.csv"));
        return new LoadReport(locations, roads, resources, requests);
    }

    public int loadLocations(Path csvPath) {
        return loadRows(csvPath, 6, fields -> {
            Location location = new Location(
                    fields[0], fields[1], fields[2], fields[3],
                    Double.parseDouble(fields[4]), Double.parseDouble(fields[5]));
            locationDao.insert(location);
        });
    }

    public int loadRoads(Path csvPath) {
        return loadRows(csvPath, 6, fields -> {
            Road road = new Road(
                    fields[0], fields[1], fields[2],
                    Double.parseDouble(fields[3]), Double.parseDouble(fields[4]), Double.parseDouble(fields[5]));
            roadDao.insert(road);
        });
    }

    public int loadResources(Path csvPath) {
        return loadRows(csvPath, 5, fields -> {
            Resource resource = new Resource(
                    fields[0], fields[1], fields[2],
                    Integer.parseInt(fields[3]), AvailabilityStatus.valueOf(fields[4]));
            resourceDao.insert(resource);
        });
    }

    public int loadServiceRequests(Path csvPath) {
        return loadRows(csvPath, 8, fields -> {
            ServiceRequest request = new ServiceRequest(
                    fields[0], fields[1], fields[2], fields[3],
                    Integer.parseInt(fields[4]),
                    LocalDateTime.parse(fields[5]),
                    LocalDateTime.parse(fields[6]),
                    RequestStatus.valueOf(fields[7]));
            serviceRequestDao.insert(request);
        });
    }

    @FunctionalInterface
    private interface RowHandler {
        void handle(String[] fields) throws Exception;
    }

    private int loadRows(Path csvPath, int expectedColumns, RowHandler handler) {
        if (!Files.exists(csvPath)) {
            System.err.println("[CsvDataLoader] file not found, skipping: " + csvPath);
            return 0;
        }
        int loaded = 0;
        int lineNumber = 0;
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // header
            lineNumber++;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                if (fields.length != expectedColumns) {
                    System.err.println("[CsvDataLoader] " + csvPath.getFileName() + ":" + lineNumber +
                            " expected " + expectedColumns + " columns, got " + fields.length + " — skipped");
                    continue;
                }
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }
                try {
                    handler.handle(fields);
                    loaded++;
                } catch (Exception e) {
                    System.err.println("[CsvDataLoader] " + csvPath.getFileName() + ":" + lineNumber +
                            " invalid row (" + e.getMessage() + ") — skipped");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + csvPath, e);
        }
        return loaded;
    }

    public record LoadReport(int locations, int roads, int resources, int serviceRequests) {
        @Override
        public String toString() {
            return String.format("Loaded: %d locations, %d roads, %d resources, %d service requests",
                    locations, roads, resources, serviceRequests);
        }
    }
}
