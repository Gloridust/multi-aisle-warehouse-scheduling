package com.example.warehouse.repository;

import com.example.warehouse.model.InboundOrder;
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
public class InboundOrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public InboundOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<InboundOrder> rowMapper = (rs, rowNum) -> {
        InboundOrder order = new InboundOrder();
        order.setId(rs.getLong("id"));
        order.setOrderNo(rs.getString("order_no"));
        order.setStatus(rs.getInt("status"));
        order.setStrategyType(rs.getString("strategy_type"));
        order.setCreateTime(rs.getString("create_time"));
        return order;
    };

    public Long insert(InboundOrder order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO inbound_order (order_no, status, strategy_type, create_time) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, order.getOrderNo());
            ps.setInt(2, order.getStatus());
            ps.setString(3, order.getStrategyType());
            ps.setString(4, order.getCreateTime());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void updateStatusAndStrategy(Long id, int status, String strategyType) {
        jdbcTemplate.update("UPDATE inbound_order SET status = ?, strategy_type = ? WHERE id = ?", status, strategyType, id);
    }

    public List<InboundOrder> findAll() {
        return jdbcTemplate.query("SELECT * FROM inbound_order ORDER BY id DESC", rowMapper);
    }

    public Optional<InboundOrder> findById(Long id) {
        List<InboundOrder> result = jdbcTemplate.query("SELECT * FROM inbound_order WHERE id = ?", rowMapper, id);
        return result.stream().findFirst();
    }
}
