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
        location.setSideNum(rs.getInt("side_num"));
        location.setStatus(rs.getInt("status"));
        location.setCurrentSkuId(rs.getObject("current_sku_id") == null ? null : rs.getLong("current_sku_id"));
        location.setCurrentQty(rs.getObject("current_qty") == null ? null : rs.getInt("current_qty"));
        location.setUsedVolume(rs.getObject("used_volume") == null ? null : rs.getDouble("used_volume"));
        location.setSkuImageBase64(rs.getString("sku_image_base64"));
        return location;
    };

    public void insertBatch(Long warehouseId, int rows, int cols, int sides) {
        for (int s = 0; s < sides; s++) {
            for (int r = 1; r <= rows; r++) {
                for (int c = 1; c <= cols; c++) {
                    jdbcTemplate.update(
                            "INSERT INTO storage_location (warehouse_id, row_num, col_num, side_num, status, current_sku_id, current_qty, used_volume) VALUES (?, ?, ?, ?, 0, NULL, NULL, NULL)",
                            warehouseId, r, c, s
                    );
                }
            }
        }
    }

    public List<StorageLocation> findByWarehouseId(Long warehouseId) {
        return jdbcTemplate.query(
                "SELECT * FROM storage_location WHERE warehouse_id = ? ORDER BY side_num, row_num, col_num",
                rowMapper,
                warehouseId
        );
    }

    public List<StorageLocation> findByWarehouseIdAndRowAndSide(Long warehouseId, int row, int side) {
        return jdbcTemplate.query(
                "SELECT * FROM storage_location WHERE warehouse_id = ? AND row_num = ? AND side_num = ? ORDER BY col_num",
                rowMapper,
                warehouseId,
                row,
                side
        );
    }

    public List<StorageLocation> findByWarehouseIdAndSide(Long warehouseId, int side) {
        return jdbcTemplate.query(
                "SELECT * FROM storage_location WHERE warehouse_id = ? AND side_num = ? ORDER BY row_num, col_num",
                rowMapper,
                warehouseId,
                side
        );
    }

    public List<StorageLocation> findFreeLocations(Long warehouseId) {
        return jdbcTemplate.query(
                "SELECT * FROM storage_location WHERE warehouse_id = ? AND status = 0 ORDER BY row_num, col_num, side_num",
                rowMapper,
                warehouseId
        );
    }

    public List<StorageLocation> findOccupiedBySku(Long warehouseId, Long skuId) {
        return jdbcTemplate.query(
                "SELECT * FROM storage_location WHERE warehouse_id = ? AND current_sku_id = ? AND current_qty > 0 ORDER BY id",
                rowMapper,
                warehouseId,
                skuId
        );
    }

    public Optional<StorageLocation> findById(Long id) {
        List<StorageLocation> result = jdbcTemplate.query("SELECT * FROM storage_location WHERE id = ?", rowMapper, id);
        return result.stream().findFirst();
    }

    public List<StockSummaryRow> fetchStockSummary(Long warehouseId) {
        return jdbcTemplate.query(
                "SELECT current_sku_id, SUM(current_qty) AS total_qty FROM storage_location WHERE warehouse_id = ? AND current_sku_id IS NOT NULL AND current_qty > 0 GROUP BY current_sku_id",
                (rs, rowNum) -> new StockSummaryRow(rs.getLong("current_sku_id"), rs.getInt("total_qty")),
                warehouseId
        );
    }

    public void updateLocationOccupied(Long id, Long skuId, int qty, double usedVolume, String imageBase64) {
        jdbcTemplate.update(
                "UPDATE storage_location SET status = 1, current_sku_id = ?, current_qty = ?, used_volume = ?, sku_image_base64 = ? WHERE id = ?",
                skuId, qty, usedVolume, imageBase64, id
        );
    }

    public void updateLocationAfterOutbound(Long id, Integer qty, Double usedVolume, Integer status, Long skuId, String imageBase64) {
        jdbcTemplate.update(
                "UPDATE storage_location SET status = ?, current_sku_id = ?, current_qty = ?, used_volume = ?, sku_image_base64 = ? WHERE id = ?",
                status, skuId, qty, usedVolume, imageBase64, id
        );
    }

    public void updateLocationStatus(Long id, int status) {
        jdbcTemplate.update("UPDATE storage_location SET status = ? WHERE id = ?", status, id);
    }

    public static class StockSummaryRow {
        private final Long skuId;
        private final Integer totalQty;

        public StockSummaryRow(Long skuId, Integer totalQty) {
            this.skuId = skuId;
            this.totalQty = totalQty;
        }

        public Long getSkuId() {
            return skuId;
        }

        public Integer getTotalQty() {
            return totalQty;
        }
    }
}
