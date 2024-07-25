// src/main/java/com/freighthub/core/controller/UserController.java

package com.freighthub.core.controller;

import com.freighthub.core.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final OtpService otpService;

    @Autowired
    public UserController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/sendOtp")
    public String sendOtp(@RequestParam String phoneNumber) {
        String otp = otpService.generateOTP();
        return otpService.sendOtp(otp, phoneNumber);
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity verifyOtp(@RequestParam String otp, @RequestParam String phoneNumber) {
        boolean verificationStatus = otpService.verifyOtp(otp, phoneNumber);
        if (verificationStatus) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP or OTP expired");
        }
    }
}
