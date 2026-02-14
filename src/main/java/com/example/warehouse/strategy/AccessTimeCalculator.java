package com.example.warehouse.strategy;

import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.model.Warehouse;

public class AccessTimeCalculator {
    public static double calcAccessTime(StorageLocation location, Warehouse warehouse) {
        double hTime = Math.abs(location.getColNum() - warehouse.getEntryCol()) * warehouse.getHorizontalSpeed();
        double vTime = Math.abs(location.getRowNum() - warehouse.getEntryRow()) * warehouse.getVerticalSpeed();
        int entrySide = warehouse.getEntrySide() == null ? 0 : warehouse.getEntrySide();
        int sideNum = location.getSideNum() == null ? 0 : location.getSideNum();
        double sideTime = Math.abs(sideNum - entrySide) * warehouse.getHorizontalSpeed();
        return Math.max(hTime, Math.max(vTime, sideTime));
    }
}
