package com.event.ems.dto;

import com.event.ems.model.EventStatus;
import com.event.ems.model.EventType;
import lombok.Data;

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
    private String description;
    private String posterUrl;
    private String status;
}