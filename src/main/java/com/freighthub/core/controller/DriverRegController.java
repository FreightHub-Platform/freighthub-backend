package com.freighthub.core.controller;

import com.freighthub.core.dto.DriverDto;
import com.freighthub.core.dto.VerifyDto;
import com.freighthub.core.service.RegDriverService;
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

@RestController
@RequestMapping("/api/driver/register")
public class DriverRegController {
    private static final Logger logger = LoggerFactory.getLogger(DriverRegController.class);

    @Autowired
    private RegDriverService regDriverService;

    @PostMapping("/0")
    public ResponseEntity<ApiResponse<?>> updatePersonalDetails(@Valid @RequestBody DriverDto driverDto) {
        try {
            regDriverService.updatePersonalDetails(driverDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Personal details updated successfully");
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
    public ResponseEntity<ApiResponse<?>> updateDocumentDetails(@Valid @RequestBody DriverDto driverDto) {
        try {
            regDriverService.updateDocumentDetails(driverDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Document details updated successfully");
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

    @PostMapping("/2")
    public ResponseEntity<ApiResponse<?>> updateVehicleDetails(@Valid @RequestBody DriverDto driverDto) {
        try {
            regDriverService.updateVehicleDetails(driverDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Vehicle details updated successfully");
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

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<?>> updateDriver(@Valid @RequestBody DriverDto driverDto) {
        try {
            regDriverService.updateDriver(driverDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Driver updated successfully");
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

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyDriver(@Valid @RequestBody VerifyDto driverDto) {
        try {
            regDriverService.verifyDriver(driverDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Driver verified successfully");
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

    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<?>> rejectDriver(@Valid @RequestBody VerifyDto driverDto) {
        try {
            regDriverService.rejectDriver(driverDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Driver rejected successfully");
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
