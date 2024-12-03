package com.freighthub.core.controller;

import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.OrderDto;
import com.freighthub.core.dto.RegisterRequest;
import com.freighthub.core.entity.Order;
import com.freighthub.core.entity.User;
import com.freighthub.core.service.BasicAlgoService;
import com.freighthub.core.service.ClusterService;
import com.freighthub.core.service.OrderService;
import com.freighthub.core.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/algo")
public class AlgoTestController {

    @Autowired
    private BasicAlgoService basicAlgoService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ClusterService clus;

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<User>> testAlgo() {
        try {
            basicAlgoService.computeRoutes(53);
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "algo tested successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/test2")
    public ResponseEntity<ApiResponse<Order>> testAlgo2() {
        try {
            clus.getTheClusters();
            ApiResponse<Order> response = new ApiResponse<>(HttpStatus.OK.value(), "algo tested successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<Order> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
