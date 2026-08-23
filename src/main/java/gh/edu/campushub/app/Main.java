package gh.edu.campushub.app;

import gh.edu.campushub.db.CsvDataLoader;
import gh.edu.campushub.db.Database;
import gh.edu.campushub.db.SchemaInitializer;
import gh.edu.campushub.engine.CampusDataStore;

import java.nio.file.Path;
import java.sql.Connection;

public class Main {

    public static void main(String[] args) {
        boolean useGui = true;
        String dbPath = Database.DEFAULT_DB_PATH;

        for (String arg : args) {
            if (arg.equals("--console")) useGui = false;
            else if (!arg.startsWith("--")) dbPath = arg;
        }

        Connection connection = Database.getConnection(dbPath);

        boolean firstRun = !SchemaInitializer.tableExists(connection, "locations");
        if (firstRun) {
            System.out.println("No existing database found at " + dbPath + " - creating schema...");
            SchemaInitializer.initialize(connection);
        }

        if (SchemaInitializer.countRows(connection, "locations") == 0) {
            System.out.println("Locations table is empty - importing seed CSVs from ./data ...");
            CsvDataLoader loader = new CsvDataLoader(connection);
            CsvDataLoader.LoadReport report = loader.loadAll(Path.of("data"));
            System.out.println(report);
        }

        CampusDataStore store = new CampusDataStore(connection);
        store.loadFromDatabase();
        System.out.println("Loaded " + store.locations().size() + " locations, " + store.roads().size()
                + " roads, " + store.resources().size() + " resources, " + store.requests().size() + " requests.");

        if (useGui) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                SwingApp app = new SwingApp(store);
                app.setVisible(true);
            });
        } else {
            new App(store).run();
            Database.close();
        }
    }
}
