package gh.edu.campushub.db;

import gh.edu.campushub.model.AlgorithmRun;
import gh.edu.campushub.structures.DynamicArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;

public class AlgorithmRunDao {

    private final Connection connection;

    public AlgorithmRunDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(AlgorithmRun run) {
        String sql = "INSERT INTO algorithm_runs (run_id, algorithm_name, input_size, execution_time_ms, memory_kb, run_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, run.getRunId());
            ps.setString(2, run.getAlgorithmName());
            ps.setInt(3, run.getInputSize());
            ps.setDouble(4, run.getExecutionTimeMs());
            if (run.getMemoryKb() != null) {
                ps.setDouble(5, run.getMemoryKb());
            } else {
                ps.setNull(5, Types.DOUBLE);
            }
            ps.setString(6, run.getRunAt().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert algorithm run " + run.getRunId(), e);
        }
    }

    public DynamicArray<AlgorithmRun> findAll() {
        DynamicArray<AlgorithmRun> result = new DynamicArray<>();
        String sql = "SELECT run_id, algorithm_name, input_size, execution_time_ms, memory_kb, run_at " +
                "FROM algorithm_runs ORDER BY run_at";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Double memoryKb = rs.getObject("memory_kb") != null ? rs.getDouble("memory_kb") : null;
                result.add(new AlgorithmRun(
                        rs.getString("run_id"),
                        rs.getString("algorithm_name"),
                        rs.getInt("input_size"),
                        rs.getDouble("execution_time_ms"),
                        memoryKb,
                        LocalDateTime.parse(rs.getString("run_at"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load algorithm runs", e);
        }
        return result;
    }
}
