package com.example.warehouse.repository;

import com.example.warehouse.model.AllocationResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllocationResultRepository {
    private final JdbcTemplate jdbcTemplate;

    public AllocationResultRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AllocationResult> rowMapper = (rs, rowNum) -> {
        AllocationResult result = new AllocationResult();
        result.setId(rs.getLong("id"));
        result.setOrderId(rs.getLong("order_id"));
        result.setStrategyType(rs.getString("strategy_type"));
        result.setLocationId(rs.getLong("location_id"));
        result.setSkuId(rs.getLong("sku_id"));
        result.setAllocatedQty(rs.getInt("allocated_qty"));
        result.setAllocatedVolume(rs.getDouble("allocated_volume"));
        result.setAccessDistance(rs.getDouble("access_distance"));
        return result;
    };

    public void insertBatch(List<AllocationResult> results) {
        for (AllocationResult result : results) {
            jdbcTemplate.update(
                    "INSERT INTO allocation_result (order_id, strategy_type, location_id, sku_id, allocated_qty, allocated_volume, access_distance) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    result.getOrderId(),
                    result.getStrategyType(),
                    result.getLocationId(),
                    result.getSkuId(),
                    result.getAllocatedQty(),
                    result.getAllocatedVolume(),
                    result.getAccessDistance()
            );
        }
    }

    public List<AllocationResult> findByOrderId(Long orderId) {
        return jdbcTemplate.query("SELECT * FROM allocation_result WHERE order_id = ? ORDER BY id", rowMapper, orderId);
    }

    public void deleteByOrderId(Long orderId) {
        jdbcTemplate.update("DELETE FROM allocation_result WHERE order_id = ?", orderId);
    }
}
