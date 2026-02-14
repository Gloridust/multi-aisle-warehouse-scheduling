package com.example.warehouse.controller;

import com.example.warehouse.dto.SkuRequest;
import com.example.warehouse.model.Sku;
import com.example.warehouse.service.SkuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sku")
public class SkuController {
    private final SkuService skuService;

    public SkuController(SkuService skuService) {
        this.skuService = skuService;
    }

    @PostMapping
    public Sku create(@RequestBody SkuRequest request) {
        return skuService.createSku(request);
    }

    @GetMapping
    public List<Sku> list() {
        return skuService.listSkus();
    }

    @PutMapping("/{id}")
    public Sku update(@PathVariable Long id, @RequestBody SkuRequest request) {
        return skuService.updateSku(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        skuService.deleteSku(id);
    }
}
