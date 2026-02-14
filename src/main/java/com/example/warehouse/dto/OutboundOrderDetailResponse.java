package com.example.warehouse.dto;

import com.example.warehouse.model.OutboundOrder;
import com.example.warehouse.model.OutboundOrderItem;

import java.util.List;

public class OutboundOrderDetailResponse {
    private OutboundOrder order;
    private List<OutboundOrderItem> items;

    public OutboundOrder getOrder() {
        return order;
    }

    public void setOrder(OutboundOrder order) {
        this.order = order;
    }

    public List<OutboundOrderItem> getItems() {
        return items;
    }

    public void setItems(List<OutboundOrderItem> items) {
        this.items = items;
    }
}
