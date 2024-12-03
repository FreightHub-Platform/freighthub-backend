package com.freighthub.core.service;

import com.freighthub.core.dto.ConsignerDto;
import com.freighthub.core.entity.Consigner;
import com.freighthub.core.entity.Order;
import com.freighthub.core.entity.Route;
import com.freighthub.core.entity.User;
import com.freighthub.core.enums.OrderStatus;
import com.freighthub.core.enums.VerifyStatus;
import com.freighthub.core.repository.ConsignerRepository;
import com.freighthub.core.repository.OrderRepository;
import com.freighthub.core.repository.RouteRepository;
import com.freighthub.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class ConsignerService {

    private static final Logger logger = LoggerFactory.getLogger(RegConsignerService.class);

    @Autowired
    private ConsignerRepository consignerRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Consigner> getConsignersByVerifyStatus(VerifyStatus verifyStatus) {
        return consignerRepository.findConsignersByVerifyStatus(verifyStatus);
    }

    @Transactional(readOnly = true)
    public List<Consigner> getAllConsigners() {
        return consignerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ConsignerDto getConsignerById(int id) {
        // Fetch the Consigner entity
        Consigner consigner = consignerRepository.findById((long) id).orElse(null);
        if (consigner == null) {
            throw new RuntimeException("Consigner not found");
        }
        String regDocBase64 = null;
        if (consigner.getRegDoc() != null) {
            regDocBase64 = Base64.getEncoder().encodeToString(consigner.getRegDoc());
        }

        // Map the entity to a DTO
        ConsignerDto consignerDto = new ConsignerDto(
                consigner.getId(),
                consigner.getUsername(),
                consigner.getRole(),
                consigner.getBusinessName(),
                consigner.getBrn(),
                regDocBase64,
                consigner.getMainNumber(),
                consigner.getAltNumber(),
                consigner.getAddressLine1(),
                consigner.getAddressLine2(),
                consigner.getCity(),
                consigner.getProvince(),
                consigner.getPostalCode(),
                consigner.getLogo(),
                consigner.getVerifyStatus(),
                consigner.getCompletion()
        );
        return consignerDto;
    }


    @Transactional(readOnly = true)
    public List<Map<String, String>> getConsignerTransactions(int id, String yearMonth) {
        User consigner = userRepository.findById((long) id).orElse(null);
        if (consigner == null) {
            throw new RuntimeException("Consigner not found");
        }

        // Parse the input Year-Month
        YearMonth parsedYearMonth;
        try {
            parsedYearMonth = YearMonth.parse(yearMonth);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid Year-Month format. Expected format: YYYY-MM");
        }

        // Calculate start and end of the month
        LocalDateTime startOfMonth = parsedYearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = parsedYearMonth.atEndOfMonth().atTime(23, 59, 59, 999999999);

        // Retrieve orders for the given consigner and month
        List<Order> orders = orderRepository.findByUserIdThisMonth(consigner, startOfMonth, endOfMonth)
                .orElse(Collections.emptyList());
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        // Prepare list of statuses for checking routes
        List<OrderStatus> statuses = List.of(OrderStatus.completed, OrderStatus.unfulfilled, OrderStatus.cancelled);

        // Get routes for orders
        List<Route> routes = routeRepository.findByOrderIdAndStatusIn(orders, statuses);
        List<Map<String, String>> routeDetails = new ArrayList<>();

        for (Route route : routes) {
            Map<String, String> routeDetail = Map.of(
                "routeId", route.getId().toString(),
                "orderId", String.valueOf(route.getOrderId().getId()),
                "status", route.getStatus().toString(),
                    "actualDistanceKm", route.getActualDistanceKm().toString(),
                    "cost", route.getCost().toString()
            );
            routeDetails.add(routeDetail);
        }
        return routeDetails;
    }

}
