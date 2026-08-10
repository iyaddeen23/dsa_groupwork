package gh.edu.campushub.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Loads {@code schema.sql} from the classpath and (re)creates the 6 database tables. */
public final class SchemaInitializer {

    private SchemaInitializer() {
    }

    public static boolean tableExists(Connection connection, String tableName) {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name = ?";
        try (java.sql.PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check for table " + tableName, e);
        }
    }

    public static int countRows(Connection connection, String tableName) {
        try (Statement statement = connection.createStatement();
             java.sql.ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count rows in " + tableName, e);
        }
    }

    public static void initialize(Connection connection) {
        String sql = readSchemaResource();
        try (Statement statement = connection.createStatement()) {
            for (String rawStatement : sql.split(";")) {
                String trimmed = rawStatement.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize schema", e);
        }
    }

    private static String readSchemaResource() {
        try (InputStream in = SchemaInitializer.class.getResourceAsStream("/schema.sql")) {
            if (in == null) {
                throw new IllegalStateException("schema.sql not found on classpath");
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().startsWith("--")) {
                        sb.append(line).append('\n');
                    }
                }
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read schema.sql", e);
        }
    }
}
