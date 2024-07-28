package com.freighthub.core.controller;

import com.freighthub.core.entity.Consigner;
import com.freighthub.core.service.ConsignerService;
import com.freighthub.core.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/consigner/")
public class ConsignerController {

    @Autowired
    private ConsignerService consignerService;

    private static final Logger logger = LoggerFactory.getLogger(ConsignerController.class);

    @GetMapping
    public ResponseEntity<List<Consigner>> getAllConsigners() {
        try {
            List<Consigner> consigners = consignerService.getAllConsigners();
            ApiResponse<List<Consigner>> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all users", consigners);
            logger.info("Consigners: {}", consigners);
            return ResponseEntity.ok(consigners);
        } catch (RuntimeException e) {
            logger.error("Error getting consigners: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/unverified")
    public ResponseEntity<List<Consigner>> getAllUnverifiedConsigners() {
        try{
            List<Consigner> consigners = consignerService.getAllUnverifiedConsigners();
            ApiResponse<List<Consigner>> response = new ApiResponse<>(HttpStatus.OK.value(), "Get unverified users", consigners);
            return ResponseEntity.ok(consigners);
        } catch (RuntimeException e) {
            logger.error("Error getting unverified consigners: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consigner> getConsignerById(@PathVariable int id) {
        try {
            Consigner consigner = consignerService.getConsignerById(id);
            ApiResponse<Consigner> response = new ApiResponse<>(HttpStatus.OK.value(), "Get user", consigner);
            logger.info("Consigner: {}", consigner.getBusinessName());
            return ResponseEntity.ok(consigner);
        } catch (RuntimeException e) {
            logger.error("Error getting consigner: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
