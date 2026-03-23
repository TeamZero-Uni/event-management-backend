package com.event.ems.dto;

import com.event.ems.model.OrganizersModel;
import com.event.ems.model.Role;
import com.event.ems.model.StudentModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MeResponse {
    private Long userId;
    private String username;
    private String fullname;
    private String address;
    private String email;
    private String phone;
    private String department;
    private Role role;
    private LocalDateTime createdAt;

    private String batch;

    private String position;
    private String clubName;
}
