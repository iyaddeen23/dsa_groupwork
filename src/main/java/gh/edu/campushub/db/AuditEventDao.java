package gh.edu.campushub.db;

import gh.edu.campushub.model.AuditEvent;
import gh.edu.campushub.structures.DynamicArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

/** Backs the stack-based undo/audit log — every mutating console action is persisted here. */
public class AuditEventDao {

    private final Connection connection;

    public AuditEventDao(Connection connection) {
        this.connection = connection;
    }

    /** Inserts the event and returns it with its generated event_id filled in. */
    public AuditEvent insert(AuditEvent event) {
        String sql = "INSERT INTO audit_events (event_type, entity_table, entity_id, before_state, after_state, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getEventType());
            ps.setString(2, event.getEntityTable());
            ps.setString(3, event.getEntityId());
            ps.setString(4, event.getBeforeState());
            ps.setString(5, event.getAfterState());
            ps.setString(6, event.getCreatedAt().toString());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                Integer generatedId = keys.next() ? keys.getInt(1) : null;
                return new AuditEvent(generatedId, event.getEventType(), event.getEntityTable(),
                        event.getEntityId(), event.getBeforeState(), event.getAfterState(), event.getCreatedAt());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert audit event", e);
        }
    }

    public void deleteById(int eventId) {
        String sql = "DELETE FROM audit_events WHERE event_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete audit event " + eventId, e);
        }
    }

    public DynamicArray<AuditEvent> findAll() {
        DynamicArray<AuditEvent> result = new DynamicArray<>();
        String sql = "SELECT event_id, event_type, entity_table, entity_id, before_state, after_state, created_at " +
                "FROM audit_events ORDER BY event_id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load audit events", e);
        }
        return result;
    }

    private AuditEvent mapRow(ResultSet rs) throws SQLException {
        return new AuditEvent(
                rs.getInt("event_id"),
                rs.getString("event_type"),
                rs.getString("entity_table"),
                rs.getString("entity_id"),
                rs.getString("before_state"),
                rs.getString("after_state"),
                LocalDateTime.parse(rs.getString("created_at"))
        );
    }
}
