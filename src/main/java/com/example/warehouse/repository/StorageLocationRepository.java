package com.example.warehouse.repository;

import com.example.warehouse.model.StorageLocation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StorageLocationRepository {
    private final JdbcTemplate jdbcTemplate;

    public StorageLocationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<StorageLocation> rowMapper = (rs, rowNum) -> {
        StorageLocation location = new StorageLocation();
        location.setId(rs.getLong("id"));
        location.setWarehouseId(rs.getLong("warehouse_id"));
        location.setRowNum(rs.getInt("row_num"));
        location.setColNum(rs.getInt("col_num"));
        location.setStatus(rs.getInt("status"));
        location.setCurrentSkuId(rs.getObject("current_sku_id") == null ? null : rs.getLong("current_sku_id"));
        location.setCurrentQty(rs.getObject("current_qty") == null ? null : rs.getInt("current_qty"));
        location.setUsedVolume(rs.getObject("used_volume") == null ? null : rs.getDouble("used_volume"));
        return location;
    };

    public void insertBatch(Long warehouseId, int rows, int cols) {
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                jdbcTemplate.update(
                        "INSERT INTO storage_location (warehouse_id, row_num, col_num, status, current_sku_id, current_qty, used_volume) VALUES (?, ?, ?, 0, NULL, NULL, NULL)",
                        warehouseId, r, c
                );
            }
        }
    }

    public List<StorageLocation> findByWarehouseId(Long warehouseId) {
        return jdbcTemplate.query("SELECT * FROM storage_location WHERE warehouse_id = ? ORDER BY id", rowMapper, warehouseId);
    }

    public List<StorageLocation> findByWarehouseIdAndRow(Long warehouseId, int row) {
        return jdbcTemplate.query("SELECT * FROM storage_location WHERE warehouse_id = ? AND row_num = ? ORDER BY col_num", rowMapper, warehouseId, row);
    }

    public List<StorageLocation> findFreeLocations(Long warehouseId) {
        return jdbcTemplate.query("SELECT * FROM storage_location WHERE warehouse_id = ? AND status = 0 ORDER BY id", rowMapper, warehouseId);
    }

    public List<StorageLocation> findOccupiedBySku(Long warehouseId, Long skuId) {
        return jdbcTemplate.query(
                "SELECT * FROM storage_location WHERE warehouse_id = ? AND status = 1 AND current_sku_id = ? ORDER BY id",
                rowMapper,
                warehouseId,
                skuId
        );
    }

    public Optional<StorageLocation> findById(Long id) {
        List<StorageLocation> result = jdbcTemplate.query("SELECT * FROM storage_location WHERE id = ?", rowMapper, id);
        return result.stream().findFirst();
    }

    public void updateLocationOccupied(Long id, Long skuId, int qty, double usedVolume) {
        jdbcTemplate.update(
                "UPDATE storage_location SET status = 1, current_sku_id = ?, current_qty = ?, used_volume = ? WHERE id = ?",
                skuId, qty, usedVolume, id
        );
    }

    public void updateLocationAfterOutbound(Long id, Integer qty, Double usedVolume, Integer status, Long skuId) {
        jdbcTemplate.update(
                "UPDATE storage_location SET status = ?, current_sku_id = ?, current_qty = ?, used_volume = ? WHERE id = ?",
                status, skuId, qty, usedVolume, id
        );
    }

    public void updateLocationStatus(Long id, int status) {
        jdbcTemplate.update("UPDATE storage_location SET status = ? WHERE id = ?", status, id);
    }
}
