package com.example.warehouse.dto;

public class WarehouseUpdateRequest {
    private String name;
    private Integer totalRows;
    private Integer totalCols;
    private Integer totalSides;
    private Double palletVolume;
    private Double horizontalSpeed;
    private Double verticalSpeed;
    private Integer entryRow;
    private Integer entryCol;
    private Integer entrySide;

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

    public Integer getTotalSides() {
        return totalSides;
    }

    public void setTotalSides(Integer totalSides) {
        this.totalSides = totalSides;
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

    public Integer getEntrySide() {
        return entrySide;
    }

    public void setEntrySide(Integer entrySide) {
        this.entrySide = entrySide;
    }
}
