package com.event.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganizerResponse {
    private Long id;
    private Long userId;
    private String position;
    private String clubName;
}

