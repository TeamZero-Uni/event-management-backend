package com.event.ems.service;

import com.event.ems.dto.AuthResponse;
import com.event.ems.dto.AuthRequest;
import com.event.ems.exception.InvalidCredentialsException;
import com.event.ems.exception.UserNotFoundException;
import com.event.ems.model.UserModel;
import com.event.ems.repo.UserRepo;
import com.event.ems.utils.JwtHelp;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtHelp jwtTokenUtil;

    public AuthResponse login(AuthRequest req, HttpServletResponse response){

        UserModel user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getUsername(),
                            req.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String accessToken = jwtTokenUtil.generateAccessToken(user);
        String refreshToken = jwtTokenUtil.generateReferenceToken(user);

        user.setToken(accessToken);
        userRepo.save(user);

        ResponseCookie cookie =  ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lux")
                .build();
        response.addHeader("set-cookie", cookie.toString());

        return new AuthResponse(
                accessToken,
                user.getUsername()
        );
    }
}
