package com.example.warehouse.controller;

import com.example.warehouse.service.SimulationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {
    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/{orderId}/run")
    public Object run(@PathVariable Long orderId, @RequestParam(required = false) Long warehouseId) {
        return simulationService.runSimulation(orderId, warehouseId);
    }

    @GetMapping("/{orderId}/result")
    public Object result(@PathVariable Long orderId) {
        return simulationService.getSimulationResult(orderId);
    }
}
