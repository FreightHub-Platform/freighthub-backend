package com.freighthub.core.controller;


import com.freighthub.core.dto.VehicleDto;
import com.freighthub.core.service.RegVehicleService;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicle/register")
public class VehicleRegController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRegController.class);

    @Autowired
    private RegVehicleService regVehicleService;

    @PostMapping("/0")
    public ResponseEntity<ApiResponse<?>> updateVehicleRegistration(@Valid @RequestBody VehicleDto vehicleDto) {
        try {
            Integer vehicle_id = regVehicleService.updateRegistrationDetails(vehicleDto);
            Map<String, Object> data = new HashMap<>();
            data.put("vehicle_id", vehicle_id);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Registration details updated successfully", data);
            // Log the response object
            logger.info("Response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/1")
    public ResponseEntity<ApiResponse<?>> updateVehicleDocuments(@Valid @RequestBody VehicleDto vehicleDto) {
        try {
            regVehicleService.updateDocumentDetails(vehicleDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Vehicle documents updated successfully");
            // Log the response object
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
