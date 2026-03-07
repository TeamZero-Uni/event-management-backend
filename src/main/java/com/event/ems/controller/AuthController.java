package com.event.ems.controller;

import com.event.ems.dto.AuthResponse;
import com.event.ems.dto.AuthRequest;
import com.event.ems.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req, HttpServletResponse res){
        AuthResponse response = authService.login(req, res);
        if(response.getError() != null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
