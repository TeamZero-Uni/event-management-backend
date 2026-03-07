package com.event.ems.service;

import com.event.ems.dto.AuthResponse;
import com.event.ems.dto.AuthRequest;
import com.event.ems.model.UserModel;
import com.event.ems.repo.UserRepo;
import com.event.ems.utils.Jwt;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final Jwt jwtTokenUtil;

    public AuthResponse login(AuthRequest req, HttpServletResponse response){

        UserModel user = userRepo.findByUsername(req.getUsername()).orElse(null);
        if (user == null) return new AuthResponse(null,null,"User not found");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
        } catch (Exception e) {
            return new AuthResponse(null,null,"Invalid username or password");
        }
        String token = jwtTokenUtil.generateToken(user);

        return new AuthResponse(token, "Login successfully", null);
    }
}
