package com.event.ems.dto;

import lombok.Data;

@Data
public class VenueRequest {
    private String placeName;
    private Integer capacity;
    private Boolean isAvailable;
}

