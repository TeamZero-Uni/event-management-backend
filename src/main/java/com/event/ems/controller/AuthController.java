package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.AuthResponse;
import com.event.ems.dto.AuthRequest;
import com.event.ems.dto.MeResponse;
import com.event.ems.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest req, HttpServletResponse res){
        AuthResponse auth = authService.login(req, res);
        return ResponseEntity.ok(new ApiResponse<>(true,"Login successful", auth, LocalDateTime.now()));
    }

    @PostMapping("refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken){
        AuthResponse auth = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new ApiResponse<>(true,"Refresh successful", auth, LocalDateTime.now()));
    }

    @PostMapping("logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse res
    ){
        authService.logout(refreshToken, res);
        return ResponseEntity.ok(new ApiResponse<>(true,"Logout successful",null, LocalDateTime.now()));
    }

    @GetMapping("me")
    public ResponseEntity<ApiResponse<MeResponse>> me(Authentication auth) {
            MeResponse me = authService.authMe(auth.getName());
            return ResponseEntity.ok(new ApiResponse<>(true, "Fetching successful", me, LocalDateTime.now()));
    }
}
