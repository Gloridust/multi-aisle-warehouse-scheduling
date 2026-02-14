package com.example.warehouse.model;

public class SimulationResult {
    private Long id;
    private Long orderId;
    private String strategyType;
    private Double totalDistance;
    private Double avgDistance;
    private Double spaceUtilization;
    private Integer usedLocations;
    private Long computeTime;

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

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Double getAvgDistance() {
        return avgDistance;
    }

    public void setAvgDistance(Double avgDistance) {
        this.avgDistance = avgDistance;
    }

    public Double getSpaceUtilization() {
        return spaceUtilization;
    }

    public void setSpaceUtilization(Double spaceUtilization) {
        this.spaceUtilization = spaceUtilization;
    }

    public Integer getUsedLocations() {
        return usedLocations;
    }

    public void setUsedLocations(Integer usedLocations) {
        this.usedLocations = usedLocations;
    }

    public Long getComputeTime() {
        return computeTime;
    }

    public void setComputeTime(Long computeTime) {
        this.computeTime = computeTime;
    }
}
