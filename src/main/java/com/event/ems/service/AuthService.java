package com.event.ems.service;

import com.event.ems.dto.auth.*;
import com.event.ems.dto.MeResponse;
import com.event.ems.exception.InvalidCredentialsException;
import com.event.ems.exception.UserNotFoundException;
import com.event.ems.factory.EmailFactory;
import com.event.ems.model.UserModel;
import com.event.ems.repo.UserRepo;
import com.event.ems.utils.JwtHelp;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtHelp jwtTokenUtil;
    private final ModelMapper mapper;
    private final EmailFactory emailFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
            throw new InvalidCredentialsException("Invalid password");
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

        boolean isValid = jwtTokenUtil.isValid(refreshToken, user, "refresh");

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
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", delete.toString());
    }

    public MeResponse authMe(String username){
        UserModel user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String batch = user.getStudentDetails() != null ? user.getStudentDetails().getBatch() : null;
        String position = user.getOrganizerDetails() != null ? user.getOrganizerDetails().getPosition() : null;
        String clubName = user.getOrganizerDetails() != null ? user.getOrganizerDetails().getClubName() : null;

        return new MeResponse(
                user.getUserId(),
                user.getUsername(),
                user.getFullname(),
                user.getAddress(),
                user.getEmail(),
                user.getPhone(),
                user.getDepartment(),
                user.getRole(),
                user.getCreatedAt(),
                batch,
                position,
                clubName
        );
    }

    public void sendOtp(ForgotPasswordRequest req){
        UserModel user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User Not found"));
        System.out.println(user.getEmail());
        System.out.println(req.getEmail());
        if(!user.getEmail().equals(req.getEmail())){
            throw new InvalidCredentialsException("Invalid email");
        }

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepo.save(user);

        Map<String, String> emailData = new HashMap<>();
        emailData.put("otp", otp);
        emailData.put("email", user.getEmail());

        emailFactory.getService("OTP").send(emailData);
    }

    public void verifyOtp(VerifyOtpReq req){
        UserModel user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!req.getOtp().equals(user.getOtp())) {
            throw new InvalidCredentialsException("Invalid OTP");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidCredentialsException("OTP expired");
        }
    }

    public void resetPassword(ResetPasswordRequest req) {

        UserModel user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));

        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepo.save(user);
    }
}
