package com.freighthub.core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/public")
public class CoreBackendController {

    @GetMapping
    public ResponseEntity<String> getPublicData() {
        return ResponseEntity.ok("This is Public data from core backend!");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {
        return ResponseEntity.ok("This is the core backend dashboard!");
    }
}
