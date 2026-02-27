package com.event.ems.dto;

import com.event.ems.model.Role;
import lombok.Data;

@Data
public class AuthRequest {
    private Long userId;
    private String username;
    private String password;

    private Role role;

    private String department;
    private String batch;

    private String fullName;
    private String email;
    private String phone;
    private String address;


}
