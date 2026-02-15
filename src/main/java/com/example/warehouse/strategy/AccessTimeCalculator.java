package com.example.warehouse.strategy;

import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.model.Warehouse;

public class AccessTimeCalculator {
    public static double calcAccessTime(StorageLocation location, Warehouse warehouse) {
        int entryRow = warehouse.getEntryRow() == null || warehouse.getEntryRow() <= 0 ? 1 : warehouse.getEntryRow();
        int entryCol = warehouse.getEntryCol() == null || warehouse.getEntryCol() <= 0 ? 1 : warehouse.getEntryCol();
        double horizontalSpeed = warehouse.getHorizontalSpeed() == null || warehouse.getHorizontalSpeed() <= 0 ? 1 : warehouse.getHorizontalSpeed();
        double verticalSpeed = warehouse.getVerticalSpeed() == null || warehouse.getVerticalSpeed() <= 0 ? 1 : warehouse.getVerticalSpeed();
        int rowNum = location.getRowNum() == null ? 0 : location.getRowNum();
        int colNum = location.getColNum() == null ? 0 : location.getColNum();
        double hTime = Math.abs(colNum - entryCol) * horizontalSpeed;
        double vTime = Math.abs(rowNum - entryRow) * verticalSpeed;
        int entrySide = warehouse.getEntrySide() == null ? 0 : warehouse.getEntrySide();
        int sideNum = location.getSideNum() == null ? 0 : location.getSideNum();
        double sideTime = Math.abs(sideNum - entrySide) * horizontalSpeed;
        return Math.max(hTime, Math.max(vTime, sideTime));
    }
}
