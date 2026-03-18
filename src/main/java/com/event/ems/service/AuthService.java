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

        user.setToken(refreshToken);
        userRepo.save(user);

        ResponseCookie cookie =  ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return new AuthResponse(
                accessToken,
                user.getUsername()
        );
    }

    public AuthResponse refreshToken(String refreshToken){
        if(refreshToken == null ||  refreshToken.isEmpty()){
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        String username = jwtTokenUtil.extractUsername(refreshToken);

        UserModel user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        boolean isValid = jwtTokenUtil.isValid(refreshToken, user);

        if(!isValid) throw new InvalidCredentialsException("Invalid refresh token");

        String accessToken = jwtTokenUtil.generateAccessToken(user);

        return new AuthResponse(
                accessToken,
                user.getUsername()
        );
    }

    public void logout(String refreshToken, HttpServletResponse response){

        try {
            if (refreshToken != null && !refreshToken.isEmpty()) {

                String username = jwtTokenUtil.extractUsername(refreshToken);

                UserModel user = userRepo.findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

                if (refreshToken.equals(user.getToken())) {
                    user.setToken(null);
                    userRepo.save(user);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        ResponseCookie delete = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // 👉 true in production (HTTPS)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", delete.toString());
    }
}
