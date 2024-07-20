package com.freighthub.core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

        @GetMapping("/dashboard")
        public String getDashboard() {
            return "This is the admin dashboard!";
        }

        @GetMapping("/settings")
        public String getSettings() {
            return "This is the admin settings page!";
        }

        @GetMapping("/users")
        public String getUsers() {
            return "This is the admin users page!";
        }

        @GetMapping("/products")
        public String getProducts() {
            return "This is the admin products page!";
        }

        @GetMapping("/orders")
        public String getOrders() {
            return "This is the admin orders page!";
        }

        @GetMapping("/reports")
        public String getReports() {
            return "This is the admin reports page!";
        }

        @GetMapping("/logout")
        public String logout() {
            return "You have been logged out!";
        }
}
