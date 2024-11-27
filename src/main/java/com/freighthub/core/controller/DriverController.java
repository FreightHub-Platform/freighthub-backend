package com.freighthub.core.controller;

import com.freighthub.core.dto.DriverDto;
import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.VehicleDto;
import com.freighthub.core.entity.Driver;
import com.freighthub.core.entity.Vehicle;
import com.freighthub.core.service.DriverService;
import com.freighthub.core.util.ApiResponse;
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
@RequestMapping("/api/driver/")
public class DriverController {

    @Autowired
    private DriverService driverService;

    private static final Logger logger = LoggerFactory.getLogger(DriverController.class);

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllDrivers() {
        try {
            List<?> drivers = driverService.getAllDrivers();
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all drivers", drivers);
            logger.info("Drivers: {}", drivers);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting drivers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/verify_status")
    public ResponseEntity<ApiResponse<?>> getAllDriversByVerifyStatus(@RequestBody DriverDto driverDto) {
        try{
            List<Driver> drivers = driverService.getDriversByVerifyStatus(driverDto.getVerifyStatus());
            ApiResponse<List<?>> response = new ApiResponse<>(HttpStatus.OK.value(), "Get " + driverDto.getVerifyStatus() + " drivers", drivers);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting drivers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/availability")
    public ResponseEntity<ApiResponse<?>> getDriversByAvailability(@RequestBody DriverDto driverDto) {
        try {
            List<Driver> drivers = driverService.getDriversByAvailability(driverDto.getAvailability());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get drivers by availability", drivers);
            logger.info("Drivers: {}", drivers);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting drivers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/ownership")
    public ResponseEntity<ApiResponse<?>> getDriversByOwnership(@RequestBody DriverDto driverDto) {
        try {
            List<Driver> vehicles = driverService.getDriversByOwnership(driverDto.getOwnership());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get Drivers by vehicle ownership", vehicles);
            logger.info("Drivers: {}", vehicles);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting drivers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/single")
    public ResponseEntity<ApiResponse<?>> getDriverById(@RequestBody GetAnyId driverId) {
        try {
            Map<String, Object> driver = driverService.getDriverById(driverId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get driver", driver);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting driver: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/fleet_owner")
    public ResponseEntity<ApiResponse<?>> getDriversByFleetOwner(@RequestBody GetAnyId fleetOwnerId) {
        try {
            List<Driver> drivers = driverService.getDriversByFleetOwner(fleetOwnerId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get drivers by fleet owner", drivers);
            logger.info("Drivers: {}", drivers);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting drivers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/swap_availability")
    public ResponseEntity<ApiResponse<?>> swapAvailability(@RequestBody DriverDto driver) {
        try {
            driverService.swapAvailability(driver);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Driver availability swapped successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error swapping driver availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
