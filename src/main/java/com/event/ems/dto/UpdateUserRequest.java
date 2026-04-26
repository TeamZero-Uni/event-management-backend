package com.event.ems.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String fullname;
    private String name;
    private String email;
    private String address;
    private String location;
    private String phone;
    private String tel;
}

