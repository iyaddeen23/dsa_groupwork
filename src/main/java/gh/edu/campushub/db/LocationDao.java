package gh.edu.campushub.db;

import gh.edu.campushub.model.Location;
import gh.edu.campushub.structures.DynamicArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationDao {

    private final Connection connection;

    public LocationDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(Location location) {
        String sql = "INSERT INTO locations (location_id, name, area, location_type, x_coord, y_coord) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(location_id) DO UPDATE SET name=excluded.name, area=excluded.area, " +
                "location_type=excluded.location_type, x_coord=excluded.x_coord, y_coord=excluded.y_coord";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, location.getLocationId());
            ps.setString(2, location.getName());
            ps.setString(3, location.getArea());
            ps.setString(4, location.getLocationType());
            ps.setDouble(5, location.getXCoord());
            ps.setDouble(6, location.getYCoord());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert location " + location.getLocationId(), e);
        }
    }

    public DynamicArray<Location> findAll() {
        DynamicArray<Location> result = new DynamicArray<>();
        String sql = "SELECT location_id, name, area, location_type, x_coord, y_coord FROM locations ORDER BY location_id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load locations", e);
        }
        return result;
    }

    public Location findById(String locationId) {
        String sql = "SELECT location_id, name, area, location_type, x_coord, y_coord FROM locations WHERE location_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, locationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load location " + locationId, e);
        }
    }

    private Location mapRow(ResultSet rs) throws SQLException {
        return new Location(
                rs.getString("location_id"),
                rs.getString("name"),
                rs.getString("area"),
                rs.getString("location_type"),
                rs.getDouble("x_coord"),
                rs.getDouble("y_coord")
        );
    }
}
