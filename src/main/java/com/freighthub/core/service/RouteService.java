package com.freighthub.core.service;

import com.freighthub.core.dto.*;
import com.freighthub.core.entity.Item;
import com.freighthub.core.entity.PurchaseOrder;
import com.freighthub.core.entity.Route;
import com.freighthub.core.entity.Order;
import com.freighthub.core.enums.OrderStatus;
import com.freighthub.core.repository.*;
import jakarta.validation.Valid;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteService {

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ConsignerRepository consignerRepository;

    public class PointConverter {
        public static Double getLatitude(Point point) {
            return point != null ? point.getY() : null;
        }

        public static Double getLongitude(Point point) {
            return point != null ? point.getX() : null;
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRouteDetailsWithItemSequence(@Valid OrderStatusDto routeDto) {
        // Fetch distinct PO IDs and their sequence numbers from the items table for the given route ID
        List<Object[]> poIdAndSequenceNumbers = itemRepository.findDistinctPoIdsAndSequenceNumbersByRouteId(routeDto.getRoute_id());

        if (poIdAndSequenceNumbers.isEmpty()) {
            throw new RuntimeException("No Purchase Orders found for the given Route ID");
        }

        // Extract PO IDs for further processing
        List<Integer> poIds = poIdAndSequenceNumbers.stream()
                .map(result -> (Integer) result[0])
                .toList();

        // Fetch all items grouped by their PO ID
        Map<Integer, List<Item>> itemsGroupedByPoId = itemRepository.findByPoIdIn(poIds).stream()
                .collect(Collectors.groupingBy(item -> item.getPoId().getId()));

        // Process the data by pairing sequence numbers with Purchase Orders and sorting
        return poIdAndSequenceNumbers.stream()
                .sorted(Comparator.comparingInt(result -> (Integer) result[1])) // Sort by sequence number
                .map(result -> {
                    Integer poId = (Integer) result[0];
                    Integer sequenceNumber = (Integer) result[1];

                    // Prepare the response for this Purchase Order
                    Map<String, Object> poDetails = new HashMap<>();
                    poDetails.put("sequenceNumber", sequenceNumber); // Include sequence number
                    poDetails.put("purchaseOrderId", poId);

                    // Get the items for this PO ID
                    List<Item> itemsForPo = itemsGroupedByPoId.getOrDefault(poId, List.of());

                    // Determine the status of the Purchase Order based on item statuses
                    long completedCount = itemsForPo.stream()
                            .filter(item -> item.getStatus() == OrderStatus.completed)
                            .count();

                    String purchaseOrderStatus = completedCount == itemsForPo.size() ? "completed" : "ongoing";
                    poDetails.put("purchaseOrderStatus", purchaseOrderStatus);

                    // Additional details for the Purchase Order
                    poDetails.put("purchaseOrderNumber", itemsForPo.isEmpty() ? null : itemsForPo.get(0).getPoId().getPoNumber());
                    poDetails.put("storeName", itemsForPo.isEmpty() ? null : itemsForPo.get(0).getPoId().getStoreName());
                    poDetails.put("dropDate", itemsForPo.isEmpty() ? null : itemsForPo.get(0).getPoId().getDropDate());

                    // Add item details for this PO ID
                    List<Map<String, Object>> itemDetails = itemsForPo.stream()
                            .map(item -> {
                                Map<String, Object> itemDetail = new HashMap<>();
                                itemDetail.put("itemName", item.getItemName());
                                itemDetail.put("weight", item.getWeight());
                                itemDetail.put("cbm", item.getCbm());
                                itemDetail.put("status", item.getStatus());
                                return itemDetail;
                            })
                            .collect(Collectors.toList());

                    poDetails.put("items", itemDetails);
                    return poDetails;
                }).collect(Collectors.toList());
    }

    @Transactional
    public void updateRouteStatuses(@Valid OrderStatusDto routeDto) {
        // Step 1: Update the Route status to the incoming status
        Route route = routeRepository.findById(routeDto.getRoute_id())
                .orElseThrow(() -> new RuntimeException("Route not found"));
        route.setStatus(routeDto.getStatus());
        routeRepository.save(route);

        // Step 2: Update the status of all items for the given route_id
        List<Item> items = itemRepository.findByRouteId(route);
        if (items.isEmpty()) {
            throw new RuntimeException("No items found for the given Route ID");
        }

        items.forEach(item -> item.setStatus(routeDto.getStatus()));
        itemRepository.saveAll(items);

        // Step 3: Check and update status for each Purchase Order (PO)
        List<Integer> poIds = itemRepository.findDistinctPoIdsByRouteId(routeDto.getRoute_id());
        for (Integer poId : poIds) {
            PurchaseOrder po = purchaseOrderRepository.findById(poId)
                    .orElseThrow(() -> new RuntimeException("Purchase Order not found for PO ID: " + poId));
            List<Item> itemsForPo = itemRepository.findByPoId(po);

            // Check if all items for this PO have the same status as the incoming status
            boolean allMatchStatus = itemsForPo.stream()
                    .allMatch(item -> item.getStatus() == routeDto.getStatus());

            if (allMatchStatus) {
                po.setStatus(routeDto.getStatus());
                purchaseOrderRepository.save(po);
            }
        }

        // Step 4: Check and update the Order status
        if (!poIds.isEmpty()) {
            Order order = purchaseOrderRepository.findById(poIds.get(0))
                    .orElseThrow(() -> new RuntimeException("Purchase Order not found for PO ID: " + poIds.get(0)))
                    .getOrderId();

            List<PurchaseOrder> purchaseOrdersForOrder = purchaseOrderRepository.findByOrderId(order);

            // Check if all POs for the order have the same status as the incoming status
            boolean allMatchStatus = purchaseOrdersForOrder.stream()
                    .allMatch(po -> po.getStatus() == routeDto.getStatus());

            if (allMatchStatus) {
                order.setStatus(routeDto.getStatus());
                orderRepository.save(order);
            }
        }
    }

    @Transactional
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @Transactional
    public Route getRouteById(Integer routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
    }

    @Transactional
    public RouteDetailsDto getRouteDetails(Integer routeId) {
        // 1. Fetch Route
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        System.out.println(route.getId());

        // 2. Fetch Purchase Orders
        List<Integer> poIds = itemRepository.findDistinctPoIdsByRouteId(routeId);
        System.out.println(poIds.getFirst());
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAllById(poIds);
        System.out.println(purchaseOrders.getFirst().getId());

        // Convert purchase orders to DTOs with lat/lng
        List<PurchaseOrderDto> purchaseOrderDtos = purchaseOrders.stream().map(po -> {
            PurchaseOrderDto poDto = new PurchaseOrderDto(
                    po.getId(),
                    po.getPoNumber(),
                    po.getStoreName(),
                    po.getStatus(),
                    PointConverter.getLatitude(po.getDropLocation()), // Convert Point to lat
                    PointConverter.getLongitude(po.getDropLocation())); // Convert Point to lng
            return poDto;
        }).toList();

        Order order = purchaseOrderRepository.findById(poIds.getFirst())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found for PO ID: " + poIds.getFirst()))
                .getOrderId();
        System.out.println(order.getId());

        // Convert order to DTO with lat/lng
        OrderDto orderDto = new OrderDto(
                order.getId(),
                order.getFromTime(),
                order.getToTime(),
                order.getPickupDate(),
                order.getStatus(),
                PointConverter.getLatitude(order.getPickupLocation()), // Convert Point to lat
                PointConverter.getLongitude(order.getPickupLocation())); // Convert Point to lng

        // 3. Fetch Items and Calculate Summary
        RouteDetailsDto.ItemSummaryDto itemSummary = calculateItemSummary(route);
        System.out.println(itemSummary.getTotalWeight());

        // 4. Fetch Consigner Business Name
        String consignerBusinessName = consignerRepository.findBusinessName(order.getUserId().getId())
                .orElseThrow(() -> new RuntimeException("Consigner not found"));



        // Combine data into RouteDetailsDto
        return new RouteDetailsDto(route, purchaseOrderDtos, itemSummary, consignerBusinessName, orderDto);
    }

    private RouteDetailsDto.ItemSummaryDto calculateItemSummary(Route routeId) {
        BigDecimal totalWeight = itemRepository.calculateTotalWeight(routeId);
        BigDecimal totalCbm = itemRepository.calculateTotalCbm(routeId);
        List<String> itemTypeNames = itemRepository.findDistinctItemTypeNames(routeId);

        RouteDetailsDto.ItemSummaryDto itemSummary = new RouteDetailsDto.ItemSummaryDto();
        itemSummary.setTotalWeight(totalWeight);
        itemSummary.setTotalCbm(totalCbm);
        itemSummary.setItemTypeNames(itemTypeNames);

        return itemSummary;
    }

}
