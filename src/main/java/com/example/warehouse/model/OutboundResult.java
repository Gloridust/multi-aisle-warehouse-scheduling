package com.example.warehouse.model;

public class OutboundResult {
    private Long id;
    private Long orderId;
    private Long locationId;
    private Long skuId;
    private Integer pickedQty;
    private Double pickedVolume;
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

    public Integer getPickedQty() {
        return pickedQty;
    }

    public void setPickedQty(Integer pickedQty) {
        this.pickedQty = pickedQty;
    }

    public Double getPickedVolume() {
        return pickedVolume;
    }

    public void setPickedVolume(Double pickedVolume) {
        this.pickedVolume = pickedVolume;
    }

    public Double getAccessDistance() {
        return accessDistance;
    }

    public void setAccessDistance(Double accessDistance) {
        this.accessDistance = accessDistance;
    }
}
