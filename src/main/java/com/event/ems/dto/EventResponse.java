package com.event.ems.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    private Long id;
    private String title;
    private String description;

    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private Integer maxParticipants;
    private String posterUrl;

    private String status;
    private String type;

    private String placeName;
    private Integer capacity;

    private String fullname;
    private String email;
}