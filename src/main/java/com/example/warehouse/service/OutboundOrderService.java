package com.example.warehouse.service;

import com.example.warehouse.dto.OutboundOrderCreateRequest;
import com.example.warehouse.dto.OutboundOrderDetailResponse;
import com.example.warehouse.dto.OutboundResultView;
import com.example.warehouse.model.OutboundOrder;
import com.example.warehouse.model.OutboundOrderItem;
import com.example.warehouse.model.OutboundResult;
import com.example.warehouse.model.Sku;
import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.model.Warehouse;
import com.example.warehouse.repository.OutboundOrderItemRepository;
import com.example.warehouse.repository.OutboundOrderRepository;
import com.example.warehouse.repository.OutboundResultRepository;
import com.example.warehouse.repository.SkuRepository;
import com.example.warehouse.repository.StorageLocationRepository;
import com.example.warehouse.repository.WarehouseRepository;
import com.example.warehouse.strategy.AccessTimeCalculator;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OutboundOrderService {
    private final OutboundOrderRepository outboundOrderRepository;
    private final OutboundOrderItemRepository outboundOrderItemRepository;
    private final OutboundResultRepository outboundResultRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final SkuRepository skuRepository;
    private final WarehouseRepository warehouseRepository;

    public OutboundOrderService(OutboundOrderRepository outboundOrderRepository,
                                OutboundOrderItemRepository outboundOrderItemRepository,
                                OutboundResultRepository outboundResultRepository,
                                StorageLocationRepository storageLocationRepository,
                                SkuRepository skuRepository,
                                WarehouseRepository warehouseRepository) {
        this.outboundOrderRepository = outboundOrderRepository;
        this.outboundOrderItemRepository = outboundOrderItemRepository;
        this.outboundResultRepository = outboundResultRepository;
        this.storageLocationRepository = storageLocationRepository;
        this.skuRepository = skuRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public OutboundOrder createOrder(OutboundOrderCreateRequest request) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        OutboundOrder order = new OutboundOrder();
        order.setOrderNo(buildOrderNo("OUT", request.getOrderNo(), now));
        order.setStatus(0);
        order.setCreateTime(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Long orderId = outboundOrderRepository.insert(order);
        List<OutboundOrderItem> items = new ArrayList<>();
        for (com.example.warehouse.dto.OutboundOrderItemRequest itemRequest : request.getItems()) {
            OutboundOrderItem item = new OutboundOrderItem();
            item.setOrderId(orderId);
            item.setSkuId(itemRequest.getSkuId());
            item.setQuantity(itemRequest.getQuantity());
            items.add(item);
        }
        outboundOrderItemRepository.insertBatch(orderId, items);
        order.setId(orderId);
        return order;
    }

    public List<OutboundOrder> listOrders() {
        return outboundOrderRepository.findAll();
    }

    public OutboundOrderDetailResponse getOrderDetail(Long orderId) {
        OutboundOrder order = outboundOrderRepository.findById(orderId).orElseThrow();
        List<OutboundOrderItem> items = outboundOrderItemRepository.findByOrderId(orderId);
        OutboundOrderDetailResponse response = new OutboundOrderDetailResponse();
        response.setOrder(order);
        response.setItems(items);
        return response;
    }

    public List<OutboundResultView> executeOutbound(Long orderId) {
        OutboundOrder order = outboundOrderRepository.findById(orderId).orElseThrow();
        Warehouse warehouse = getWarehouse();
        List<OutboundOrderItem> items = outboundOrderItemRepository.findByOrderId(orderId);
        Map<Long, Sku> skuMap = skuRepository.findAll().stream()
                .collect(Collectors.toMap(Sku::getId, sku -> sku));
        List<OutboundResult> results = new ArrayList<>();
        for (OutboundOrderItem item : items) {
            Sku sku = skuMap.get(item.getSkuId());
            if (sku == null) {
                continue;
            }
            List<StorageLocation> locations = storageLocationRepository.findOccupiedBySku(warehouse.getId(), item.getSkuId());
            locations.sort(Comparator.comparingDouble(loc -> AccessTimeCalculator.calcAccessTime(loc, warehouse)));
            int remaining = item.getQuantity();
            for (StorageLocation location : locations) {
                if (remaining <= 0) {
                    break;
                }
                Integer currentQty = location.getCurrentQty();
                if (currentQty == null || currentQty <= 0) {
                    continue;
                }
                int pickedQty = Math.min(currentQty, remaining);
                double pickedVolume = pickedQty * sku.getUnitVolume();
                double distance = AccessTimeCalculator.calcAccessTime(location, warehouse);
                OutboundResult result = new OutboundResult();
                result.setOrderId(orderId);
                result.setLocationId(location.getId());
                result.setSkuId(item.getSkuId());
                result.setPickedQty(pickedQty);
                result.setPickedVolume(pickedVolume);
                result.setAccessDistance(distance);
                results.add(result);
                int newQty = currentQty - pickedQty;
                if (newQty <= 0) {
                    storageLocationRepository.updateLocationAfterOutbound(location.getId(), null, null, 0, null);
                } else {
                    double newUsedVolume = newQty * sku.getUnitVolume();
                    storageLocationRepository.updateLocationAfterOutbound(location.getId(), newQty, newUsedVolume, 1, item.getSkuId());
                }
                remaining -= pickedQty;
            }
        }
        outboundResultRepository.deleteByOrderId(orderId);
        outboundResultRepository.insertBatch(results);
        outboundOrderRepository.updateStatus(order.getId(), 2);
        return getResults(orderId);
    }

    public List<OutboundResultView> getResults(Long orderId) {
        List<OutboundResult> results = outboundResultRepository.findByOrderId(orderId);
        Map<Long, String> skuNameMap = skuRepository.findAll().stream()
                .collect(Collectors.toMap(Sku::getId, Sku::getName));
        Map<Long, StorageLocation> locationMap = storageLocationRepository.findByWarehouseId(getWarehouse().getId()).stream()
                .collect(Collectors.toMap(StorageLocation::getId, loc -> loc));
        List<OutboundResultView> views = new ArrayList<>();
        for (OutboundResult result : results) {
            OutboundResultView view = new OutboundResultView();
            StorageLocation location = locationMap.get(result.getLocationId());
            view.setLocationId(result.getLocationId());
            view.setRowNum(location == null ? null : location.getRowNum());
            view.setColNum(location == null ? null : location.getColNum());
            view.setSkuId(result.getSkuId());
            view.setSkuName(skuNameMap.get(result.getSkuId()));
            view.setPickedQty(result.getPickedQty());
            view.setPickedVolume(result.getPickedVolume());
            view.setAccessDistance(result.getAccessDistance());
            views.add(view);
        }
        return views;
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
