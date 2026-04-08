package com.event.ems.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventRequest {
    private String eventTitle;
    private String eventType;
    private String venueName;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long venueId;
    private Integer maxParticipants;
    private BigDecimal budget;
    private String description;
    private String posterUrl;
    private String status;
}