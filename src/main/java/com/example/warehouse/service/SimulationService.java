package com.example.warehouse.service;

import com.example.warehouse.dto.AllocationView;
import com.example.warehouse.dto.SimulationResponse;
import com.example.warehouse.dto.SimulationResultView;
import com.example.warehouse.model.AllocationResult;
import com.example.warehouse.model.SimulationResult;
import com.example.warehouse.model.Sku;
import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.model.Warehouse;
import com.example.warehouse.repository.InboundOrderItemRepository;
import com.example.warehouse.repository.SimulationResultRepository;
import com.example.warehouse.repository.SkuRepository;
import com.example.warehouse.repository.StorageLocationRepository;
import com.example.warehouse.repository.WarehouseRepository;
import com.example.warehouse.strategy.AllocationStrategy;
import com.example.warehouse.strategy.StrategyFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SimulationService {
    private final InboundOrderItemRepository inboundOrderItemRepository;
    private final SimulationResultRepository simulationResultRepository;
    private final SkuRepository skuRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final WarehouseRepository warehouseRepository;
    private final StrategyFactory strategyFactory;

    public SimulationService(InboundOrderItemRepository inboundOrderItemRepository,
                             SimulationResultRepository simulationResultRepository,
                             SkuRepository skuRepository,
                             StorageLocationRepository storageLocationRepository,
                             WarehouseRepository warehouseRepository,
                             StrategyFactory strategyFactory) {
        this.inboundOrderItemRepository = inboundOrderItemRepository;
        this.simulationResultRepository = simulationResultRepository;
        this.skuRepository = skuRepository;
        this.storageLocationRepository = storageLocationRepository;
        this.warehouseRepository = warehouseRepository;
        this.strategyFactory = strategyFactory;
    }

    public SimulationResponse runSimulation(Long orderId, Long warehouseId) {
        Warehouse warehouse = warehouseId == null
                ? warehouseRepository.findFirst().orElseThrow()
                : warehouseRepository.findById(warehouseId).orElseThrow();
        List<StorageLocation> freeLocations = storageLocationRepository.findFreeLocations(warehouse.getId());
        List<OrderItemDetail> details = buildOrderDetails(orderId);
        List<String> strategyTypes = List.of("CATEGORY", "NEAREST", "GENETIC");
        simulationResultRepository.deleteByOrderId(orderId);
        Map<Long, StorageLocation> locationMap = storageLocationRepository.findByWarehouseId(warehouse.getId())
                .stream().collect(Collectors.toMap(StorageLocation::getId, loc -> loc));
        Map<Long, Sku> skuMap = skuRepository.findAll().stream()
                .collect(Collectors.toMap(Sku::getId, sku -> sku));
        List<SimulationResultView> resultViews = new ArrayList<>();
        for (String strategyType : strategyTypes) {
            AllocationStrategy strategy = strategyFactory.get(strategyType);
            long start = System.currentTimeMillis();
            List<AllocationResult> allocations = strategy.allocate(warehouse, details, freeLocations);
            long computeTime = System.currentTimeMillis() - start;
            SimulationResultView view = new SimulationResultView();
            view.setStrategyType(strategyType);
            view.setAllocations(toAllocationViews(allocations, locationMap, skuMap));
            view.setUsedLocations(allocations.size());
            double totalDistance = allocations.stream().mapToDouble(AllocationResult::getAccessDistance).sum();
            view.setTotalDistance(totalDistance);
            view.setAvgDistance(allocations.isEmpty() ? 0 : totalDistance / allocations.size());
            double usedVolume = allocations.stream().mapToDouble(AllocationResult::getAllocatedVolume).sum();
            double capacity = allocations.size() * warehouse.getPalletVolume();
            view.setSpaceUtilization(capacity == 0 ? 0 : (usedVolume / capacity) * 100);
            view.setComputeTime(computeTime);
            SimulationResult simulationResult = new SimulationResult();
            simulationResult.setOrderId(orderId);
            simulationResult.setStrategyType(strategyType);
            simulationResult.setTotalDistance(view.getTotalDistance());
            simulationResult.setAvgDistance(view.getAvgDistance());
            simulationResult.setSpaceUtilization(view.getSpaceUtilization());
            simulationResult.setUsedLocations(view.getUsedLocations());
            simulationResult.setComputeTime(view.getComputeTime());
            simulationResultRepository.insert(simulationResult);
            resultViews.add(view);
        }
        SimulationResponse response = new SimulationResponse();
        response.setOrderId(orderId);
        response.setResults(resultViews);
        return response;
    }

    public SimulationResponse getSimulationResult(Long orderId) {
        SimulationResponse response = new SimulationResponse();
        response.setOrderId(orderId);
        List<SimulationResultView> views = new ArrayList<>();
        Map<String, SimulationResult> resultMap = simulationResultRepository.findByOrderId(orderId)
                .stream().collect(Collectors.toMap(SimulationResult::getStrategyType, result -> result));
        for (String strategyType : List.of("CATEGORY", "NEAREST", "GENETIC")) {
            SimulationResultView view = new SimulationResultView();
            view.setStrategyType(strategyType);
            SimulationResult result = resultMap.get(strategyType);
            if (result != null) {
                view.setTotalDistance(result.getTotalDistance());
                view.setAvgDistance(result.getAvgDistance());
                view.setSpaceUtilization(result.getSpaceUtilization());
                view.setUsedLocations(result.getUsedLocations());
                view.setComputeTime(result.getComputeTime());
            }
            views.add(view);
        }
        response.setResults(views);
        return response;
    }

    private List<OrderItemDetail> buildOrderDetails(Long orderId) {
        Map<Long, Sku> skuMap = skuRepository.findAll().stream()
                .collect(Collectors.toMap(Sku::getId, sku -> sku));
        List<OrderItemDetail> details = new ArrayList<>();
        inboundOrderItemRepository.findByOrderId(orderId).forEach(item -> {
            OrderItemDetail detail = new OrderItemDetail();
            detail.setSkuId(item.getSkuId());
            detail.setQuantity(item.getQuantity());
            detail.setSku(skuMap.get(item.getSkuId()));
            details.add(detail);
        });
        return details;
    }

    private List<AllocationView> toAllocationViews(List<AllocationResult> allocations,
                                                   Map<Long, StorageLocation> locationMap,
                                                   Map<Long, Sku> skuMap) {
        List<AllocationView> views = new ArrayList<>();
        for (AllocationResult allocation : allocations) {
            AllocationView view = new AllocationView();
            StorageLocation location = locationMap.get(allocation.getLocationId());
            view.setLocationId(allocation.getLocationId());
            view.setRowNum(location == null ? null : location.getRowNum());
            view.setColNum(location == null ? null : location.getColNum());
            view.setSkuId(allocation.getSkuId());
            Sku sku = skuMap.get(allocation.getSkuId());
            view.setSkuName(sku == null ? null : sku.getName());
            view.setAllocatedQty(allocation.getAllocatedQty());
            view.setAllocatedVolume(allocation.getAllocatedVolume());
            view.setAccessDistance(allocation.getAccessDistance());
            views.add(view);
        }
        return views;
    }
}
