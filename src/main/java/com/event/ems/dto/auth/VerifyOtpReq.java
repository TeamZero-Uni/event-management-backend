package com.event.ems.dto.auth;

import lombok.Data;

@Data
public class VerifyOtpReq {
    private String otp;
    private String username;
}
