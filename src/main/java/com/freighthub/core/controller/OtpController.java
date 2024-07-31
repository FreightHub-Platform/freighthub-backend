package com.freighthub.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.freighthub.core.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("api/otpv1")
public class OtpController {

    private final OtpService otpService;
    @Autowired
    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/sendOtp")
    public ResponseEntity<?> sendOtp(@RequestParam String phoneNumber) {
        String otp = otpService.generateOTP();
        return ResponseEntity.ok(otpService.sendOtp(otp, phoneNumber));

    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<?> verifyOtp(@RequestParam String otp, @RequestParam String phoneNumber) {
        boolean verificationStatus = otpService.verifyOtp(otp, phoneNumber);
        if (verificationStatus) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP or OTP expired");
        }
    }
}
