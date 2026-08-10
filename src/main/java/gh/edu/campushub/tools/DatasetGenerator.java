package gh.edu.campushub.tools;

import gh.edu.campushub.config.TeamConfig;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Generates the project's seed dataset: 50 University of Ghana, Legon campus
 * locations, 100+ connecting roads, 300 service requests and 30 resources —
 * all script-generated (PRD Section 3: "Manually typing 480+ records wastes a
 * day we don't have") and reproducible via {@link TeamConfig#RANDOM_SEED}.
 *
 * Location names are real/plausible public campus landmarks; no personal data
 * of any real individual is used anywhere in the generated records.
 *
 * Run with: mvn exec:java -Dexec.mainClass=gh.edu.campushub.tools.DatasetGenerator
 * or simply execute main() from the IDE. Writes into ./data/.
 */
public final class DatasetGenerator {

    /** Name paired 1:1 with its location_type, so the generated data stays semantically consistent. */
    private static final String[][] LOCATIONS = {
            {"Balme Library", "Library"}, {"Department of Computer Science", "Academic"},
            {"UG Legon Hospital", "Health"}, {"Commonwealth Hall", "Hostel"}, {"Volta Hall", "Hostel"},
            {"Akuafo Hall", "Hostel"}, {"Legon Hall", "Hostel"}, {"Mensah Sarbah Hall", "Hostel"},
            {"Jean Nelson Aggrey Hall", "Hostel"}, {"Elizabeth Sey Hall", "Hostel"},
            {"Pentagon Hostel Block A", "Hostel"}, {"Pentagon Hostel Block B", "Hostel"},
            {"Diaspora Hall", "Hostel"}, {"TF Hostel", "Hostel"}, {"Bani Hostel", "Hostel"},
            {"Valco Trust Hostel", "Hostel"}, {"International Students Hostel", "Hostel"},
            {"Great Hall", "Admin"}, {"Central Cafeteria", "Dining"}, {"Night Market", "Market"},
            {"N-Block Lecture Theatre", "Academic"}, {"JQB School of Business", "Academic"},
            {"Institute of African Studies", "Academic"}, {"School of Engineering Sciences", "Academic"},
            {"Department of Physics", "Lab"}, {"Department of Chemistry", "Lab"},
            {"Department of Mathematics", "Academic"}, {"Department of Biochemistry", "Lab"},
            {"School of Law", "Academic"}, {"Noguchi Memorial Institute for Medical Research", "Lab"},
            {"UG Sports Stadium", "Sports"}, {"UG Basketball Court", "Sports"}, {"Athletic Oval", "Sports"},
            {"Registry Building", "Admin"}, {"Central Administration Block", "Admin"},
            {"GCB Bank Legon Branch", "Bank"}, {"Ecobank Legon Branch", "Bank"},
            {"Legon Hall Annex (Annie Jiagge)", "Hostel"}, {"Volta Hall Annex", "Hostel"},
            {"Akuafo Hall Annex", "Hostel"}, {"Limann Hostel", "Hostel"}, {"Jubilee Hall", "Hostel"},
            {"Alexander Kwapong Hall", "Hostel"}, {"Jones Quartey Building", "Academic"},
            {"Trust Towers Hostel", "Hostel"}, {"Main Gate Shuttle Stop", "ShuttleStop"},
            {"Okponglo Shuttle Stop", "ShuttleStop"}, {"Legon Interchange Shuttle Stop", "ShuttleStop"},
            {"University Bookshop", "Market"}, {"Central Mosque", "Religious"}
    };

    private static final String[] AREAS = {
            "North Campus", "Central Campus", "South Campus", "Commonwealth Close",
            "Volta Close", "Legon Hall Precinct", "Okponglo", "Legon Interchange"
    };

    private static final String[] REQUEST_CATEGORIES = {
            "Medical", "Maintenance", "Security", "ITSupport", "Cleaning",
            "Plumbing", "Electrical", "Shuttle", "Document", "Facilities"
    };

    private static final String[] RESOURCE_TYPES = {
            "ShuttleVan", "MaintenanceRider", "SecurityOfficer", "ITTechnician",
            "CleaningCrew", "Electrician", "Plumber"
    };

    private static final String[] AVAILABILITY = {"AVAILABLE", "AVAILABLE", "AVAILABLE", "BUSY", "BUSY", "OFFLINE"};
    private static final String[] STATUSES = {
            "NEW", "NEW", "NEW", "ASSIGNED", "ASSIGNED", "IN_PROGRESS", "IN_PROGRESS",
            "COMPLETED", "COMPLETED", "COMPLETED", "COMPLETED", "COMPLETED", "COMPLETED", "COMPLETED",
            "CANCELLED"
    };

    private static final int LOCATION_COUNT = 50;
    private static final int ROAD_COUNT = 110;
    private static final int RESOURCE_COUNT = 30;
    private static final int REQUEST_COUNT = 300;

    private static final LocalDateTime ANCHOR = LocalDateTime.of(2026, 7, 1, 6, 0);

    private final Random random = new Random(TeamConfig.RANDOM_SEED);

    public static void main(String[] args) throws IOException {
        Path dataDir = args.length > 0 ? Path.of(args[0]) : Path.of("data");
        Files.createDirectories(dataDir);
        new DatasetGenerator().generate(dataDir);
        System.out.println("Dataset written to " + dataDir.toAbsolutePath());
        System.out.println("RANDOM_SEED=" + TeamConfig.RANDOM_SEED
                + " TEAM_DIGIT_SUM=" + TeamConfig.TEAM_DIGIT_SUM
                + " TEAM_TAIL_SUM=" + TeamConfig.TEAM_TAIL_SUM);
    }

    public void generate(Path dataDir) throws IOException {
        String[] locationIds = writeLocations(dataDir.resolve("locations.csv"));
        writeRoads(dataDir.resolve("roads.csv"), locationIds);
        writeResources(dataDir.resolve("resources.csv"), locationIds);
        writeServiceRequests(dataDir.resolve("service_requests.csv"), locationIds);
    }

    private String[] writeLocations(Path path) throws IOException {
        String[] ids = new String[LOCATION_COUNT];
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))) {
            out.println("location_id,name,area,location_type,x_coord,y_coord");
            for (int i = 0; i < LOCATION_COUNT; i++) {
                String id = String.format("L%03d", i + 1);
                ids[i] = id;
                String name = LOCATIONS[i % LOCATIONS.length][0];
                String type = LOCATIONS[i % LOCATIONS.length][1];
                String area = AREAS[random.nextInt(AREAS.length)];
                double x = 5.60 + random.nextDouble() * 0.08;  // Legon campus bounding box
                double y = 0.15 + random.nextDouble() * 0.08;
                out.printf("%s,%s,%s,%s,%.4f,%.4f%n", id, name, area, type, x, y);
            }
        }
        return ids;
    }

    private void writeRoads(Path path, String[] locationIds) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))) {
            out.println("road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight");
            int roadNumber = 1;
            java.util.Set<String> seenPairs = new java.util.HashSet<>();

            // Guarantee full connectivity first: a random spanning tree over all locations.
            for (int i = 1; i < locationIds.length; i++) {
                int parent = random.nextInt(i);
                roadNumber = writeRoad(out, roadNumber, locationIds[parent], locationIds[i], seenPairs);
            }

            // Then add extra random edges up to ROAD_COUNT for realistic path redundancy.
            while (roadNumber <= ROAD_COUNT) {
                String from = locationIds[random.nextInt(locationIds.length)];
                String to = locationIds[random.nextInt(locationIds.length)];
                if (from.equals(to)) {
                    continue;
                }
                roadNumber = writeRoad(out, roadNumber, from, to, seenPairs);
            }
        }
    }

    private int writeRoad(PrintWriter out, int roadNumber, String from, String to, java.util.Set<String> seenPairs) {
        String key = from.compareTo(to) < 0 ? from + "-" + to : to + "-" + from;
        if (!seenPairs.add(key)) {
            return roadNumber;
        }
        double distanceKm = 0.1 + random.nextDouble() * 2.4;
        double travelTimeMin = distanceKm * (3.5 + random.nextDouble() * 2.5);
        double conditionWeight = 0.9 + random.nextDouble() * 0.6;
        out.printf("R%03d,%s,%s,%.2f,%.1f,%.2f%n", roadNumber, from, to, distanceKm, travelTimeMin, conditionWeight);
        return roadNumber + 1;
    }

    private void writeResources(Path path, String[] locationIds) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))) {
            out.println("resource_id,resource_type,home_location_id,capacity,availability_status");
            for (int i = 1; i <= RESOURCE_COUNT; i++) {
                String id = String.format("V%03d", i);
                String type = RESOURCE_TYPES[random.nextInt(RESOURCE_TYPES.length)];
                String home = locationIds[random.nextInt(locationIds.length)];
                int capacity = 1 + random.nextInt(6);
                String availability = AVAILABILITY[random.nextInt(AVAILABILITY.length)];
                out.printf("%s,%s,%s,%d,%s%n", id, type, home, capacity, availability);
            }
        }
    }

    private void writeServiceRequests(Path path, String[] locationIds) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))) {
            out.println("request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status");
            for (int i = 1; i <= REQUEST_COUNT; i++) {
                String id = String.format("Q%03d", i);
                String source = locationIds[random.nextInt(locationIds.length)];
                String destination;
                do {
                    destination = locationIds[random.nextInt(locationIds.length)];
                } while (destination.equals(source));

                String category = REQUEST_CATEGORIES[random.nextInt(REQUEST_CATEGORIES.length)];
                int urgency = sampleUrgency();
                LocalDateTime submitted = ANCHOR.plusMinutes(random.nextInt(60 * 24 * 14)); // spread over 14 days
                int deadlineSlackMin = switch (urgency) {
                    case 5 -> 30 + random.nextInt(30);
                    case 4 -> 60 + random.nextInt(60);
                    case 3 -> 120 + random.nextInt(120);
                    case 2 -> 240 + random.nextInt(240);
                    default -> 480 + random.nextInt(480);
                };
                LocalDateTime deadline = submitted.plusMinutes(deadlineSlackMin);
                String status = STATUSES[random.nextInt(STATUSES.length)];

                out.printf("%s,%s,%s,%s,%d,%s,%s,%s%n",
                        id, source, destination, category, urgency, submitted, deadline, status);
            }
        }
    }

    /** Skews toward low/medium urgency with a smaller tail of critical requests, like real dispatch data. */
    private int sampleUrgency() {
        int roll = random.nextInt(100);
        if (roll < 35) return 1;
        if (roll < 65) return 2;
        if (roll < 85) return 3;
        if (roll < 95) return 4;
        return 5;
    }
}
