package com.event.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private String name;
    private String email;
    private String tel;
    private String department;
    private String batch;
    private String tgNumber;
    private String avatar;
}