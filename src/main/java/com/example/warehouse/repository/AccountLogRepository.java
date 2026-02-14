package com.example.warehouse.repository;

import com.example.warehouse.model.AccountLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountLogRepository {
    private final JdbcTemplate jdbcTemplate;

    public AccountLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AccountLog> rowMapper = (rs, rowNum) -> {
        AccountLog log = new AccountLog();
        log.setId(rs.getLong("id"));
        log.setUsername(rs.getString("username"));
        log.setAction(rs.getString("action"));
        log.setDetail(rs.getString("detail"));
        log.setCreatedAt(rs.getString("created_at"));
        return log;
    };

    public void insert(AccountLog log) {
        jdbcTemplate.update(
                "INSERT INTO account_log (username, action, detail, created_at) VALUES (?, ?, ?, ?)",
                log.getUsername(),
                log.getAction(),
                log.getDetail(),
                log.getCreatedAt()
        );
    }

    public List<AccountLog> findByUsername(String username, int limit) {
        return jdbcTemplate.query(
                "SELECT * FROM account_log WHERE username = ? ORDER BY id DESC LIMIT ?",
                rowMapper,
                username,
                limit
        );
    }
}
