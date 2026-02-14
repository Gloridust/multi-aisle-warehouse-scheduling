package com.example.warehouse.strategy;

import com.example.warehouse.model.AllocationResult;
import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.model.Warehouse;
import com.example.warehouse.service.OrderItemDetail;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CategoryStrategy implements AllocationStrategy {
    @Override
    public String getType() {
        return "CATEGORY";
    }

    @Override
    public List<AllocationResult> allocate(Warehouse warehouse, List<OrderItemDetail> items, List<StorageLocation> availableLocations) {
        Map<String, List<OrderItemDetail>> grouped = new LinkedHashMap<>();
        for (OrderItemDetail item : items) {
            String category = item.getSku().getCategory();
            grouped.computeIfAbsent(category, key -> new ArrayList<>()).add(item);
        }
        List<AllocationResult> results = new ArrayList<>();
        int ptr = 0;
        for (List<OrderItemDetail> groupItems : grouped.values()) {
            for (OrderItemDetail item : groupItems) {
                int remaining = item.getQuantity();
                int capacity = resolveCapacity(warehouse, item);
                while (remaining > 0) {
                    if (ptr >= availableLocations.size()) {
                        throw new IllegalStateException("Not enough locations");
                    }
                    StorageLocation location = availableLocations.get(ptr);
                    int allocateQty = Math.min(remaining, capacity);
                    AllocationResult result = new AllocationResult();
                    result.setLocationId(location.getId());
                    result.setSkuId(item.getSkuId());
                    result.setAllocatedQty(allocateQty);
                    result.setAllocatedVolume(allocateQty * item.getSku().getUnitVolume());
                    result.setAccessDistance(AccessTimeCalculator.calcAccessTime(location, warehouse));
                    results.add(result);
                    remaining -= allocateQty;
                    ptr++;
                }
            }
        }
        return results;
    }

    private int resolveCapacity(Warehouse warehouse, OrderItemDetail item) {
        double palletVolume = warehouse.getPalletVolume() == null ? 0 : warehouse.getPalletVolume();
        double unitVolume = item.getSku().getUnitVolume() == null ? 0 : item.getSku().getUnitVolume();
        if (palletVolume <= 0) {
            palletVolume = 1;
        }
        if (unitVolume <= 0) {
            unitVolume = 1;
        }
        return Math.max(1, (int) Math.floor(palletVolume / unitVolume));
    }
}
