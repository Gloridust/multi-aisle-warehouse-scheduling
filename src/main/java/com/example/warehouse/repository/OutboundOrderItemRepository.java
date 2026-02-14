package com.example.warehouse.repository;

import com.example.warehouse.model.OutboundOrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OutboundOrderItemRepository {
    private final JdbcTemplate jdbcTemplate;

    public OutboundOrderItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OutboundOrderItem> rowMapper = (rs, rowNum) -> {
        OutboundOrderItem item = new OutboundOrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setSkuId(rs.getLong("sku_id"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    };

    public void insertBatch(Long orderId, List<OutboundOrderItem> items) {
        for (OutboundOrderItem item : items) {
            jdbcTemplate.update(
                    "INSERT INTO outbound_order_item (order_id, sku_id, quantity) VALUES (?, ?, ?)",
                    orderId, item.getSkuId(), item.getQuantity()
            );
        }
    }

    public List<OutboundOrderItem> findByOrderId(Long orderId) {
        return jdbcTemplate.query("SELECT * FROM outbound_order_item WHERE order_id = ? ORDER BY id", rowMapper, orderId);
    }
}
