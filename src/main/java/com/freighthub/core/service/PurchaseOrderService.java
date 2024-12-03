package com.freighthub.core.service;

import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.OrderStatusDto;
import com.freighthub.core.dto.PurchaseOrderDto;
import com.freighthub.core.entity.Item;
import com.freighthub.core.entity.Order;
import com.freighthub.core.entity.PurchaseOrder;
import com.freighthub.core.entity.Route;
import com.freighthub.core.enums.OrderStatus;
import com.freighthub.core.repository.ItemRepository;
import com.freighthub.core.repository.OrderRepository;
import com.freighthub.core.repository.PurchaseOrderRepository;
import com.freighthub.core.repository.RouteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationService notificationService;

    private PurchaseOrderDto mapToPurchaseOrderDto(PurchaseOrder purchaseOrder) {
        // Map PurchaseOrder entity to PurchaseOrderDto
        return new PurchaseOrderDto(
                purchaseOrder.getId(),
                purchaseOrder.getPoNumber(),
                purchaseOrder.getStoreName(),
                purchaseOrder.getContactNumber(),
                purchaseOrder.getEmail(),
                purchaseOrder.getStatus(),
                purchaseOrder.getAddress(),
                purchaseOrder.getOtp(),
                purchaseOrder.isLtlFlag(),
                RouteService.PointConverter.getLatitude(purchaseOrder.getDropLocation()),
                RouteService.PointConverter.getLongitude(purchaseOrder.getDropLocation()),
                purchaseOrder.getOrderId() != null ? purchaseOrder.getOrderId().getId() : null
        );
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getAllPurchaseOrders() {
        // Fetch all purchase orders and map to DTOs
        return purchaseOrderRepository.findAll()
                .stream()
                .map(this::mapToPurchaseOrderDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPurchaseOrderById(int id) {
        // Fetch purchase order by ID and map to DTO
        PurchaseOrderDto po = purchaseOrderRepository.findById(id)
                .map(this::mapToPurchaseOrderDto)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with ID: " + id));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(po.getId());
        List<Item> items = itemRepository.findByPoId(purchaseOrder);
        Map<String, Object> poAndItems = Map.of("purchaseOrder", po, "items", items);
        return poAndItems;
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getPurchaseOrdersByOrderId(int id) {
        // Find order by ID
        Order order = orderRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        // Fetch purchase orders by order and map to DTOs
        return purchaseOrderRepository.findByOrderId(order)
                .stream()
                .map(this::mapToPurchaseOrderDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void completePurchaseOrder(@Valid OrderStatusDto purchaseOrderDto) {
        // Step 1: Find the Purchase Order by ID
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderDto.getPo_id())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        Route route = routeRepository.findById(purchaseOrderDto.getRoute_id())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        // Step 2: Check if the OTP matches
        if (!purchaseOrder.getOtp().equals(purchaseOrderDto.getOtp())) {
            throw new RuntimeException("Invalid OTP for the Purchase Order");
        }

        // Step 3: Update OrderStatus of Items in ItemRepository
        List<Item> items = itemRepository.findByPoIdAndRouteId(purchaseOrder, route);
        if (items.isEmpty()) {
            throw new RuntimeException("No items found for the given Purchase Order and Route ID");
        }

        // Step 4: Set the status of all matching items to 'completed'
        for (Item item : items) { item.setStatus(OrderStatus.completed); }
        itemRepository.saveAll(items);

        // Step 6: Check if all items for the given PO are completed
        boolean allCompleted = itemRepository.areAllItemsCompleted(purchaseOrderDto.getPo_id(), OrderStatus.completed);
        if (allCompleted) {
            purchaseOrder.setStatus(OrderStatus.completed); // Set PO status to completed
            purchaseOrderRepository.save(purchaseOrder);

            // Step 7: Get the order_id from the completed purchase order
            Order orderId = purchaseOrder.getOrderId();

            notificationService.addNotificationRoute("Purchase order with number #" + purchaseOrder.getPoNumber() + " has been completed!", orderId.getId());

            // Step 8: Check if all POs associated with this order_id are completed
            List<PurchaseOrder> associatedPos = purchaseOrderRepository.findByOrderId(orderId);
            boolean allPosCompleted = associatedPos.stream()
                    .allMatch(po -> po.getStatus() == OrderStatus.completed);

            // Step 9: If all POs for the order are completed, update the order status to completed
            if (allPosCompleted) {
                Order order = orderRepository.findById((long) orderId.getId())
                        .orElseThrow(() -> new RuntimeException("Order not found for Order ID: " + orderId));
                order.setStatus(OrderStatus.completed); // Set Order status to completed
                orderRepository.save(order);

                // Notify the consigner that the order has been completed
                notificationService.addNotificationRoute("Congratulation! Your order #" + order.getId() + " has been completed, and successfully delivered!", order.getId());

            }
        }

        // Step 10: Check if all items for the given route are completed
        boolean allRouteItemsCompleted = itemRepository.areAllItemsCompletedForRoute(purchaseOrderDto.getRoute_id(), OrderStatus.completed);
        if (allRouteItemsCompleted) {
            // Update the status of the route to 'completed' if all items for the route are completed
            route.setStatus(OrderStatus.completed); // Assuming Route has a status field
            routeRepository.save(route);
        }
    }

    public void completePurchaseOrderForce(@Valid OrderStatusDto purchaseOrderDto) {

        // Find the Purchase Order by ID
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderDto.getPo_id())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        // Check if the OTP matches
        if (!purchaseOrder.getOtp().equals(purchaseOrderDto.getOtp())) {
            throw new RuntimeException("Invalid OTP for the Purchase Order");
        }

        // Update OrderStatus of Items in ItemRepository
        List<Item> items = itemRepository.findByPoId(
                purchaseOrder
        );

        if (items.isEmpty()) {
            throw new RuntimeException("No items found for the given Purchase Order and Route ID");
        }

        // Set the status of all matching items to 'completed'
        for (Item item : items) {
            item.setStatus(OrderStatus.completed);
        }

        // Save the updated items back to the repository
        itemRepository.saveAll(items);

        // Optionally, update the status of the Purchase Order if all items are now completed
        boolean allCompleted = itemRepository.areAllItemsCompleted(purchaseOrderDto.getPo_id(), OrderStatus.completed);
        if (allCompleted) {
            purchaseOrder.setStatus(OrderStatus.completed); // Assuming PurchaseOrder has a status field
            purchaseOrderRepository.save(purchaseOrder);
        }
    }

    @Transactional
    public void unfulfilledPurchaseOrder(@Valid OrderStatusDto purchaseOrderDto) {
        // Find the Purchase Order by ID
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderDto.getPo_id())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        Route route = routeRepository.findById(purchaseOrderDto.getRoute_id())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        // Check if the OTP matches
        if (!purchaseOrder.getOtp().equals(purchaseOrderDto.getOtp())) {
            throw new RuntimeException("Invalid OTP for the Purchase Order");
        }

        //  Update OrderStatus of Items in ItemRepository
        List<Item> items = itemRepository.findByPoIdAndRouteId(purchaseOrder, route);
        if (items.isEmpty()) {
            throw new RuntimeException("No items found for the given Purchase Order and Route ID");
        }

        //  Set the status of all matching items to 'completed'
        for (Item item : items) { item.setStatus(OrderStatus.unfulfilled); }
        itemRepository.saveAll(items);

        purchaseOrder.setStatus(OrderStatus.unfulfilled); // Set PO status to completed
        purchaseOrderRepository.save(purchaseOrder);

        route.setStatus(OrderStatus.unfulfilled); // Set Route status to unfulfilled
        routeRepository.save(route);

        // Get the order_id from the completed purchase order
        Order orderId = purchaseOrder.getOrderId();
        orderId.setStatus(OrderStatus.unfulfilled); // Set Order status to unfulfilled
        orderRepository.save(orderId);

        // Notify the consigner that the order has been unfulfilled
        notificationService.addNotificationRoute("We are Sorry! Your order #" + orderId.getId() + " had some trouble with delivery :(. Please check your orders for more details", orderId.getId());
    }

    @Transactional
    public void unloadPurchaseOrder(@Valid OrderStatusDto purchaseOrderDto) {
        // Step 1: Find the Purchase Order by ID
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderDto.getPo_id())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        Route route = routeRepository.findById(purchaseOrderDto.getRoute_id())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        // Step 3: Update OrderStatus of Items in ItemRepository
        List<Item> items = itemRepository.findByPoIdAndRouteId(purchaseOrder, route);
        if (items.isEmpty()) {
            throw new RuntimeException("No items found for the given Purchase Order and Route ID");
        }

        // Step 4: Set the status of all matching items to 'unloading'
        for (Item item : items) { item.setStatus(OrderStatus.unloading); }
        itemRepository.saveAll(items);
    }
}
