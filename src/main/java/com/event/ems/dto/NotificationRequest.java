package com.event.ems.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private Long userId;
    private Long eventId;
    private String message;
}
