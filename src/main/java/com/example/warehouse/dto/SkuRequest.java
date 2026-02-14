package com.example.warehouse.dto;

public class SkuRequest {
    private String name;
    private String category;
    private Double unitVolume;
    private Double unitWeight;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getUnitVolume() {
        return unitVolume;
    }

    public void setUnitVolume(Double unitVolume) {
        this.unitVolume = unitVolume;
    }

    public Double getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(Double unitWeight) {
        this.unitWeight = unitWeight;
    }
}
