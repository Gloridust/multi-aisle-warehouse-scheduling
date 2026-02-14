package com.example.warehouse.repository;

import com.example.warehouse.model.OutboundOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class OutboundOrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public OutboundOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OutboundOrder> rowMapper = (rs, rowNum) -> {
        OutboundOrder order = new OutboundOrder();
        order.setId(rs.getLong("id"));
        order.setOrderNo(rs.getString("order_no"));
        order.setStatus(rs.getInt("status"));
        order.setCreateTime(rs.getString("create_time"));
        return order;
    };

    public Long insert(OutboundOrder order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO outbound_order (order_no, status, create_time) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, order.getOrderNo());
            ps.setInt(2, order.getStatus());
            ps.setString(3, order.getCreateTime());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void updateStatus(Long id, int status) {
        jdbcTemplate.update("UPDATE outbound_order SET status = ? WHERE id = ?", status, id);
    }

    public List<OutboundOrder> findAll() {
        return jdbcTemplate.query("SELECT * FROM outbound_order ORDER BY id DESC", rowMapper);
    }

    public Optional<OutboundOrder> findById(Long id) {
        List<OutboundOrder> result = jdbcTemplate.query("SELECT * FROM outbound_order WHERE id = ?", rowMapper, id);
        return result.stream().findFirst();
    }
}
