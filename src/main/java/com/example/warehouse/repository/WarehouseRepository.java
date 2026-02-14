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
        int totalSides = rs.getInt("total_sides");
        warehouse.setTotalSides(totalSides <= 0 ? 2 : totalSides);
        warehouse.setPalletVolume(rs.getDouble("pallet_volume"));
        warehouse.setHorizontalSpeed(rs.getDouble("horizontal_speed"));
        warehouse.setVerticalSpeed(rs.getDouble("vertical_speed"));
        warehouse.setEntryRow(rs.getInt("entry_row"));
        warehouse.setEntryCol(rs.getInt("entry_col"));
        warehouse.setEntrySide(rs.getInt("entry_side"));
        return warehouse;
    };

    public Long insert(Warehouse warehouse) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO warehouse (name, total_rows, total_cols, total_sides, pallet_volume, horizontal_speed, vertical_speed, entry_row, entry_col, entry_side) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, warehouse.getName());
            ps.setInt(2, warehouse.getTotalRows());
            ps.setInt(3, warehouse.getTotalCols());
            ps.setInt(4, warehouse.getTotalSides());
            ps.setDouble(5, warehouse.getPalletVolume());
            ps.setDouble(6, warehouse.getHorizontalSpeed());
            ps.setDouble(7, warehouse.getVerticalSpeed());
            ps.setInt(8, warehouse.getEntryRow());
            ps.setInt(9, warehouse.getEntryCol());
            ps.setInt(10, warehouse.getEntrySide());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void update(Warehouse warehouse) {
        jdbcTemplate.update(
                "UPDATE warehouse SET name=?, total_rows=?, total_cols=?, total_sides=?, pallet_volume=?, horizontal_speed=?, vertical_speed=?, entry_row=?, entry_col=?, entry_side=? WHERE id=?",
                warehouse.getName(),
                warehouse.getTotalRows(),
                warehouse.getTotalCols(),
                warehouse.getTotalSides(),
                warehouse.getPalletVolume(),
                warehouse.getHorizontalSpeed(),
                warehouse.getVerticalSpeed(),
                warehouse.getEntryRow(),
                warehouse.getEntryCol(),
                warehouse.getEntrySide(),
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
