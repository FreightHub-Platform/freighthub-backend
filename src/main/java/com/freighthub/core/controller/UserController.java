// src/main/java/com/freighthub/core/controller/UserController.java

package com.freighthub.core.controller;

import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.UserDetailsDto;
import com.freighthub.core.service.UserService;
import com.freighthub.core.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all system users from the user entity
//       @GetMapping("/all")
//       public ResponseEntity<ApiResponse<?>> getAllUsers(){
//           try{
//               ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all Users", userService.getAllUsers());
//               return ResponseEntity.ok()
//                       .contentType(MediaType.APPLICATION_JSON)
//                         .body(response);
//           } catch (RuntimeException e) {
//               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//           }
//       }

    // Get a user by id
    @PostMapping("/id")
    public ResponseEntity<ApiResponse<?>> getUserById(@RequestBody Long user) {
        System.out.println("User id: " + user);
        try {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get user by id", userService.getUserById(user));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GetMapping("/all")
//    public ResponseEntity<List<UserDetailsDto>> getAllUsers() {
//        List<UserDetailsDto> userDetails = userService.getAllUserDetails();
//        return ResponseEntity.ok(userDetails);
//    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        try {
            List<UserDetailsDto> userDetails = userService.getAllUserDetails();
            ApiResponse<List<UserDetailsDto>> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all users", userDetails);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
