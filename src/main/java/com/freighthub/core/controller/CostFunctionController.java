package com.freighthub.core.controller;

import com.freighthub.core.dto.CostFunctionDto;
import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.entity.CostFunction;
import com.freighthub.core.service.CostFunctionService;
import com.freighthub.core.service.ItemService;
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
@RequestMapping("/api/cost-function/")
public class CostFunctionController {

    @Autowired
    private CostFunctionService costFunctionService;

    private static final Logger logger = LoggerFactory.getLogger(CostFunctionController.class);

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllFunction() {
        try {
            CostFunction costFunction = costFunctionService.getFunction();
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all items", costFunction);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting items: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<?>> updateFunction(@RequestBody CostFunctionDto cost) {
        try {
            CostFunction costFunction = costFunctionService.updateFunction(cost);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Update function", costFunction);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error updating function: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
