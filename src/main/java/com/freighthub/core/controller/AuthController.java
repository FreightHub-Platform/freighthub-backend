package com.freighthub.core.controller;

import com.freighthub.core.dto.RegisterRequest;
import com.freighthub.core.entity.User;
import com.freighthub.core.enums.UserRole;
import com.freighthub.core.service.UserService;
import com.freighthub.core.util.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class AuthController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest);
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "User registered successfully", user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Integer> loginUser(@RequestParam UserRole role, @RequestParam int id) {
        User user = new User();
        user.setRole(role);
        user.setId(id);

        Integer completion = userService.loginCheck(user);
        if (completion != null) {
            return ResponseEntity.ok(completion);
        } else {
            return ResponseEntity.badRequest().body(-1); // Indicating an error condition with a specific value
        }
    }

}

