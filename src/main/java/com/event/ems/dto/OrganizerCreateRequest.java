package com.event.ems.dto;

import lombok.Data;

@Data
public class OrganizerCreateRequest {
    private String username;
    private String password;
    private String email;
    private String clubName;
    private String role;
}

