package com.example.warehouse.service;

import com.example.warehouse.dto.WarehouseCreateRequest;
import com.example.warehouse.dto.WarehouseUpdateRequest;
import com.example.warehouse.model.Warehouse;
import com.example.warehouse.repository.StorageLocationRepository;
import com.example.warehouse.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final StorageLocationRepository storageLocationRepository;

    public WarehouseService(WarehouseRepository warehouseRepository, StorageLocationRepository storageLocationRepository) {
        this.warehouseRepository = warehouseRepository;
        this.storageLocationRepository = storageLocationRepository;
    }

    public Warehouse createWarehouse(WarehouseCreateRequest request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setTotalRows(request.getTotalRows());
        warehouse.setTotalCols(request.getTotalCols());
        warehouse.setPalletVolume(request.getPalletVolume());
        warehouse.setHorizontalSpeed(request.getHorizontalSpeed());
        warehouse.setVerticalSpeed(request.getVerticalSpeed());
        warehouse.setEntryRow(request.getEntryRow() == null ? 1 : request.getEntryRow());
        warehouse.setEntryCol(request.getEntryCol() == null ? 1 : request.getEntryCol());
        Long id = warehouseRepository.insert(warehouse);
        storageLocationRepository.insertBatch(id, warehouse.getTotalRows(), warehouse.getTotalCols());
        warehouse.setId(id);
        return warehouse;
    }

    public Warehouse updateWarehouse(Long id, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow();
        warehouse.setName(request.getName());
        warehouse.setTotalRows(request.getTotalRows());
        warehouse.setTotalCols(request.getTotalCols());
        warehouse.setPalletVolume(request.getPalletVolume());
        warehouse.setHorizontalSpeed(request.getHorizontalSpeed());
        warehouse.setVerticalSpeed(request.getVerticalSpeed());
        warehouse.setEntryRow(request.getEntryRow() == null ? 1 : request.getEntryRow());
        warehouse.setEntryCol(request.getEntryCol() == null ? 1 : request.getEntryCol());
        warehouseRepository.update(warehouse);
        return warehouse;
    }

    public Optional<Warehouse> getWarehouse(Long id) {
        return warehouseRepository.findById(id);
    }

    public Optional<Warehouse> getFirstWarehouse() {
        return warehouseRepository.findFirst();
    }

    public List<Warehouse> listWarehouses() {
        return warehouseRepository.findAll();
    }
}
