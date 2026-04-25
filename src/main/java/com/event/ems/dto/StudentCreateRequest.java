package com.event.ems.dto;

import lombok.Data;

@Data
public class StudentCreateRequest {
    private String username;
    private String password;
    private String department;
    private String email;
    private Integer batch;
    private Integer year;
    private String role;
    private String fullname;
}

