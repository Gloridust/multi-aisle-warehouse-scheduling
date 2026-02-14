package com.example.warehouse.strategy;

import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.model.Warehouse;

public class AccessTimeCalculator {
    public static double calcAccessTime(StorageLocation location, Warehouse warehouse) {
        double hTime = Math.abs(location.getColNum() - warehouse.getEntryCol()) * warehouse.getHorizontalSpeed();
        double vTime = Math.abs(location.getRowNum() - warehouse.getEntryRow()) * warehouse.getVerticalSpeed();
        return Math.max(hTime, vTime);
    }
}
