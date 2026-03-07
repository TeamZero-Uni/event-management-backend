package com.event.ems.dto;

import lombok.Data;

@Data
public class StudentDto {
    private String username;
    private String password;
    private String fullname;
    private String address;
    private String email;
    private String phone;
    private String department;
    private String batch;
}
