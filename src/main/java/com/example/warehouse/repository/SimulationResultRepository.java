package com.example.warehouse.repository;

import com.example.warehouse.model.SimulationResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SimulationResultRepository {
    private final JdbcTemplate jdbcTemplate;

    public SimulationResultRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SimulationResult> rowMapper = (rs, rowNum) -> {
        SimulationResult result = new SimulationResult();
        result.setId(rs.getLong("id"));
        result.setOrderId(rs.getLong("order_id"));
        result.setStrategyType(rs.getString("strategy_type"));
        result.setTotalDistance(rs.getDouble("total_distance"));
        result.setAvgDistance(rs.getDouble("avg_distance"));
        result.setSpaceUtilization(rs.getDouble("space_utilization"));
        result.setUsedLocations(rs.getInt("used_locations"));
        result.setComputeTime(rs.getLong("compute_time"));
        return result;
    };

    public void insert(SimulationResult result) {
        jdbcTemplate.update(
                "INSERT INTO simulation_result (order_id, strategy_type, total_distance, avg_distance, space_utilization, used_locations, compute_time) VALUES (?, ?, ?, ?, ?, ?, ?)",
                result.getOrderId(),
                result.getStrategyType(),
                result.getTotalDistance(),
                result.getAvgDistance(),
                result.getSpaceUtilization(),
                result.getUsedLocations(),
                result.getComputeTime()
        );
    }

    public List<SimulationResult> findByOrderId(Long orderId) {
        return jdbcTemplate.query("SELECT * FROM simulation_result WHERE order_id = ? ORDER BY id", rowMapper, orderId);
    }

    public void deleteByOrderId(Long orderId) {
        jdbcTemplate.update("DELETE FROM simulation_result WHERE order_id = ?", orderId);
    }
}
