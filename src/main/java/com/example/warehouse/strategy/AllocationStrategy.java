package com.example.warehouse.strategy;

import com.example.warehouse.model.AllocationResult;
import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.model.Warehouse;
import com.example.warehouse.service.OrderItemDetail;

import java.util.List;

public interface AllocationStrategy {
    String getType();

    List<AllocationResult> allocate(Warehouse warehouse, List<OrderItemDetail> items, List<StorageLocation> availableLocations);
}
