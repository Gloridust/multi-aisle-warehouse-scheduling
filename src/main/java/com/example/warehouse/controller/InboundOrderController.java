package com.example.warehouse.controller;

import com.example.warehouse.dto.ExecuteInboundRequest;
import com.example.warehouse.dto.InboundOrderCreateRequest;
import com.example.warehouse.dto.InboundOrderDetailResponse;
import com.example.warehouse.service.InboundOrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inbound-order")
public class InboundOrderController {
    private final InboundOrderService inboundOrderService;

    public InboundOrderController(InboundOrderService inboundOrderService) {
        this.inboundOrderService = inboundOrderService;
    }

    @PostMapping
    public Object create(@RequestBody InboundOrderCreateRequest request) {
        return inboundOrderService.createOrder(request);
    }

    @GetMapping
    public Object list() {
        return inboundOrderService.listOrders();
    }

    @GetMapping("/{id}")
    public InboundOrderDetailResponse detail(@PathVariable Long id) {
        return inboundOrderService.getOrderDetail(id);
    }

    @PostMapping("/{id}/execute")
    public Object execute(@PathVariable Long id, @RequestBody ExecuteInboundRequest request) {
        return inboundOrderService.executeInbound(id, request.getStrategyType());
    }

    @GetMapping("/{id}/allocation")
    public Object allocation(@PathVariable Long id) {
        return inboundOrderService.getAllocationResults(id);
    }
}
