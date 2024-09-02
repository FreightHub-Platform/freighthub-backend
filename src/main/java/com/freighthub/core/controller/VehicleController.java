package com.freighthub.core.controller;


import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.VehicleDto;
import com.freighthub.core.entity.Vehicle;
import com.freighthub.core.service.VehicleService;
import com.freighthub.core.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle/")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllVehicles() {
        try {
            List<?> vehicles = vehicleService.getAllVehicles();
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all vehicles", vehicles);
            logger.info("Vehicles: {}", vehicles);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting vehicles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/verify_status")
    public ResponseEntity<ApiResponse<?>> getAllVehiclesByVerifyStatus(@RequestBody VehicleDto vehicleDto) {
        try {
            List<Vehicle> vehicles = vehicleService.getVehiclesByVerifyStatus(vehicleDto.getVerifyStatus());
            ApiResponse<List<?>> response = new ApiResponse<>(HttpStatus.OK.value(), "Get " + vehicleDto.getVerifyStatus() + " vehicles", vehicles);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting vehicles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/availability")
    public ResponseEntity<ApiResponse<?>> getVehiclesByAvailability(@RequestBody VehicleDto vehicleDto) {
        try {
            List<Vehicle> vehicles = vehicleService.getVehiclesByAvailability(vehicleDto.getAvailability());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get vehicles by availability", vehicles);
            logger.info("Vehicles: {}", vehicles);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting vehicles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/single")
    public ResponseEntity<ApiResponse<?>> getVehicleById(@RequestBody GetAnyId vehicleId) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get vehicle", vehicle);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting vehicle: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/fleet_owner")
    public ResponseEntity<ApiResponse<?>> getVehiclesByFleetOwner(@RequestBody GetAnyId fleetOwnerId) {
        try {
            List<Vehicle> vehicles = vehicleService.getVehiclesByFleetOwner(fleetOwnerId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get vehicles by fleet owner", vehicles);
            logger.info("Vehicles: {}", vehicles);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting vehicles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/driver")
    public ResponseEntity<ApiResponse<?>> getVehiclesByDriver(@RequestBody GetAnyId driverId) {
        try {
            List<Vehicle> vehicles = vehicleService.getVehiclesByDriver(driverId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get vehicles by driver", vehicles);
            logger.info("Vehicles: {}", vehicles);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting vehicles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/swap_availability")
    public ResponseEntity<ApiResponse<?>> swapAvailability(@RequestBody VehicleDto vehicle) {
        try {
            vehicleService.swapAvailability(vehicle);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Vehicle availability swapped successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error swapping vehicle availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
