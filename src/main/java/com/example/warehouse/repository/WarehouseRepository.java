package com.example.warehouse.repository;

import com.example.warehouse.model.Warehouse;
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
public class WarehouseRepository {
    private final JdbcTemplate jdbcTemplate;

    public WarehouseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Warehouse> rowMapper = (rs, rowNum) -> {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(rs.getLong("id"));
        warehouse.setName(rs.getString("name"));
        warehouse.setTotalRows(rs.getInt("total_rows"));
        warehouse.setTotalCols(rs.getInt("total_cols"));
        warehouse.setPalletVolume(rs.getDouble("pallet_volume"));
        warehouse.setHorizontalSpeed(rs.getDouble("horizontal_speed"));
        warehouse.setVerticalSpeed(rs.getDouble("vertical_speed"));
        warehouse.setEntryRow(rs.getInt("entry_row"));
        warehouse.setEntryCol(rs.getInt("entry_col"));
        return warehouse;
    };

    public Long insert(Warehouse warehouse) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO warehouse (name, total_rows, total_cols, pallet_volume, horizontal_speed, vertical_speed, entry_row, entry_col) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, warehouse.getName());
            ps.setInt(2, warehouse.getTotalRows());
            ps.setInt(3, warehouse.getTotalCols());
            ps.setDouble(4, warehouse.getPalletVolume());
            ps.setDouble(5, warehouse.getHorizontalSpeed());
            ps.setDouble(6, warehouse.getVerticalSpeed());
            ps.setInt(7, warehouse.getEntryRow());
            ps.setInt(8, warehouse.getEntryCol());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void update(Warehouse warehouse) {
        jdbcTemplate.update(
                "UPDATE warehouse SET name=?, total_rows=?, total_cols=?, pallet_volume=?, horizontal_speed=?, vertical_speed=?, entry_row=?, entry_col=? WHERE id=?",
                warehouse.getName(),
                warehouse.getTotalRows(),
                warehouse.getTotalCols(),
                warehouse.getPalletVolume(),
                warehouse.getHorizontalSpeed(),
                warehouse.getVerticalSpeed(),
                warehouse.getEntryRow(),
                warehouse.getEntryCol(),
                warehouse.getId()
        );
    }

    public Optional<Warehouse> findById(Long id) {
        List<Warehouse> result = jdbcTemplate.query("SELECT * FROM warehouse WHERE id = ?", rowMapper, id);
        return result.stream().findFirst();
    }

    public Optional<Warehouse> findFirst() {
        List<Warehouse> result = jdbcTemplate.query("SELECT * FROM warehouse ORDER BY id LIMIT 1", rowMapper);
        return result.stream().findFirst();
    }

    public List<Warehouse> findAll() {
        return jdbcTemplate.query("SELECT * FROM warehouse ORDER BY id DESC", rowMapper);
    }
}
