package com.freighthub.core.service;

import com.freighthub.core.dto.*;
import com.freighthub.core.entity.*;
import com.freighthub.core.enums.OrderStatus;
import com.freighthub.core.repository.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemTypeRepository itemTypeRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private ConsignerRepository consignerRepository;

    @Autowired
    private MailService emailService;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    public Point convertToPoint(Double lat, Double lng) {
        if (lat == null || lng == null) {
            throw new IllegalArgumentException("Latitude and Longitude cannot be null");
        }
        return geometryFactory.createPoint(new Coordinate(lng, lat)); // Ensure lng first, lat second
    }

    private OrderDto convertToOrderDto(Order order) {
        // Extract latitude and longitude from the pickupLocation
        Double latitude = order.getPickupLocation() != null
                ? RouteService.PointConverter.getLatitude(order.getPickupLocation())
                : null;
        Double longitude = order.getPickupLocation() != null
                ? RouteService.PointConverter.getLongitude(order.getPickupLocation())
                : null;

        // Extract user ID from the associated user entity
        Integer userId = order.getUserId() != null ? order.getUserId().getId() : null;

        // Build and return the DTO
        return new OrderDto(
                order.getId(),               // ID
                order.getOrderTime(),        // Order time
                order.getPickupDate(),       // Pickup date
                order.getFromTime(),         // From time
                order.getToTime(),
                order.getOtp(), // To time
                new LocationPoint(latitude, longitude), // Pickup location (converted to LocationPoint)
                order.getStatus(),           // Status
                order.getPickupPoint(),
                userId                       // User ID
        );
    }

    @Transactional
    public Order saveOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setPickupDate(orderDto.getPickupDate());
        order.setFromTime(orderDto.getFromTime());
        order.setToTime(orderDto.getToTime());
        order.setPickupLocation(convertToPoint(orderDto.getPickupLocation().getLat(), orderDto.getPickupLocation().getLng()));
        order.setPickupPoint(orderDto.getPickupPoint());

        Integer otp = new Random().nextInt(9000) + 1000;
        order.setOtp(otp);

        User user = userRepository.findById((long) orderDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        order.setUserId(user);
        order = orderRepository.save(order);

        for (PurchaseOrderDto purchaseOrderDto : orderDto.getPurchaseOrders()) {
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setPoNumber(purchaseOrderDto.getPoNumber());
            purchaseOrder.setStoreName(purchaseOrderDto.getStoreName());
            purchaseOrder.setDropDate(purchaseOrderDto.getDropDate());
            purchaseOrder.setDropTime(purchaseOrderDto.getDropTime());
            purchaseOrder.setContactNumber(purchaseOrderDto.getContactNumber());
            purchaseOrder.setEmail(purchaseOrderDto.getEmail());
            purchaseOrder.setAddress(purchaseOrderDto.getAddress());
            purchaseOrder.setLtlFlag(purchaseOrderDto.isLtlFlag());
            purchaseOrder.setDropLocation(convertToPoint(purchaseOrderDto.getDropLocation().getLat(), purchaseOrderDto.getDropLocation().getLng()));
            purchaseOrder.setOrderId(order);

            Integer p_otp = new Random().nextInt(9000) + 1000;
            purchaseOrder.setOtp(p_otp);

            purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
            System.out.println("purchase order id: " + purchaseOrder.getId());
            Consigner consigner = consignerRepository.findById((long) orderDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println("lkasdlaksd");

            // send email to purchase order email, a po_id embedded in url /consignee/po/{id}
            String subject = "Purchase Order Created";
            String text = "Your Purchase Order has been created. Please click the link to view the details: http://localhost:3000/consignee/po/" + purchaseOrder.getId();

            emailService.sendSimpleMail(purchaseOrder.getEmail(), subject, text);

            System.out.println("\nemail sent");

            for (ItemDto itemDto : purchaseOrderDto.getItems()) {
                Item item = new Item();
                item.setItemName(itemDto.getItemName());
                item.setWeight(itemDto.getWeight());
                item.setCbm(itemDto.getCbm());
                item.setRefrigerated(itemDto.getRefrigerated());
                item.setHazardous(itemDto.getHazardous());
                item.setPerishable(itemDto.getPerishable());
                item.setFragile(itemDto.getFragile());
                item.setSequenceNumber(itemDto.getSequenceNumber());
                item.setSafeDelivery(itemDto.getSafeDelivery());
                item.setPoId(purchaseOrder); // Set foreign key
                System.out.println(itemDto.getITypeId());
                System.out.println(itemDto.getCbm());
                ItemType type = itemTypeRepository.findById((int) itemDto.getITypeId()).orElseThrow(() -> new RuntimeException("Item Type not found"));
                item.setITypeId(type);

                itemRepository.save(item);
            }
        }

        return order;

    }

    @Transactional
    public void cancelOrder(GetAnyId orderId) {
        // Step 1: Find the Order by ID
        Order order = orderRepository.findById((long) orderId.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        // Step 2: Get all Routes associated with the Order
        List<Route> routes = routeRepository.findByOrderId(order);

        boolean hasAssignedVehicles = false;
        try {
            // Step 3: Check if any vehicles are assigned to the routes
            hasAssignedVehicles = routes.stream().anyMatch(route -> route.getVehicleId() != null);
        } catch (Exception e) {
            throw new RuntimeException("Error checking for assigned vehicles");
        }

        System.out.println("hasAssignedVehicles: " + hasAssignedVehicles);

        // Step 4: Calculate cost adjustment and update status
        LocalDate today = LocalDate.now();
        System.out.println("Today: " + today);
        for (Route route : routes) {
            if (hasAssignedVehicles && route.getOrderId().getPickupDate().minusDays(3).isBefore(today)) {
                // If near pickup date, apply 20% of estimated cost to cost
                BigDecimal adjustedCost = route.getEstdCost().multiply(BigDecimal.valueOf(0.20));
                System.out.println("Adjusted cost: " + adjustedCost);
                route.setCost(adjustedCost);
            } else if (route.getOrderId().getPickupDate().minusDays(2).isBefore(today)) {
                // If near pickup date, apply 10%
                BigDecimal adjustedCost = route.getEstdCost().multiply(BigDecimal.valueOf(0.10));
                System.out.println("Adjusted cost: " + adjustedCost);
                route.setCost(adjustedCost);
            } else {
                route.setCost(new BigDecimal("0.00"));
            }
            // Set route status to cancelled
            route.setStatus(OrderStatus.cancelled);
        }
        routeRepository.saveAll(routes);

        // Step 5: Update Order status to cancelled
        order.setStatus(OrderStatus.cancelled);
        orderRepository.save(order);

        // Step 6: Cancel associated Purchase Orders
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByOrderId(order);
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            purchaseOrder.setStatus(OrderStatus.cancelled);
            purchaseOrderRepository.save(purchaseOrder);

            // Step 7: Cancel Items belonging to the Purchase Order
            List<Item> items = itemRepository.findByPoId(purchaseOrder);
            for (Item item : items) {
                item.setStatus(OrderStatus.cancelled);
            }
            itemRepository.saveAll(items);
        }

        // Step 8: Save all cancellations
        purchaseOrderRepository.saveAll(purchaseOrders);


    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        // Fetch all orders and map to DTOs
        return orderRepository.findAll().stream()
                .map(this::convertToOrderDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersForConsigner(GetAnyId userRequest) {
        // Fetch the orders for the consigner using the user ID from the request
        User user = new User();
        user.setId(userRequest.getId());
        return orderRepository.findByUserId(user).stream()
                .map(this::convertToOrderDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(GetAnyId orderRequest) {
        // Fetch the order and convert to DTO
        Order order = orderRepository.findById((long) orderRequest.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToOrderDto(order);
    }

    @Transactional
    public void  makePendingOrder(Order order) {
        order.setStatus(OrderStatus.pending);
        orderRepository.save(order);

        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByOrderId(order);
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            purchaseOrder.setStatus(OrderStatus.pending);
            purchaseOrderRepository.save(purchaseOrder);

            List<Item> items = itemRepository.findByPoId(purchaseOrder);
            for (Item item : items) {
                item.setStatus(OrderStatus.pending);
                itemRepository.save(item);
            }
        }

    }

    @Transactional
    public List<Route> getOrderTransactions(int id) {
        Order order = orderRepository.findById((long) id).orElse(null);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        return routeRepository.findByOrderId(order);


    }

    @Transactional
    public Object viewOrder(GetAnyId orderId) {
        Order order = orderRepository.findById((long) orderId.getId()).orElseThrow(() -> new RuntimeException("Order not found"));
        System.out.println("order id- "+order.getId());
        List<Route> routes = routeRepository.findAllByOrderId(order);
        System.out.println("route - "+routes.getFirst().getId());

        List<ViewOrderDto> dto = routes.stream().map(this::mapRouteToDto).toList();
        System.out.println(dto.getFirst());
        return dto;

//        return routes.getFirst();
    }


    // Helper to map a Route to ViewOrderDto
    private ViewOrderDto mapRouteToDto(Route route) {
        List<Integer> poIds = itemRepository.findDistinctPoIdsByRouteId(route.getId());
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAllByIdIn(poIds);
        System.out.println("po id- "+purchaseOrders.getFirst().getId());

        System.out.println(route.getVTypeId().getType());


        ViewOrderDto dto = new ViewOrderDto(
                route.getId(),
                route.getPath(),
                route.getDistanceKm(),
                route.getActualDistanceKm(),
                route.getCost(),
                route.getTimeMinutes(),
                route.getStatus(),
                route.getVTypeId().getType(),
                route.getVehicleId()
        );
        System.out.println("dto route id -" + dto.getRouteId());
//        return dto;
        dto.setPurchaseOrderDtos(purchaseOrders.stream().map(po -> mapPurchaseOrderToDto(po, route)).toList());
        return dto;
    }

    // Helper to map a PurchaseOrder to PurchaseOrderDto
    private PurchaseOrderDto mapPurchaseOrderToDto(PurchaseOrder po, Route routeId) {
        List<Item> items = itemRepository.findByPoIdAndRouteId(po, routeId);
        System.out.println("po id - " + po.getId());// Pass both PO ID and Route ID
        System.out.println("item id - "+items.getFirst().getId());

        PurchaseOrderDto dto = new PurchaseOrderDto(
                po.getId(),
                po.getPoNumber(),
                po.getStoreName(),
                po.getDropDate(),
                po.getDropTime(),
                po.getContactNumber(),
                po.getEmail(),
                po.getStatus(),
                po.getAddress(),
                po.getOtp(),
                po.isLtlFlag(),
                PointConverter.getLatitude(po.getDropLocation()),
                PointConverter.getLongitude(po.getDropLocation()),
                po.getOrderId()
        );
        dto.setItems(items.stream().map(this::mapItemToDto).toList());
        System.out.println("dto route id -" + dto.getId());
        return dto;
    }

    // Helper to map Point to LocationPoint
    private LocationPoint mapPointToLocationPoint(Point point) {
        return new LocationPoint(point.getX(), point.getY());
    }

    @Transactional
    public Object getDistinctMonths(GetAnyId consigner) {
        List<Object> orders = orderRepository.findDistinctMonthsByConsignerId(consigner.getId());
        return orders.stream()
                .map(order -> order.toString().substring(0, 7)) // Extract "yyyy-MM" part
                .distinct() // Ensure uniqueness
                .collect(Collectors.toList());
    }


    public static class PointConverter {
        public static Double getLatitude(Point point) {
            return point != null ? point.getY() : null;
        }

        public static Double getLongitude(Point point) {
            return point != null ? point.getX() : null;
        }
    }

    // Helper to map an Item to ItemDto
    private ItemDto mapItemToDto(Item item) {

        return new ItemDto(
                item.getId(),
                item.getItemName(),
                item.getWeight(),
                item.getCbm(),
                item.getStatus(),
                item.getSequenceNumber()
        );
    }
}
