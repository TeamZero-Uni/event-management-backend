package com.event.ems.dto;

import com.event.ems.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String fullname;
    private String address;
    private String email;
    private String phone;
    private String department;
    private Role role;
    private LocalDateTime createdAt;
}

