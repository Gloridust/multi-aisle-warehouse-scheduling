package com.example.warehouse.service;

import com.example.warehouse.dto.AllocationView;
import com.example.warehouse.dto.InboundOrderCreateRequest;
import com.example.warehouse.dto.InboundOrderDetailResponse;
import com.example.warehouse.model.AllocationResult;
import com.example.warehouse.model.InboundOrder;
import com.example.warehouse.model.InboundOrderItem;
import com.example.warehouse.model.Sku;
import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.model.Warehouse;
import com.example.warehouse.repository.AllocationResultRepository;
import com.example.warehouse.repository.InboundOrderItemRepository;
import com.example.warehouse.repository.InboundOrderRepository;
import com.example.warehouse.repository.SkuRepository;
import com.example.warehouse.repository.StorageLocationRepository;
import com.example.warehouse.repository.WarehouseRepository;
import com.example.warehouse.strategy.AllocationStrategy;
import com.example.warehouse.strategy.StrategyFactory;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InboundOrderService {
    private final InboundOrderRepository inboundOrderRepository;
    private final InboundOrderItemRepository inboundOrderItemRepository;
    private final AllocationResultRepository allocationResultRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final SkuRepository skuRepository;
    private final WarehouseRepository warehouseRepository;
    private final StrategyFactory strategyFactory;

    public InboundOrderService(InboundOrderRepository inboundOrderRepository,
                               InboundOrderItemRepository inboundOrderItemRepository,
                               AllocationResultRepository allocationResultRepository,
                               StorageLocationRepository storageLocationRepository,
                               SkuRepository skuRepository,
                               WarehouseRepository warehouseRepository,
                               StrategyFactory strategyFactory) {
        this.inboundOrderRepository = inboundOrderRepository;
        this.inboundOrderItemRepository = inboundOrderItemRepository;
        this.allocationResultRepository = allocationResultRepository;
        this.storageLocationRepository = storageLocationRepository;
        this.skuRepository = skuRepository;
        this.warehouseRepository = warehouseRepository;
        this.strategyFactory = strategyFactory;
    }

    public InboundOrder createOrder(InboundOrderCreateRequest request) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        InboundOrder order = new InboundOrder();
        order.setOrderNo(buildOrderNo("IN", request.getOrderNo(), now));
        order.setStatus(0);
        order.setStrategyType(null);
        order.setCreateTime(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Long orderId = inboundOrderRepository.insert(order);
        List<InboundOrderItem> items = new ArrayList<>();
        for (com.example.warehouse.dto.InboundOrderItemRequest itemRequest : request.getItems()) {
            InboundOrderItem item = new InboundOrderItem();
            item.setOrderId(orderId);
            item.setSkuId(itemRequest.getSkuId());
            item.setQuantity(itemRequest.getQuantity());
            items.add(item);
        }
        inboundOrderItemRepository.insertBatch(orderId, items);
        order.setId(orderId);
        return order;
    }

    public List<InboundOrder> listOrders() {
        return inboundOrderRepository.findAll();
    }

    public InboundOrderDetailResponse getOrderDetail(Long orderId) {
        InboundOrder order = inboundOrderRepository.findById(orderId).orElseThrow();
        List<InboundOrderItem> items = inboundOrderItemRepository.findByOrderId(orderId);
        InboundOrderDetailResponse response = new InboundOrderDetailResponse();
        response.setOrder(order);
        response.setItems(items);
        return response;
    }

    public List<AllocationView> getAllocationResults(Long orderId) {
        List<AllocationResult> results = allocationResultRepository.findByOrderId(orderId);
        Map<Long, String> skuNameMap = skuRepository.findAll().stream()
                .collect(Collectors.toMap(Sku::getId, Sku::getName));
        Map<Long, StorageLocation> locationMap = storageLocationRepository.findByWarehouseId(getWarehouse().getId()).stream()
                .collect(Collectors.toMap(StorageLocation::getId, loc -> loc));
        List<AllocationView> views = new ArrayList<>();
        for (AllocationResult result : results) {
            AllocationView view = new AllocationView();
            StorageLocation location = locationMap.get(result.getLocationId());
            view.setLocationId(result.getLocationId());
            view.setRowNum(location == null ? null : location.getRowNum());
            view.setColNum(location == null ? null : location.getColNum());
            view.setSkuId(result.getSkuId());
            view.setSkuName(skuNameMap.get(result.getSkuId()));
            view.setAllocatedQty(result.getAllocatedQty());
            view.setAllocatedVolume(result.getAllocatedVolume());
            view.setAccessDistance(result.getAccessDistance());
            views.add(view);
        }
        return views;
    }

    public List<AllocationView> executeInbound(Long orderId, String strategyType) {
        InboundOrder order = inboundOrderRepository.findById(orderId).orElseThrow();
        Warehouse warehouse = getWarehouse();
        List<StorageLocation> freeLocations = storageLocationRepository.findFreeLocations(warehouse.getId());
        List<OrderItemDetail> details = buildOrderDetails(orderId);
        AllocationStrategy strategy = strategyFactory.get(strategyType);
        List<AllocationResult> allocations = strategy.allocate(warehouse, details, freeLocations);
        for (AllocationResult allocation : allocations) {
            allocation.setOrderId(orderId);
            allocation.setStrategyType(strategyType);
        }
        allocationResultRepository.deleteByOrderId(orderId);
        allocationResultRepository.insertBatch(allocations);
        for (AllocationResult allocation : allocations) {
            storageLocationRepository.updateLocationOccupied(
                    allocation.getLocationId(),
                    allocation.getSkuId(),
                    allocation.getAllocatedQty(),
                    allocation.getAllocatedVolume()
            );
        }
        inboundOrderRepository.updateStatusAndStrategy(orderId, 2, strategyType);
        return getAllocationResults(orderId);
    }

    private List<OrderItemDetail> buildOrderDetails(Long orderId) {
        List<InboundOrderItem> items = inboundOrderItemRepository.findByOrderId(orderId);
        Map<Long, com.example.warehouse.model.Sku> skuMap = skuRepository.findAll().stream()
                .collect(Collectors.toMap(com.example.warehouse.model.Sku::getId, sku -> sku));
        List<OrderItemDetail> details = new ArrayList<>();
        for (InboundOrderItem item : items) {
            OrderItemDetail detail = new OrderItemDetail();
            detail.setSkuId(item.getSkuId());
            detail.setQuantity(item.getQuantity());
            detail.setSku(skuMap.get(item.getSkuId()));
            details.add(detail);
        }
        return details;
    }

    private Warehouse getWarehouse() {
        return warehouseRepository.findFirst().orElseThrow();
    }

    private String buildOrderNo(String prefix, String provided, ZonedDateTime now) {
        if (provided != null && !provided.trim().isEmpty()) {
            return provided.trim();
        }
        String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int suffix = now.getNano() % 1000;
        return prefix + time + String.format("%03d", suffix);
    }
}
