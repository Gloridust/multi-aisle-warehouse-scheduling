package com.example.warehouse.model;

public class AllocationResult {
    private Long id;
    private Long orderId;
    private String strategyType;
    private Long locationId;
    private Long skuId;
    private Integer allocatedQty;
    private Double allocatedVolume;
    private Double accessDistance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
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
