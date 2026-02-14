package com.example.warehouse.repository;

import com.example.warehouse.model.AccountProfile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountProfileRepository {
    private final JdbcTemplate jdbcTemplate;

    public AccountProfileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AccountProfile> rowMapper = (rs, rowNum) -> {
        AccountProfile profile = new AccountProfile();
        profile.setId(rs.getLong("id"));
        profile.setUsername(rs.getString("username"));
        profile.setNickname(rs.getString("nickname"));
        profile.setEmail(rs.getString("email"));
        profile.setPhone(rs.getString("phone"));
        profile.setAvatarBase64(rs.getString("avatar_base64"));
        profile.setUpdatedAt(rs.getString("updated_at"));
        return profile;
    };

    public Optional<AccountProfile> findByUsername(String username) {
        List<AccountProfile> result = jdbcTemplate.query(
                "SELECT * FROM account_profile WHERE username = ?",
                rowMapper,
                username
        );
        return result.stream().findFirst();
    }

    public Long insert(AccountProfile profile) {
        jdbcTemplate.update(
                "INSERT INTO account_profile (username, nickname, email, phone, avatar_base64, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
                profile.getUsername(),
                profile.getNickname(),
                profile.getEmail(),
                profile.getPhone(),
                profile.getAvatarBase64(),
                profile.getUpdatedAt()
        );
        return jdbcTemplate.queryForObject("SELECT id FROM account_profile WHERE username = ?", Long.class, profile.getUsername());
    }

    public void update(AccountProfile profile) {
        jdbcTemplate.update(
                "UPDATE account_profile SET nickname = ?, email = ?, phone = ?, avatar_base64 = ?, updated_at = ? WHERE id = ?",
                profile.getNickname(),
                profile.getEmail(),
                profile.getPhone(),
                profile.getAvatarBase64(),
                profile.getUpdatedAt(),
                profile.getId()
        );
    }
}
