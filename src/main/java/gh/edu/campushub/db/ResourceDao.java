package gh.edu.campushub.db;

import gh.edu.campushub.model.AvailabilityStatus;
import gh.edu.campushub.model.Resource;
import gh.edu.campushub.structures.DynamicArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResourceDao {

    private final Connection connection;

    public ResourceDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(Resource resource) {
        String sql = "INSERT INTO resources (resource_id, resource_type, home_location_id, capacity, availability_status) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT(resource_id) DO UPDATE SET resource_type=excluded.resource_type, " +
                "home_location_id=excluded.home_location_id, capacity=excluded.capacity, " +
                "availability_status=excluded.availability_status";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, resource.getResourceId());
            ps.setString(2, resource.getResourceType());
            ps.setString(3, resource.getHomeLocationId());
            ps.setInt(4, resource.getCapacity());
            ps.setString(5, resource.getAvailabilityStatus().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert resource " + resource.getResourceId(), e);
        }
    }

    public void updateAvailability(String resourceId, AvailabilityStatus status) {
        String sql = "UPDATE resources SET availability_status = ? WHERE resource_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, resourceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update resource " + resourceId, e);
        }
    }

    public DynamicArray<Resource> findAll() {
        DynamicArray<Resource> result = new DynamicArray<>();
        String sql = "SELECT resource_id, resource_type, home_location_id, capacity, availability_status " +
                "FROM resources ORDER BY resource_id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load resources", e);
        }
        return result;
    }

    private Resource mapRow(ResultSet rs) throws SQLException {
        return new Resource(
                rs.getString("resource_id"),
                rs.getString("resource_type"),
                rs.getString("home_location_id"),
                rs.getInt("capacity"),
                AvailabilityStatus.valueOf(rs.getString("availability_status"))
        );
    }
}
