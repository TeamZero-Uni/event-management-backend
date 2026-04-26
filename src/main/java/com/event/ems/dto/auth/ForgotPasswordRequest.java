package com.event.ems.dto.auth;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String username;
    private String email;
}
