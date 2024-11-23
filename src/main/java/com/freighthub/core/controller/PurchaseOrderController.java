package com.freighthub.core.controller;

import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.OrderStatusDto;
import com.freighthub.core.entity.PurchaseOrder;
import com.freighthub.core.service.PurchaseOrderService;
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

@RestController
@RequestMapping("/api/purchase-order/")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderController.class);

    // Get all Purchase Orders
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllPurchaseOrders() {
        try {
            List<?> purchaseOrders = purchaseOrderService.getAllPurchaseOrders();
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all purchase orders", purchaseOrders);
            logger.info("PurchaseOrders: {}", purchaseOrders);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting purchase orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get a single Purchase Order by ID
    @PostMapping("/single")
    public ResponseEntity<ApiResponse<?>> getPurchaseOrderById(@RequestBody GetAnyId purchaseOrder) {
        try {
            PurchaseOrder singlePurchaseOrder = purchaseOrderService.getPurchaseOrderById(purchaseOrder.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get purchase order", singlePurchaseOrder);
            logger.info("PurchaseOrder: {}", singlePurchaseOrder.getPoNumber());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting purchase order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Mark a Purchase Order as completed
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<?>> completePurchaseOrder(@Valid @RequestBody OrderStatusDto purchaseOrderDto) {
        try {
            purchaseOrderService.completePurchaseOrder(purchaseOrderDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Purchase order completed successfully");
            logger.info("Response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/complete_force")
    public ResponseEntity<ApiResponse<?>> completePurchaseOrderForce(@Valid @RequestBody OrderStatusDto purchaseOrderDto) {
        try {
            purchaseOrderService.completePurchaseOrderForce(purchaseOrderDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Purchase order completed successfully");
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
