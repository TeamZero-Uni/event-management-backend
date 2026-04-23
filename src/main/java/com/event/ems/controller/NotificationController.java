package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.NotificationRequest;
import com.event.ems.dto.NotificationResponse;
import com.event.ems.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(@RequestBody NotificationRequest request) {
        ApiResponse<NotificationResponse> response = notificationService.createNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(Authentication auth) {
        List<NotificationResponse> notifications = notificationService.getMyNotifications(auth.getName());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Notifications fetched successfully",
                notifications,
                LocalDateTime.now()
        ));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id, Authentication auth) {
        notificationService.markAsRead(id, auth.getName());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Notification marked as read",
                null,
                LocalDateTime.now()
        ));
    }
}