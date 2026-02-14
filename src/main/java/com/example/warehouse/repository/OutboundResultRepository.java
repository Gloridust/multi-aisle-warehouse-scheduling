package com.example.warehouse.repository;

import com.example.warehouse.model.OutboundResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OutboundResultRepository {
    private final JdbcTemplate jdbcTemplate;

    public OutboundResultRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OutboundResult> rowMapper = (rs, rowNum) -> {
        OutboundResult result = new OutboundResult();
        result.setId(rs.getLong("id"));
        result.setOrderId(rs.getLong("order_id"));
        result.setLocationId(rs.getLong("location_id"));
        result.setSkuId(rs.getLong("sku_id"));
        result.setPickedQty(rs.getInt("picked_qty"));
        result.setPickedVolume(rs.getDouble("picked_volume"));
        result.setAccessDistance(rs.getDouble("access_distance"));
        return result;
    };

    public void insertBatch(List<OutboundResult> results) {
        for (OutboundResult result : results) {
            jdbcTemplate.update(
                    "INSERT INTO outbound_result (order_id, location_id, sku_id, picked_qty, picked_volume, access_distance) VALUES (?, ?, ?, ?, ?, ?)",
                    result.getOrderId(),
                    result.getLocationId(),
                    result.getSkuId(),
                    result.getPickedQty(),
                    result.getPickedVolume(),
                    result.getAccessDistance()
            );
        }
    }

    public List<OutboundResult> findByOrderId(Long orderId) {
        return jdbcTemplate.query("SELECT * FROM outbound_result WHERE order_id = ? ORDER BY id", rowMapper, orderId);
    }

    public void deleteByOrderId(Long orderId) {
        jdbcTemplate.update("DELETE FROM outbound_result WHERE order_id = ?", orderId);
    }
}
