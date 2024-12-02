package com.freighthub.core.controller;

import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.ItemListDto;
import com.freighthub.core.dto.OrderStatusDto;
import com.freighthub.core.dto.VerifyDto;
import com.freighthub.core.entity.Consigner;
import com.freighthub.core.entity.Item;
import com.freighthub.core.service.ItemService;
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
@RequestMapping("/api/item/")
public class ItemController {

    @Autowired
    private ItemService itemService;

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllItems() {
        try {
            List<?> items = itemService.getAllItems();
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all items", items);
            logger.info("Items: {}", items);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting items: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/single")
    public ResponseEntity<ApiResponse<?>> getItemById(@RequestBody GetAnyId item) {
        try {
            Item single_item = itemService.getItemById(item.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get item", single_item);
            logger.info("Item: {}", single_item.getItemName());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //get items for a purchase order
    @PostMapping("/po")
    public ResponseEntity<ApiResponse<?>> getItemsByPo(@RequestBody GetAnyId po) {
        try {
            List<?> items = itemService.getItemsByPo(po.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get items by PO", items);
            logger.info("Items: {}", items);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting items: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<?>> completeItem(@Valid @RequestBody OrderStatusDto itemDto) {
        try {
            itemService.completeItem(itemDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Item completed successfully");
            logger.info("Response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/safe-delivery")
    public ResponseEntity<ApiResponse<?>> safeDelivery(@Valid @RequestBody ItemListDto itemsDto) {
        try {
            itemService.safeDelivery(itemsDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Item safe delivery verified successfully");
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
