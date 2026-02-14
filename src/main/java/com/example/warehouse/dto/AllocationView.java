package com.example.warehouse.dto;

public class AllocationView {
    private Long locationId;
    private Integer rowNum;
    private Integer colNum;
    private Long skuId;
    private String skuName;
    private Integer allocatedQty;
    private Double allocatedVolume;
    private Double accessDistance;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Integer getColNum() {
        return colNum;
    }

    public void setColNum(Integer colNum) {
        this.colNum = colNum;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Integer getAllocatedQty() {
        return allocatedQty;
    }

    public void setAllocatedQty(Integer allocatedQty) {
        this.allocatedQty = allocatedQty;
    }

    public Double getAllocatedVolume() {
        return allocatedVolume;
    }

    public void setAllocatedVolume(Double allocatedVolume) {
        this.allocatedVolume = allocatedVolume;
    }

    public Double getAccessDistance() {
        return accessDistance;
    }

    public void setAccessDistance(Double accessDistance) {
        this.accessDistance = accessDistance;
    }
}
