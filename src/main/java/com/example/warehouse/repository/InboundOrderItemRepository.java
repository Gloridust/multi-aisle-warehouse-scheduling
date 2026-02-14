package com.example.warehouse.repository;

import com.example.warehouse.model.InboundOrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InboundOrderItemRepository {
    private final JdbcTemplate jdbcTemplate;

    public InboundOrderItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<InboundOrderItem> rowMapper = (rs, rowNum) -> {
        InboundOrderItem item = new InboundOrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setSkuId(rs.getLong("sku_id"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    };

    public void insertBatch(Long orderId, List<InboundOrderItem> items) {
        for (InboundOrderItem item : items) {
            jdbcTemplate.update(
                    "INSERT INTO inbound_order_item (order_id, sku_id, quantity) VALUES (?, ?, ?)",
                    orderId, item.getSkuId(), item.getQuantity()
            );
        }
    }

    public List<InboundOrderItem> findByOrderId(Long orderId) {
        return jdbcTemplate.query("SELECT * FROM inbound_order_item WHERE order_id = ? ORDER BY id", rowMapper, orderId);
    }
}
