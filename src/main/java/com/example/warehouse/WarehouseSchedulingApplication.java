package com.example.warehouse;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class WarehouseSchedulingApplication {
    public static void main(String[] args) {
        SpringApplication.run(WarehouseSchedulingApplication.class, args);
    }

    @Bean
    public CommandLineRunner migrateWarehouseSchema(JdbcTemplate jdbcTemplate) {
        return args -> {
            addColumnIfMissing(jdbcTemplate, "warehouse", "total_sides", "INTEGER DEFAULT 2");
            addColumnIfMissing(jdbcTemplate, "warehouse", "entry_side", "INTEGER DEFAULT 0");
            addColumnIfMissing(jdbcTemplate, "storage_location", "side_num", "INTEGER DEFAULT 0");
            backfillStorageSides(jdbcTemplate);
        };
    }

    private void addColumnIfMissing(JdbcTemplate jdbcTemplate, String table, String column, String definition) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("PRAGMA table_info(" + table + ")");
        boolean exists = rows.stream().anyMatch(row -> column.equals(row.get("name")));
        if (!exists) {
            jdbcTemplate.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        }
    }

    private void backfillStorageSides(JdbcTemplate jdbcTemplate) {
        List<Map<String, Object>> warehouses = jdbcTemplate.queryForList(
                "SELECT id, total_rows, total_cols, total_sides FROM warehouse"
        );
        for (Map<String, Object> warehouse : warehouses) {
            long warehouseId = ((Number) warehouse.get("id")).longValue();
            int rows = ((Number) warehouse.get("total_rows")).intValue();
            int cols = ((Number) warehouse.get("total_cols")).intValue();
            int sides = ((Number) warehouse.get("total_sides")).intValue();
            if (sides <= 0) {
                sides = 2;
            }
            for (int side = 0; side < sides; side++) {
                List<Map<String, Object>> existing = jdbcTemplate.queryForList(
                        "SELECT row_num, col_num FROM storage_location WHERE warehouse_id = ? AND side_num = ?",
                        warehouseId,
                        side
                );
                Set<String> keys = new HashSet<>();
                for (Map<String, Object> row : existing) {
                    int rowNum = ((Number) row.get("row_num")).intValue();
                    int colNum = ((Number) row.get("col_num")).intValue();
                    keys.add(rowNum + "-" + colNum);
                }
                for (int r = 1; r <= rows; r++) {
                    for (int c = 1; c <= cols; c++) {
                        if (!keys.contains(r + "-" + c)) {
                            jdbcTemplate.update(
                                    "INSERT INTO storage_location (warehouse_id, row_num, col_num, side_num, status, current_sku_id, current_qty, used_volume) VALUES (?, ?, ?, ?, 0, NULL, NULL, NULL)",
                                    warehouseId,
                                    r,
                                    c,
                                    side
                            );
                        }
                    }
                }
            }
        }
    }
}
