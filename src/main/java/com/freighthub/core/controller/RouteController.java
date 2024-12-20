package com.freighthub.core.controller;

import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.OrderStatusDto;
import com.freighthub.core.dto.RouteDetailsDto;
import com.freighthub.core.entity.Route;
import com.freighthub.core.service.RouteService;
import com.freighthub.core.util.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/route/")
public class RouteController {

    private static final Logger logger = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private RouteService routeService;

    // Get all Routes
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllRoutes() {
        try {
            List<Route> routes = routeService.getAllRoutes();
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Routes Retrieved", routes);
            logger.info("Response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Get a single Route by ID - for mobile claim amount
    @PostMapping("/single")
    public ResponseEntity<ApiResponse<?>> getRouteById(@Valid @RequestBody OrderStatusDto routeDto) {
        try {
            Route route = routeService.getRouteById(routeDto.getRoute_id());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Route Retrieved", route);
            logger.info("Response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //Update a route status - accept, arriving, loading, ongoing
    @PostMapping("/update-status")
    public ResponseEntity<ApiResponse<?>> updateRoute(@Valid @RequestBody OrderStatusDto routeDto) {
        try {
            routeService.updateRouteStatuses(routeDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Route Status Updated to " + routeDto.getStatus());
            logger.info("Response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Get all Routes details for mobile (an overview) of route-po statuses
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

    // Get full details about a route before undertaking it
    @PostMapping("/full-details")
    public ResponseEntity<ApiResponse<?>> getRouteDetails(@Valid @RequestBody GetAnyId routeId) {
        try {
            RouteDetailsDto details = routeService.getRouteDetails(routeId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Route details Retrieved", details);
            logger.info("Response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //get routes matching for driver (load board)
    @PostMapping("/driver")
    public ResponseEntity<ApiResponse<?>> getDriverRoutes(@Valid @RequestBody GetAnyId driverId) {
        try {
            List<Object> routes = routeService.getDriverRoutes(driverId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Routes Retrieved", routes);
            logger.info("Response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //get assigned routes for driver so far
    @PostMapping("/assigned")
    public ResponseEntity<ApiResponse<?>> getAssignedRoutes(@Valid @RequestBody GetAnyId driverId) {
        try {
            List<Object> routes = routeService.getAssignedRoutes(driverId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Routes Retrieved", routes);
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
