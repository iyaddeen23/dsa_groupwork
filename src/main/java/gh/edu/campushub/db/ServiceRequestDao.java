package gh.edu.campushub.db;

import gh.edu.campushub.model.RequestStatus;
import gh.edu.campushub.model.ServiceRequest;
import gh.edu.campushub.structures.DynamicArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ServiceRequestDao {

    private final Connection connection;

    public ServiceRequestDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(ServiceRequest request) {
        String sql = "INSERT INTO service_requests (request_id, source_location_id, destination_location_id, " +
                "category, urgency, time_submitted, deadline, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(request_id) DO UPDATE SET source_location_id=excluded.source_location_id, " +
                "destination_location_id=excluded.destination_location_id, category=excluded.category, " +
                "urgency=excluded.urgency, time_submitted=excluded.time_submitted, deadline=excluded.deadline, " +
                "status=excluded.status";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, request.getRequestId());
            ps.setString(2, request.getSourceLocationId());
            ps.setString(3, request.getDestinationLocationId());
            ps.setString(4, request.getCategory());
            ps.setInt(5, request.getUrgency());
            ps.setString(6, request.getTimeSubmitted().toString());
            ps.setString(7, request.getDeadline().toString());
            ps.setString(8, request.getStatus().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert request " + request.getRequestId(), e);
        }
    }

    public void updateStatus(String requestId, RequestStatus status) {
        String sql = "UPDATE service_requests SET status = ? WHERE request_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, requestId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update request " + requestId, e);
        }
    }

    public DynamicArray<ServiceRequest> findAll() {
        DynamicArray<ServiceRequest> result = new DynamicArray<>();
        String sql = "SELECT request_id, source_location_id, destination_location_id, category, urgency, " +
                "time_submitted, deadline, status FROM service_requests ORDER BY request_id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load service requests", e);
        }
        return result;
    }

    public DynamicArray<ServiceRequest> findByStatus(RequestStatus status) {
        DynamicArray<ServiceRequest> result = new DynamicArray<>();
        String sql = "SELECT request_id, source_location_id, destination_location_id, category, urgency, " +
                "time_submitted, deadline, status FROM service_requests WHERE status = ? ORDER BY request_id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load service requests with status " + status, e);
        }
        return result;
    }

    private ServiceRequest mapRow(ResultSet rs) throws SQLException {
        return new ServiceRequest(
                rs.getString("request_id"),
                rs.getString("source_location_id"),
                rs.getString("destination_location_id"),
                rs.getString("category"),
                rs.getInt("urgency"),
                LocalDateTime.parse(rs.getString("time_submitted")),
                LocalDateTime.parse(rs.getString("deadline")),
                RequestStatus.valueOf(rs.getString("status"))
        );
    }
}
