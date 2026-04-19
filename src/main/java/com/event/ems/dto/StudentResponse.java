package com.event.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String batch;
    private Long userId;
    private String username;
    private String fullname;
    private String email;
}
