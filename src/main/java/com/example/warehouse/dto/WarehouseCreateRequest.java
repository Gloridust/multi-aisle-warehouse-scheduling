package com.example.warehouse.dto;

public class WarehouseCreateRequest {
    private String name;
    private Integer totalRows;
    private Integer totalCols;
    private Double palletVolume;
    private Double horizontalSpeed;
    private Double verticalSpeed;
    private Integer entryRow;
    private Integer entryCol;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public Integer getTotalCols() {
        return totalCols;
    }

    public void setTotalCols(Integer totalCols) {
        this.totalCols = totalCols;
    }

    public Double getPalletVolume() {
        return palletVolume;
    }

    public void setPalletVolume(Double palletVolume) {
        this.palletVolume = palletVolume;
    }

    public Double getHorizontalSpeed() {
        return horizontalSpeed;
    }

    public void setHorizontalSpeed(Double horizontalSpeed) {
        this.horizontalSpeed = horizontalSpeed;
    }

    public Double getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setVerticalSpeed(Double verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public Integer getEntryRow() {
        return entryRow;
    }

    public void setEntryRow(Integer entryRow) {
        this.entryRow = entryRow;
    }

    public Integer getEntryCol() {
        return entryCol;
    }

    public void setEntryCol(Integer entryCol) {
        this.entryCol = entryCol;
    }
}
