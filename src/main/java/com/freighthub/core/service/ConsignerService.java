package com.freighthub.core.service;

import com.freighthub.core.entity.Consigner;
import com.freighthub.core.entity.Order;
import com.freighthub.core.entity.Route;
import com.freighthub.core.entity.User;
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
import java.util.Collections;
import java.util.List;

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
    public Consigner getConsignerById(int id) {
        return consignerRepository.findById((long) id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Route> getConsignerTransactions(int id) {
        User consigner = userRepository.findById((long) id).orElse(null);
        if (consigner == null) {
            throw new RuntimeException("Consigner not found");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        List<Order> orders =  orderRepository.findByUserIdThisMonth(consigner, startOfMonth, endOfMonth)
                .orElse(Collections.emptyList());

        // Get routes for orders
        return routeRepository.findByOrderIdIn(orders);


    }
}
