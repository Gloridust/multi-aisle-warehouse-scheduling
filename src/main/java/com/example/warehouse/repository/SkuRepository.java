package com.example.warehouse.repository;

import com.example.warehouse.model.Sku;
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
public class SkuRepository {
    private final JdbcTemplate jdbcTemplate;

    public SkuRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Sku> rowMapper = (rs, rowNum) -> {
        Sku sku = new Sku();
        sku.setId(rs.getLong("id"));
        sku.setName(rs.getString("name"));
        sku.setCategory(rs.getString("category"));
        sku.setUnitVolume(rs.getDouble("unit_volume"));
        sku.setUnitWeight(rs.getDouble("unit_weight"));
        return sku;
    };

    public Long insert(Sku sku) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO sku (name, category, unit_volume, unit_weight) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, sku.getName());
            ps.setString(2, sku.getCategory());
            ps.setDouble(3, sku.getUnitVolume());
            ps.setDouble(4, sku.getUnitWeight());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void update(Sku sku) {
        jdbcTemplate.update(
                "UPDATE sku SET name = ?, category = ?, unit_volume = ?, unit_weight = ? WHERE id = ?",
                sku.getName(), sku.getCategory(), sku.getUnitVolume(), sku.getUnitWeight(), sku.getId()
        );
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM sku WHERE id = ?", id);
    }

    public List<Sku> findAll() {
        return jdbcTemplate.query("SELECT * FROM sku ORDER BY id", rowMapper);
    }

    public Optional<Sku> findById(Long id) {
        List<Sku> result = jdbcTemplate.query("SELECT * FROM sku WHERE id = ?", rowMapper, id);
        return result.stream().findFirst();
    }
}
