package com.freighthub.core.controller;

import com.freighthub.core.dto.AssignDto;
import com.freighthub.core.dto.FleetOwnerDto;
import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.entity.FleetOwner;
import com.freighthub.core.service.FleetOwnerService;
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
@RequestMapping("/api/fleet_owner/")
public class FleetOwnerController {

    @Autowired
    FleetOwnerService fleetOwnerService;

    private static final Logger logger = LoggerFactory.getLogger(FleetOwnerController.class);

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllFleetOwners() {
        try {
            List<?> fleetOwners = fleetOwnerService.getAllFleetOwners();
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all users", fleetOwners);
            logger.info("Consigners: {}", fleetOwners);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/verify_status")
    public ResponseEntity<ApiResponse<?>> getAllFleetOwnersByVerifyStatus(@RequestBody FleetOwnerDto fleetOwnerDto) {
        try{
            List<FleetOwner> fleetOwners = fleetOwnerService.getFleetOwnersByVerifyStatus(fleetOwnerDto.getVerifyStatus());
            ApiResponse<List<?>> response = new ApiResponse<>(HttpStatus.OK.value(), "Get unverified users", fleetOwners);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting unverified users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/single")
    public ResponseEntity<ApiResponse<?>> getConsignerById(@RequestBody GetAnyId fleeto) {
        try {
            FleetOwner fleetOwner = fleetOwnerService.getFleetOwnerById(fleeto.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get user", fleetOwner);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<?>> assignDriver(@RequestBody AssignDto assign) {
        try {
            fleetOwnerService.assignDriver(assign);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Driver Assigned successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error assigning users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/de_assign")
    public ResponseEntity<ApiResponse<?>> deAssignDriver(@RequestBody AssignDto assign) {
        try {
            fleetOwnerService.deAssignDriver(assign);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Driver De-Assigned successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error de-assigning users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
