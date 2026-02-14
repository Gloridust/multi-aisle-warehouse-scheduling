package com.example.warehouse.repository;

import com.example.warehouse.model.AdminUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdminUserRepository {
    private final JdbcTemplate jdbcTemplate;

    public AdminUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AdminUser> rowMapper = (rs, rowNum) -> {
        AdminUser user = new AdminUser();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setUpdatedAt(rs.getString("updated_at"));
        return user;
    };

    public Optional<AdminUser> findByUsername(String username) {
        List<AdminUser> result = jdbcTemplate.query("SELECT * FROM admin_user WHERE username = ?", rowMapper, username);
        return result.stream().findFirst();
    }

    public void updatePassword(Long id, String passwordHash, String updatedAt) {
        jdbcTemplate.update(
                "UPDATE admin_user SET password_hash = ?, updated_at = ? WHERE id = ?",
                passwordHash, updatedAt, id
        );
    }
}
