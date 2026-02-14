package com.example.warehouse.controller;

import com.example.warehouse.dto.OutboundOrderCreateRequest;
import com.example.warehouse.dto.OutboundOrderDetailResponse;
import com.example.warehouse.service.OutboundOrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/outbound-order")
public class OutboundOrderController {
    private final OutboundOrderService outboundOrderService;

    public OutboundOrderController(OutboundOrderService outboundOrderService) {
        this.outboundOrderService = outboundOrderService;
    }

    @PostMapping
    public Object create(@RequestBody OutboundOrderCreateRequest request) {
        return outboundOrderService.createOrder(request);
    }

    @GetMapping
    public Object list() {
        return outboundOrderService.listOrders();
    }

    @GetMapping("/{id}")
    public OutboundOrderDetailResponse detail(@PathVariable Long id) {
        return outboundOrderService.getOrderDetail(id);
    }

    @PostMapping("/{id}/execute")
    public Object execute(@PathVariable Long id) {
        return outboundOrderService.executeOutbound(id);
    }

    @GetMapping("/{id}/result")
    public Object result(@PathVariable Long id) {
        return outboundOrderService.getResults(id);
    }
}
