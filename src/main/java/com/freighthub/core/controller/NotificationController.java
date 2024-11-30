package com.freighthub.core.controller;

import com.freighthub.core.dto.NotificationDto;
import com.freighthub.core.dto.OrderDto;
import com.freighthub.core.service.NotificationService;
import com.freighthub.core.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> createOrder(@RequestBody NotificationDto notificationDto) {
        try{
            notificationService.addNotification(notificationDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Notification Saved Successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/mark-as-read")
    public ResponseEntity<ApiResponse<?>> markAsRead(@RequestBody NotificationDto notificationDto) {
        try{
            notificationService.markAsRead(notificationDto.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Notification marked as read");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/mark-as-unread")
    public ResponseEntity<ApiResponse<?>> markAsUnread(@RequestBody NotificationDto notificationDto) {
        try{
            notificationService.markAsUnread(notificationDto.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Notification marked as unread");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/user")
    public ResponseEntity<ApiResponse<?>> getNotificationsForUser(@RequestBody NotificationDto notificationDto) {
        try{
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all notifications for user", notificationService.getNotificationsForUser(notificationDto.getUserId()));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
