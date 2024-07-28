package com.freighthub.core.controller;

import com.freighthub.core.dto.ConsignerDto;
import com.freighthub.core.dto.RegisterRequest;
import com.freighthub.core.entity.Consigner;
import com.freighthub.core.entity.User;
import com.freighthub.core.service.RegConsignerService;
import com.freighthub.core.service.UserService;
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
@RequestMapping("/api/consigner/register")
public class ConsignerRegController {

    private static final Logger logger = LoggerFactory.getLogger(ConsignerRegController.class);

    @Autowired
    private RegConsignerService regConsignerService;

    @PostMapping("/0")
    public ResponseEntity<ApiResponse<?>> updateBusinessDetails(@Valid @RequestBody ConsignerDto consignerDto) {
        try {
            regConsignerService.updateBusinessDetails(consignerDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Business updated successfully");
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
    public ResponseEntity<ApiResponse<?>> updateContactDetails(@Valid @RequestBody ConsignerDto consignerDto) {
        try {
            regConsignerService.updateContactDetails(consignerDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Business updated successfully");
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
    public ResponseEntity<ApiResponse<?>> updateLocationDetails(@Valid @RequestBody ConsignerDto consignerDto) {
        try {
            regConsignerService.updateLocationDetails(consignerDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Business updated successfully");
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
    public ResponseEntity<ApiResponse<?>> verifyConsigner(@Valid @RequestBody ConsignerDto consignerDto) {
        try {
            regConsignerService.verifyConsigner(consignerDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Consigner verified successfully");
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



