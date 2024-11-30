package com.freighthub.core.controller;

import com.freighthub.core.dto.RegisterRequest;
import com.freighthub.core.entity.User;
import com.freighthub.core.service.BasicAlgoService;
import com.freighthub.core.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/algo")
public class AlgoTestController {

    @Autowired
    private BasicAlgoService basicAlgoService;

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<User>> testAlgo() {
        try {
            basicAlgoService.computeRoutes();
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "algo tested successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
