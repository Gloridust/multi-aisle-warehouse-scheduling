package com.example.warehouse.service;

import com.example.warehouse.dto.VisualizationLocationView;
import com.example.warehouse.model.Sku;
import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.model.Warehouse;
import com.example.warehouse.repository.WarehouseRepository;
import com.example.warehouse.repository.SkuRepository;
import com.example.warehouse.repository.StorageLocationRepository;
import com.example.warehouse.strategy.AccessTimeCalculator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VisualizationService {
    private final StorageLocationRepository storageLocationRepository;
    private final SkuRepository skuRepository;
    private final WarehouseRepository warehouseRepository;

    public VisualizationService(StorageLocationRepository storageLocationRepository,
                                SkuRepository skuRepository,
                                WarehouseRepository warehouseRepository) {
        this.storageLocationRepository = storageLocationRepository;
        this.skuRepository = skuRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public List<VisualizationLocationView> getVisualization(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        Map<Long, Sku> skuMap = skuRepository.findAll().stream()
                .collect(Collectors.toMap(Sku::getId, sku -> sku));
        List<VisualizationLocationView> views = new ArrayList<>();
        for (StorageLocation location : storageLocationRepository.findByWarehouseId(warehouseId)) {
            VisualizationLocationView view = new VisualizationLocationView();
            view.setId(location.getId());
            view.setRowNum(location.getRowNum());
            view.setColNum(location.getColNum());
            view.setSideNum(location.getSideNum());
            view.setStatus(location.getStatus());
            view.setSkuId(location.getCurrentSkuId());
            Sku sku = location.getCurrentSkuId() == null ? null : skuMap.get(location.getCurrentSkuId());
            view.setSkuName(sku == null ? null : sku.getName());
            view.setCurrentQty(location.getCurrentQty());
            view.setUsedVolume(location.getUsedVolume());
            view.setSkuImageBase64(location.getSkuImageBase64());
            view.setAccessDistance(AccessTimeCalculator.calcAccessTime(location, warehouse));
            views.add(view);
        }
        return views;
    }
}
