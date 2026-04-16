package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.EventRegRequest;
import com.event.ems.dto.RegistrationResponse;
import com.event.ems.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/event/")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> eventRegister(@RequestBody EventRegRequest request) {
        System.out.println("REQUEST: " + request);
        registerService.registerEvent(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Register successful", null, LocalDateTime.now())
        );
    }

    @GetMapping("/register/all")
    public ResponseEntity<ApiResponse<List<RegistrationResponse>>> getAllRegistrations() {
        List<RegistrationResponse> registrations = registerService.getAllRegistrations();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Registrations fetched successfully", registrations, LocalDateTime.now())
        );
    }
}
