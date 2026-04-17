package com.event.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long userId;
    private Long eventId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
