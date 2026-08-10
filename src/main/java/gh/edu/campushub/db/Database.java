package gh.edu.campushub.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Owns the single SQLite connection the whole app reads and writes through. */
public final class Database {

    public static final String DEFAULT_DB_PATH = "campus_hub.db";

    private static Connection connection;
    private static String currentUrl;

    private Database() {
    }

    public static synchronized Connection getConnection() {
        return getConnection(DEFAULT_DB_PATH);
    }

    public static synchronized Connection getConnection(String dbPath) {
        String url = "jdbc:sqlite:" + dbPath;
        try {
            if (connection == null || connection.isClosed() || !url.equals(currentUrl)) {
                connection = DriverManager.getConnection(url);
                connection.createStatement().execute("PRAGMA foreign_keys = ON;");
                currentUrl = url;
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open database at " + dbPath, e);
        }
    }

    public static synchronized void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close database connection", e);
        } finally {
            connection = null;
            currentUrl = null;
        }
    }
}
