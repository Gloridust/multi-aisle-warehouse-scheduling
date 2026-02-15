package com.example.warehouse.model;

public class StorageLocation {
    private Long id;
    private Long warehouseId;
    private Integer rowNum;
    private Integer colNum;
    private Integer sideNum;
    private Integer status;
    private Long currentSkuId;
    private Integer currentQty;
    private Double usedVolume;
    private String skuImageBase64;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
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

    public Integer getSideNum() {
        return sideNum;
    }

    public void setSideNum(Integer sideNum) {
        this.sideNum = sideNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCurrentSkuId() {
        return currentSkuId;
    }

    public void setCurrentSkuId(Long currentSkuId) {
        this.currentSkuId = currentSkuId;
    }

    public Integer getCurrentQty() {
        return currentQty;
    }

    public void setCurrentQty(Integer currentQty) {
        this.currentQty = currentQty;
    }

    public Double getUsedVolume() {
        return usedVolume;
    }

    public void setUsedVolume(Double usedVolume) {
        this.usedVolume = usedVolume;
    }

    public String getSkuImageBase64() {
        return skuImageBase64;
    }

    public void setSkuImageBase64(String skuImageBase64) {
        this.skuImageBase64 = skuImageBase64;
    }
}
