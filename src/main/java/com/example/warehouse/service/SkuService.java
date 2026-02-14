package com.example.warehouse.service;

import com.example.warehouse.dto.SkuRequest;
import com.example.warehouse.model.Sku;
import com.example.warehouse.repository.SkuRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuService {
    private final SkuRepository skuRepository;

    public SkuService(SkuRepository skuRepository) {
        this.skuRepository = skuRepository;
    }

    public Sku createSku(SkuRequest request) {
        Sku sku = new Sku();
        sku.setName(request.getName());
        sku.setCategory(request.getCategory());
        sku.setUnitVolume(request.getUnitVolume());
        sku.setUnitWeight(request.getUnitWeight());
        Long id = skuRepository.insert(sku);
        sku.setId(id);
        return sku;
    }

    public Sku updateSku(Long id, SkuRequest request) {
        Sku sku = skuRepository.findById(id).orElseThrow();
        sku.setName(request.getName());
        sku.setCategory(request.getCategory());
        sku.setUnitVolume(request.getUnitVolume());
        sku.setUnitWeight(request.getUnitWeight());
        skuRepository.update(sku);
        return sku;
    }

    public void deleteSku(Long id) {
        skuRepository.delete(id);
    }

    public List<Sku> listSkus() {
        return skuRepository.findAll();
    }

    public Sku getSku(Long id) {
        return skuRepository.findById(id).orElseThrow();
    }
}
