package com.freighthub.core.controller;

import com.freighthub.core.dto.OrderStatusDto;
import com.freighthub.core.service.RouteService;
import com.freighthub.core.util.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/route/")
public class RouteController {

    private static final Logger logger = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private RouteService routeService;

    // Get all Routes

    // Get a single Route by ID

    //Accept a route
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<?>> acceptRoute(@Valid @RequestBody OrderStatusDto routeDto) {
        try {
            routeService.acceptRouteAndUpdatePoStatus(routeDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Route Accepted");
            logger.info("Response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Get all Routes details for mobile
    @PostMapping("/status-mobile")
    public ResponseEntity<ApiResponse<?>> getRouteStatus(@Valid @RequestBody OrderStatusDto routeDto) {
        try {
            List<Map<String, Object>> routeStatuses = routeService.getRouteDetailsWithItemSequence(routeDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Route Statuses Retrieved", routeStatuses);
            logger.info("Response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }



}
