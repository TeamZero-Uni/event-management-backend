package com.event.ems.dto;

import com.event.ems.model.RegitrationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RegistrationResponse {
    private Long registrationId;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String username;
    private String email;
    private String telNumber;
    private RegitrationStatus status;
    private LocalDateTime registrationDate;
}

