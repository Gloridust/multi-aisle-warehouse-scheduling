package com.example.warehouse.dto;

import java.util.List;

public class OutboundOrderCreateRequest {
    private String orderNo;
    private List<OutboundOrderItemRequest> items;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public List<OutboundOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OutboundOrderItemRequest> items) {
        this.items = items;
    }
}
