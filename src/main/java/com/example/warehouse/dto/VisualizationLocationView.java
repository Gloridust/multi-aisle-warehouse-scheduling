package com.example.warehouse.dto;

public class VisualizationLocationView {
    private Long id;
    private Integer rowNum;
    private Integer colNum;
    private Integer sideNum;
    private Integer status;
    private Long skuId;
    private String skuName;
    private Integer currentQty;
    private Double usedVolume;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
