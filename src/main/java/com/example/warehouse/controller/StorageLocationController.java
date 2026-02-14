package com.example.warehouse.controller;

import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.repository.StorageLocationRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
public class StorageLocationController {
    private final StorageLocationRepository storageLocationRepository;

    public StorageLocationController(StorageLocationRepository storageLocationRepository) {
        this.storageLocationRepository = storageLocationRepository;
    }

    @PutMapping("/{id}/status")
    public StorageLocation updateStatus(@PathVariable Long id, @RequestParam int status) {
        storageLocationRepository.updateLocationStatus(id, status);
        return storageLocationRepository.findById(id).orElseThrow();
    }
}
