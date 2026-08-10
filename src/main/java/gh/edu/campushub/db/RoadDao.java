package gh.edu.campushub.db;

import gh.edu.campushub.model.Road;
import gh.edu.campushub.structures.DynamicArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoadDao {

    private final Connection connection;

    public RoadDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(Road road) {
        String sql = "INSERT INTO roads (road_id, from_location_id, to_location_id, distance_km, travel_time_min, condition_weight) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(road_id) DO UPDATE SET from_location_id=excluded.from_location_id, " +
                "to_location_id=excluded.to_location_id, distance_km=excluded.distance_km, " +
                "travel_time_min=excluded.travel_time_min, condition_weight=excluded.condition_weight";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, road.getRoadId());
            ps.setString(2, road.getFromLocationId());
            ps.setString(3, road.getToLocationId());
            ps.setDouble(4, road.getDistanceKm());
            ps.setDouble(5, road.getTravelTimeMin());
            ps.setDouble(6, road.getConditionWeight());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert road " + road.getRoadId(), e);
        }
    }

    public DynamicArray<Road> findAll() {
        DynamicArray<Road> result = new DynamicArray<>();
        String sql = "SELECT road_id, from_location_id, to_location_id, distance_km, travel_time_min, condition_weight " +
                "FROM roads ORDER BY road_id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load roads", e);
        }
        return result;
    }

    private Road mapRow(ResultSet rs) throws SQLException {
        return new Road(
                rs.getString("road_id"),
                rs.getString("from_location_id"),
                rs.getString("to_location_id"),
                rs.getDouble("distance_km"),
                rs.getDouble("travel_time_min"),
                rs.getDouble("condition_weight")
        );
    }
}
