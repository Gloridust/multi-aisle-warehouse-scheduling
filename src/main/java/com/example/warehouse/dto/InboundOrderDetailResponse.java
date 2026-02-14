package com.example.warehouse.dto;

import com.example.warehouse.model.InboundOrder;
import com.example.warehouse.model.InboundOrderItem;

import java.util.List;

public class InboundOrderDetailResponse {
    private InboundOrder order;
    private List<InboundOrderItem> items;

    public InboundOrder getOrder() {
        return order;
    }

    public void setOrder(InboundOrder order) {
        this.order = order;
    }

    public List<InboundOrderItem> getItems() {
        return items;
    }

    public void setItems(List<InboundOrderItem> items) {
        this.items = items;
    }
}
