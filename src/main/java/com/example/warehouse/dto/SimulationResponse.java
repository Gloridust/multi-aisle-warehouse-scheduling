package com.example.warehouse.dto;

import java.util.List;

public class SimulationResponse {
    private Long orderId;
    private List<SimulationResultView> results;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<SimulationResultView> getResults() {
        return results;
    }

    public void setResults(List<SimulationResultView> results) {
        this.results = results;
    }
}
