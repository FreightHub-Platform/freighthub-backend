package com.freighthub.core.controller;

import com.freighthub.core.dto.ConsignerDto;
import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.entity.Consigner;
import com.freighthub.core.service.ConsignerService;
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
@RequestMapping("/api/consigner/")
public class ConsignerController {

    @Autowired
    private ConsignerService consignerService;

    private static final Logger logger = LoggerFactory.getLogger(ConsignerController.class);

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllConsigners() {
        try {
            List<?> consigners = consignerService.getAllConsigners();
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all users", consigners);
            logger.info("Consigners: {}", consigners);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting consigners: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/verify_status")
    public ResponseEntity<ApiResponse<?>> getAllConsignersByVerifyStatus(@RequestBody ConsignerDto consignerDto) {
        try{
            List<Consigner> consigners = consignerService.getConsignersByVerifyStatus(consignerDto.getVerifyStatus());
            ApiResponse<List<?>> response = new ApiResponse<>(HttpStatus.OK.value(), "Get" + consignerDto.getVerifyStatus() + "users", consigners);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting consigners: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/single")
    public ResponseEntity<ApiResponse<?>> getConsignerById(@RequestBody GetAnyId consig) {
        try {
            Consigner consigner = consignerService.getConsignerById(consig.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get user", consigner);
            logger.info("Consigner: {}", consigner.getBusinessName());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting consigner: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
