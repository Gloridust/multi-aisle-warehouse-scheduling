package com.example.warehouse.service;

import com.example.warehouse.dto.VisualizationLocationView;
import com.example.warehouse.model.Sku;
import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.repository.SkuRepository;
import com.example.warehouse.repository.StorageLocationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VisualizationService {
    private final StorageLocationRepository storageLocationRepository;
    private final SkuRepository skuRepository;

    public VisualizationService(StorageLocationRepository storageLocationRepository, SkuRepository skuRepository) {
        this.storageLocationRepository = storageLocationRepository;
        this.skuRepository = skuRepository;
    }

    public List<VisualizationLocationView> getVisualization(Long warehouseId) {
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
            views.add(view);
        }
        return views;
    }
}
