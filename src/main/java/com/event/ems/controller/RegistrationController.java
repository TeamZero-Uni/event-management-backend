package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.EventRegRequest;
import com.event.ems.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
}
