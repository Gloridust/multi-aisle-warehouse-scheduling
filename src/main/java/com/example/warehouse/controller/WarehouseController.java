package com.example.warehouse.controller;

import com.example.warehouse.dto.WarehouseCreateRequest;
import com.example.warehouse.dto.WarehouseUpdateRequest;
import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.model.Warehouse;
import com.example.warehouse.repository.StorageLocationRepository;
import com.example.warehouse.service.VisualizationService;
import com.example.warehouse.service.WarehouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;
    private final StorageLocationRepository storageLocationRepository;
    private final VisualizationService visualizationService;

    public WarehouseController(WarehouseService warehouseService,
                               StorageLocationRepository storageLocationRepository,
                               VisualizationService visualizationService) {
        this.warehouseService = warehouseService;
        this.storageLocationRepository = storageLocationRepository;
        this.visualizationService = visualizationService;
    }

    @PostMapping
    public Warehouse create(@RequestBody WarehouseCreateRequest request) {
        return warehouseService.createWarehouse(request);
    }

    @GetMapping("/{id}")
    public Warehouse get(@PathVariable Long id) {
        return warehouseService.getWarehouse(id).orElseThrow();
    }

    @PutMapping("/{id}")
    public Warehouse update(@PathVariable Long id, @RequestBody WarehouseUpdateRequest request) {
        return warehouseService.updateWarehouse(id, request);
    }

    @GetMapping
    public List<Warehouse> list() {
        return warehouseService.listWarehouses();
    }

    @GetMapping("/{id}/locations")
    public List<StorageLocation> getLocations(@PathVariable Long id,
                                              @RequestParam(required = false) Integer row,
                                              @RequestParam(required = false) Integer side) {
        if (row != null && side != null) {
            return storageLocationRepository.findByWarehouseIdAndRowAndSide(id, row, side);
        }
        if (side != null) {
            return storageLocationRepository.findByWarehouseIdAndSide(id, side);
        }
        return storageLocationRepository.findByWarehouseId(id);
    }

    @GetMapping("/{id}/visualization")
    public Object visualization(@PathVariable Long id) {
        return visualizationService.getVisualization(id);
    }

    @GetMapping("/stock")
    public Object stockSummary() {
        Warehouse warehouse = warehouseService.getFirstWarehouse().orElseThrow();
        return storageLocationRepository.fetchStockSummary(warehouse.getId());
    }
}
