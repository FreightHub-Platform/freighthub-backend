package com.freighthub.core.service;

import com.freighthub.core.dto.OrderStatusDto;
import com.freighthub.core.entity.Item;
import com.freighthub.core.entity.PurchaseOrder;
import com.freighthub.core.entity.Route;
import com.freighthub.core.entity.Order;
import com.freighthub.core.enums.OrderStatus;
import com.freighthub.core.repository.ItemRepository;
import com.freighthub.core.repository.OrderRepository;
import com.freighthub.core.repository.PurchaseOrderRepository;
import com.freighthub.core.repository.RouteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void acceptRouteAndUpdatePoStatus(@Valid OrderStatusDto routeDto) {
        // Step 1: Set the Route status to accepted
        Route route = routeRepository.findById(routeDto.getRoute_id())
                .orElseThrow(() -> new RuntimeException("Route not found"));
        route.setStatus(OrderStatus.accepted);  // assuming RouteStatus is an enum with accepted
        routeRepository.save(route);

        // Step 2: Set the status of all items for the given route_id to accepted
        List<Item> items = itemRepository.findByRouteId(route);
        if (items.isEmpty()) {
            throw new RuntimeException("No items found for the given Route ID");
        }

        items.forEach(item -> item.setStatus(OrderStatus.accepted)); // assuming OrderStatus is an enum with accepted
        itemRepository.saveAll(items);

        // Step 3: Check each PO for the given route and update status
        // Fetch distinct PO IDs for the given route_id from the items table
        List<Integer> poIds = itemRepository.findDistinctPoIdsByRouteId(routeDto.getRoute_id());
        System.out.println("PO IDs for the route: " + poIds);

        // Iterate through each PO and check if all items are completed
        for (Integer poId : poIds) {
            PurchaseOrder po = purchaseOrderRepository.findById(poId)
                    .orElseThrow(() -> new RuntimeException("Purchase Order not found for PO ID: " + poId));
            List<Item> itemsForPo = itemRepository.findByPoId(po);
            System.out.println("Items for PO ID " + poId + ": " + itemsForPo.get(0).getId());


            // Check if all items are completed
            long completedCount = itemsForPo.stream()
                    .filter(item -> item.getStatus() == OrderStatus.accepted)
                    .count();
            System.out.println("Completed items for PO ID " + poId + ": " + completedCount);
            System.out.println("Total items for PO ID " + poId + ": " + itemsForPo.size());

            // If all items for this PO are completed, set the PO status to accepted
            if (completedCount == itemsForPo.size()) {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                        .orElseThrow(() -> new RuntimeException("Purchase Order not found for PO ID: " + poId));
                purchaseOrder.setStatus(OrderStatus.accepted); // Set PO status to accepted
                purchaseOrderRepository.save(purchaseOrder);
            }
        }

        // Step 4: Check if all POs related to the given route's PO IDs are accepted
        // We already have the list of PO IDs for the route
        List<PurchaseOrder> purchaseOrdersForRoute = purchaseOrderRepository.findAllById(poIds);

        // Check if all PO statuses are accepted
        boolean allAccepted = purchaseOrdersForRoute.stream()
                .allMatch(po -> po.getStatus() == OrderStatus.accepted);

        // If all POs for the route are accepted, set the related Order's status to completed
        if (allAccepted) {
            // Assuming each PO has a reference to an Order via order_id
            Order order = purchaseOrdersForRoute.get(0).getOrderId();
            order.setStatus(OrderStatus.accepted);
            orderRepository.save(order);
        }


    }

}
