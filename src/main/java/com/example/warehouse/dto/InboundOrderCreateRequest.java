package com.example.warehouse.dto;

import java.util.List;

public class InboundOrderCreateRequest {
    private String orderNo;
    private List<InboundOrderItemRequest> items;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public List<InboundOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<InboundOrderItemRequest> items) {
        this.items = items;
    }
}
